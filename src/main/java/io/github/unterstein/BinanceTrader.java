package io.github.unterstein;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.market.OrderBook;
import io.github.unterstein.statistic.TrendAnalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.github.unterstein.remoteManagment.ManagementConstants.shutDown;
import static java.lang.Thread.sleep;

@Component
public class BinanceTrader {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);

    private TradingClient client;
    private TrendAnalizer trendAnalizer;

    private  double tradeDifference;
    private  double tradeProfit;
    private  int tradeAmount;

    private Double currentBoughtPrice;
    private Long orderId;
    private int panicBuyCounter;
    private int panicSellCounter;
    private double trackingLastPrice;
    private OrderBook orderBook;


    private int lastKnownTradingBalance;
    private double lastBid;
    private double lastAsk;
    private double profitablePrice;

    private String tradeCurrency;
    private String baseCurrency;
    private double antiBurstValue;
    private double antiBurstPercentage;
    private double boughtPrice;
    private double goalSellPrice;
    private double goalBuyPrice;


    @Autowired
    BinanceTrader(TradingClient client, TrendAnalizer trendAnalizer) {
        trackingLastPrice = client.lastPrice();
        this.client = client;
        this.trendAnalizer = trendAnalizer;
        this.tradeCurrency = client.getTradeCurrency();
        this.baseCurrency = client.getBaseCurrency();
        clear();
    }

    public void setClient(TradingClient client) {
        this.client = client;
    }

    public void setTrackingLastPrice(double trackingLastPrice) {
        this.trackingLastPrice = trackingLastPrice;
    }

    public void setTradeDifference(double tradeDifference) {
        this.tradeDifference = tradeDifference;
    }

    public void setTradeProfit(double tradeProfit) {
        this.tradeProfit = tradeProfit;
    }

    public void setTradeAmount(int tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    void tick() {


        double lastPrice = 0;
        try {
            getLatestOrderBook();
            lastPrice = client.lastPrice();
            AssetBalance tradingBalance = client.getTradingBalance();
            lastBid = getLastBid();
            lastAsk = getLastAsk();
            profitablePrice = lastBid + (lastBid * tradeProfit / 100);
            antiBurstValue = lastAsk - profitablePrice;
            antiBurstPercentage = antiBurstValue / lastAsk * 100.0;


            double burstDetectionDifference = lastAsk - profitablePrice;
            logger.info(String.format("bid:%.8f ask:%.8f price:%.8f profitablePrice:%.8f diff:%.8f\n  ",
                    lastBid, lastAsk, lastPrice, profitablePrice, burstDetectionDifference));
            checkShutDown();
            if (isFall() && trendAnalizer.isUpTrend()) {//Relocate to Conditions enum
                logger.info("Fall burst detected");
                goalBuyPrice = lastAsk - (lastAsk * 0.3 / 100);
                int rightMomentCounter = 0;
                while (lastAsk > goalBuyPrice){
                    logger.info(String.format("waiting price to fall enough, difference %.8f, Last minAsk: %.8f, goalSellPrice: %.8f\"",
                            lastAsk - goalBuyPrice, lastAsk, goalBuyPrice));
                    sleepSeconds(1);
                    lastAsk = getLastAsk();
                    rightMomentCounter++;
                    if (rightMomentCounter == 20) {
                        logger.info("Price did not fall enough, out!");
                        return;
                    }
                }
                client.buyMarket(tradeAmount);// TODO lastAsk amount
                lastKnownTradingBalance = tradeAmount;
                logger.info(String.format("Bought %d coins to market! at %.8f rate", tradeAmount, lastAsk));
                boughtPrice = lastAsk;
                goalSellPrice = boughtPrice + (boughtPrice * 0.2 / 100);
                while (lastBid < goalSellPrice) {
                    logger.info(String.format("waiting price to rise enough, difference %.8f, Last maxBid: %.8f, goalSellPrice: %.8f\"",
                            goalSellPrice - lastBid, lastBid, goalSellPrice));
                    sleepSeconds(3);
                    lastBid = getLastBid();
                    rightMomentCounter++;
                    //if bid is too low - set limit order
                    if (rightMomentCounter == 180) {
                        client.setLimitOrder(lastKnownTradingBalance, goalSellPrice);
                        logger.info(String.format("Set limit order %d coins to market! Rate: %.8f", lastKnownTradingBalance, goalSellPrice));
                        return;
                    }
                }
                client.sellMarket(tradeAmount);
                logger.info(String.format("Sold %d coins to market! Rate: %.8f", tradeAmount, lastBid));
                logger.info(String.format("Profit %.8f", (boughtPrice - lastBid) * tradeAmount));
            } else {

                logger.info(String.format("No profit detected, difference %.8f %.3f percent\n", antiBurstValue, antiBurstPercentage));
            }

        } catch (Exception e) {
            logger.error("Unable to perform ticker", e);
        } finally {
            trackingLastPrice = lastPrice;
            trendAnalizer.setCurrentPrice(lastPrice);
        }
    }


    private void getLatestOrderBook() {
        orderBook = client.getOrderBook();
    }

    private Double getLastAsk() {
        getLatestOrderBook();
        return Double.valueOf(orderBook.getAsks().get(0).getPrice());
    }

    private Double getLastBid() {
        getLatestOrderBook();
        return Double.valueOf(orderBook.getBids().get(0).getPrice());
    }

    private boolean isFall() {
        return antiBurstPercentage < -0.8;
    }

    private void sleepSeconds(int seconds) {
        try {
            sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkShutDown() {
        if (shutDown) {
            logger.info("\n\nShutting down!\n\n");
            System.exit(0);
        }
    }

    private boolean isNew(OrderStatus status) {
        return status == OrderStatus.NEW;
    }

    private boolean notCanceled(OrderStatus status) {
        return status != OrderStatus.CANCELED;
    }

    private boolean isPriceAscending(double lastPrice) {
        return lastPrice > trackingLastPrice;
    }

    private boolean isBurst() {
        return lastAsk >= profitablePrice;
    }

    private boolean noOrders() {
        return orderId == null;
    }

    private void panicSellForCondition(double lastPrice, double lastKnownTradingBalance, boolean condition) {
        if (condition) {
            logger.info("panicSellForCondition");
            client.panicSell(lastKnownTradingBalance, lastPrice);
            clear();
        }
    }

    private void clear() {
        panicBuyCounter = 0;
        panicSellCounter = 0;
        orderId = null;
        currentBoughtPrice = null;
    }

    List<AssetBalance> getBalances() {
        return client.getBalances();
    }
}
