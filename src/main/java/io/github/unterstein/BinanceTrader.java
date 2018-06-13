package io.github.unterstein;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.market.OrderBook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.github.unterstein.remoteManagment.ManagementConstants.shutDown;
import static java.lang.Thread.sleep;

public class BinanceTrader {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);

    private TradingClient client;
    private final double tradeDifference;
    private final double tradeProfit;
    private final int tradeAmount;

    private Double currentBoughtPrice;
    private Long orderId;
    private int panicBuyCounter;
    private int panicSellCounter;
    private double trackingLastPrice;
    private OrderBook orderBook;


    private double lastKnownTradingBalance;
    private double lastBid;
    private double lastAsk;
    private double buyPrice;
    private double sellPrice;
    private double profitablePrice;

    private String tradeCurrency;
    private String baseCurrency;
    private double antiBurstValue;
    private double antiBurstPercentage;
    private double boughtPrice;
    private double goalSellPrice;

    private static double[] trend = new double[200];
    private static int pointer = 0;

    BinanceTrader(double tradeDifference, double tradeProfit, int tradeAmount, String baseCurrency, String tradeCurrency, String key, String secret) {
        client = new TradingClient(baseCurrency, tradeCurrency, key, secret);
        trackingLastPrice = client.lastPrice();
        this.tradeCurrency = tradeCurrency;
        this.baseCurrency = baseCurrency;
        this.tradeAmount = tradeAmount;
        this.tradeProfit = tradeProfit;
        this.tradeDifference = tradeDifference;
        clear();
    }

    public void setClient(TradingClient client) {
        this.client = client;
    }

    public void setTrackingLastPrice(double trackingLastPrice) {
        this.trackingLastPrice = trackingLastPrice;
    }

    void tick() {

        double lastPrice = 0;
        try {
            getLatestOrderBook();
            lastPrice = client.lastPrice();
            AssetBalance tradingBalance = client.getTradingBalance();
            lastKnownTradingBalance = client.getAllTradingBalance();
            lastBid = getLastBid();
            lastAsk = getLastAsk();
            profitablePrice = lastBid + (lastBid * tradeProfit / 100);
            antiBurstValue = lastAsk - profitablePrice;
            antiBurstPercentage = antiBurstValue / lastAsk * 100.0;


            double burstDetectionDifference = lastAsk - profitablePrice;
            logger.info(String.format("bid:%.8f ask:%.8f price:%.8f profitablePrice:%.8f diff:%.8f\n  ",
                    lastBid, lastAsk, lastPrice, profitablePrice, burstDetectionDifference));
            checkShutDown();
            if (isFall() && isUpTrend()) {
                logger.info("Fall burst detected");
                int rightMomentCounter = 0;
                client.buyMarket(tradeAmount);
                logger.info(String.format("Bought %d coins to market! at %.8f rate", tradeAmount, lastAsk));
                boughtPrice = lastAsk;
                goalSellPrice = boughtPrice + (boughtPrice * 0.2 / 100);
                while (lastBid < goalSellPrice) {
                    logger.info(String.format("waiting price to rise enough, difference %.8f, Last maxBid: %.8f, goalSellPrice: %.8f\"", goalSellPrice - lastBid, lastBid, goalSellPrice));
                    sleepSeconds(3);
                    lastBid = getLastBid();
                    rightMomentCounter++;
                    if (rightMomentCounter == 180) {
                        break;
                    }
                }
                client.sellMarket(tradeAmount);
                logger.info(String.format("Sold %d coins to market! Rate: %.8f", tradeAmount, lastBid));
                logger.info(String.format("Profit %.8f", (boughtPrice - lastBid) * tradeAmount));

//      } else if(isBurst()){
//
//        client.buyMarket(tradeAmount);
//        boughtPrice = lastAsk;
//        logger.info(String.format("Bought %d coins to market! at %.8f rate", tradeAmount, boughtPrice));
//        goalSellPrice = boughtPrice + (boughtPrice * 0.2 / 100);
//        int rightMomentCounter = 0;
//        while (lastBid < goalSellPrice){
//          logger.info(String.format("waiting price to rise enough, difference %.8f, Last maxBid: %.8f, goalSellPrice: %.8f\"", goalSellPrice - lastBid, lastBid, goalSellPrice));
//          sleepSeconds(3);
//          lastBid = getLastBid();
//          rightMomentCounter++;
//          if (rightMomentCounter == 20){
//            break;
//          }
//        }
//        client.sellMarket(tradeAmount);
//        logger.info(String.format("Sold %d coins to market! Rate: %.8f", tradeAmount, lastBid));
//        logger.info(String.format("Profit %.8f", (boughtPrice - lastBid)*tradeAmount));
            } else {

                logger.info(String.format("No profit detected, difference %.8f %.3f percent\n", antiBurstValue, antiBurstPercentage));
            }

        } catch (Exception e) {
            logger.error("Unable to perform ticker", e);
        } finally {
            trackingLastPrice = lastPrice;
            trend[pointer++] = lastPrice;
            if (pointer == 200) {
                pointer = 0;
            }
        }
    }

    private boolean isUpTrend() {
        if (trend[pointer] > trend[0]) {
            logger.info("Up-trend detected");
            return true;
        }
        logger.info("Down-trend detected");
        return false;
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
