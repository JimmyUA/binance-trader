package io.github.unterstein;


import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.market.OrderBook;
import io.github.unterstein.statistic.TrendAnalizer;
import io.github.unterstein.tasks.BuyTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.unterstein.remoteManagment.ManagementConstants.shutDown;
import static java.lang.Thread.sleep;
import static util.Slepper.sleepSeconds;

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
    private double lastTrakingAsk;
    private double profitablePrice;

    private String tradeCurrency;
    private String baseCurrency;
    private double antiBurstValue;
    private double antiBurstPercentage;
    private double boughtPrice;
    private double goalSellPrice;
    private double goalBuyPrice;
    private double stopLossPrice;


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
            client.getLatestOrderBook();
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
            if (isFall() && trendAnalizer.isUptrendByAsk(lastAsk)) {//Relocate to Conditions enum
                logger.info("Fall burst detected");
//                goalBuyPrice = lastTrakingAsk - (lastTrakingAsk * 0.2 / 100);
                int rightMomentCounter = 0;
//                while (lastAsk > goalBuyPrice){
//                    logger.info(String.format("waiting price to fall enough, difference %.8f, Last minAsk: %.8f, goalSellPrice: %.8f\"",
//                            lastAsk - goalBuyPrice, lastAsk, goalBuyPrice));
//                    sleepSeconds(1);
//                    lastAsk = getLastAsk();
//                    rightMomentCounter++;
//                    if (rightMomentCounter == 20) {
//                        logger.info("Price did not fall enough, out!");
//                        return;
//                    }
//                }
                NewOrderResponse order = client.buyMarket(tradeAmount);// TODO lastAsk amount
                lastKnownTradingBalance = tradeAmount;
                logger.info(String.format("Bought %d coins from market! at %.8f rate", tradeAmount, lastAsk));
                boughtPrice = lastAsk;
                BuyTask buyTask = new BuyTask();
                buyTask.setBoughtPrice(boughtPrice)
                        .setTradeAmount(tradeAmount);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(buyTask);
            } else {

                logger.info(String.format("No profit detected, difference %.8f %.3f percent\n", antiBurstValue, antiBurstPercentage));
            }

        } catch (Exception e) {
            logger.error("Unable to perform ticker", e);
        } finally {
            trackingLastPrice = lastPrice;
            lastTrakingAsk = lastAsk;
        }
    }

    private double getLastAsk() {
        return client.getLastAsk();
    }

    private double getLastBid() {
        return client.getLastBid();
    }


    private boolean isFall() {
        return antiBurstPercentage < -0.75;
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

    public double getBoughtPrice(NewOrderResponse order) {
        orderId = order.getOrderId();
        String price = client.getOrder(orderId).getStopPrice();
        return Double.parseDouble(price);
    }
}
