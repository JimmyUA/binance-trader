package io.github.unterstein;


import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.market.OrderBook;
import io.github.unterstein.decision.BuyDecisionMaker;
import io.github.unterstein.decision.SellDecisionMaker;
import io.github.unterstein.statistic.TrendAnalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.github.unterstein.remoteManagment.ManagementConstants.shutDown;
import static io.github.unterstein.remoteManagment.ManagementConstants.stopTicker;
import static util.Slepper.sleepSeconds;

@Component
public class BinanceTrader {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);

    private TradingClient client;
    private TrendAnalizer trendAnalizer;

    private double tradeDifference;
    private double tradeProfit;
    private int tradeAmount;

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
    private BuyDecisionMaker buyDecisionMaker;

    @Autowired
    private SellDecisionMaker sellDecisionMaker;
    private boolean wasOverGoalPrice = false;

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
        if (lastKnownTradingBalance > tradeAmount * 3) {
            logger.info("Sold enough already1 Lets sell first");
            return;
        }
        try {
            client.getLatestOrderBook();
            lastPrice = client.lastPrice();
            AssetBalance tradingBalance = client.getTradingBalance();
            updateLastBid();
            lastAsk = getLastAsk();
            profitablePrice = lastBid + (lastBid * tradeProfit / 100);
            antiBurstValue = lastAsk - profitablePrice;
            antiBurstPercentage = antiBurstValue / lastAsk * 100.0;


            double burstDetectionDifference = lastAsk - profitablePrice;
            logger.info(String.format("bid:%.8f ask:%.8f price:%.8f profitablePrice:%.8f diff:%.8f\n  ",
                    lastBid, lastAsk, lastPrice, profitablePrice, burstDetectionDifference));
            checkShutDown();
            if (isFall() && isRightMomentToBuy()) {//Relocate to Conditions enum

                executePurchase();
                sellingProcess();

            } else if (isGoingToBeTurnUpByRSI()) {

                executePurchase();
                sellingProcess();

            } else {

                logger.info(String.format("No profit detected, difference %.8f %.3f percent\n", antiBurstValue, antiBurstPercentage));
            }

        } catch (Exception e) {
            logger.error("Unable to perform ticker", e);
            sellToMarket(lastBid);
        } finally {
            trackingLastPrice = lastPrice;
            lastTrakingAsk = lastAsk;
        }
    }

    private boolean isGoingToBeTurnUpByRSI() {
        return buyDecisionMaker.isGoingToBeTurnUpByRSI();
    }

    private void sellingProcess() {
        int rightMomentCounter = 0;
        goalSellPrice = boughtPrice + (boughtPrice * 0.002);
        while (notAMomentToSell()) {
            sleepSeconds(3);
            updateLastBid();
            if (lastBid > goalSellPrice){
                wasOverGoalPrice = true;
            }
            logger.info(String.format("Market in Up-trend still, difference %.8f, Last maxBid: %.8f, goalSellPrice: %.8f\"",
                    goalSellPrice - lastBid, lastBid, goalSellPrice));
            if (stopTicker){
                return;
            }
        }
        if (wasOverGoalPrice){
            logger.info("Trend changed and price was over goal price!");
            sellToMarket(lastBid);
            return;
        }
        goalSellPrice = goalSellPrice + (0.002 * goalSellPrice);
        stopLossPrice = boughtPrice - (boughtPrice * 0.015);
        while (true) {
            logger.info("Trend changed and price did not rise enough, waiting");
            sleepSeconds(3);
            updateLastBid();
            logger.info(String.format("Waiting while reach goal price, difference %.8f, Last maxBid: %.8f, goalSellPrice: %.8f\"",
                    goalSellPrice - lastBid, lastBid, goalSellPrice));
            if (lastBid > goalSellPrice) {
                logger.info("price is high enough");
                sellToMarket(lastBid);
                sleepSeconds(180);
                break;
            } else if (lastBid < stopLossPrice && sellDecisionMaker.isTooDangerous()) {
                logger.info(String.format("Too dangerous too keep holding coins lastBid: %.8f lower than stop loss: %.8f and price keep fail", lastBid, stopLossPrice));
                sellToMarket(lastBid);
            }
            if (stopTicker){
                return;
            }
        }

    }


    private void executePurchase() {
        logger.info("Fall burst detected");

        client.buyMarket(tradeAmount);// TODO lastAsk amount
        lastKnownTradingBalance = tradeAmount;
        logger.info(String.format("Bought %d coins from market! at %.8f rate", tradeAmount, lastAsk));
        boughtPrice = lastAsk;
    }

    private boolean isRightMomentToBuy() {
        return buyDecisionMaker.isRightMomentToBuy(lastAsk);
    }

    private void updateLastBid() {
        lastBid = getLastBid();
    }

    private boolean notAMomentToSell() {
        return !sellDecisionMaker.isRightMomentToSell(lastBid);
    }

    private double getLastAsk() {
        return client.getLastAsk();
    }

    private double getLastBid() {
        return client.getLastBid();
    }


    private boolean isFall() {
        return antiBurstPercentage < -0.85;
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

    private void sellToMarket(Double lastBid) {
        client.sellMarket(tradeAmount);
        logger.info(String.format("Sold %d coins to market! Rate: %.8f", tradeAmount, lastBid));
        logger.info(String.format("Profit %.8f", (boughtPrice - lastBid) * tradeAmount));
    }
}
