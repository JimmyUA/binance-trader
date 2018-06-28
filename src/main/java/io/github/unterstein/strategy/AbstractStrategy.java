package io.github.unterstein.strategy;

import io.github.unterstein.TradingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractStrategy implements Strategy {

    protected static Logger logger = LoggerFactory.getLogger(Strategy.class);

    protected boolean isBought = false;

    @Autowired
    protected TradingClient client;

    protected int tradeAmount;
    protected Double boughtPrice;
    protected Double goalSellPrice;
    protected Double lastBid;
    protected boolean wasOverGoalPrice = false;
    protected double stopLossPrice;


    @Override
    public boolean isBought() {
        return isBought;
    }

    @Override
    public void setTradeAmount(int tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    protected double getLastBid() {
        return client.getLastBidsAverage(tradeAmount, 3);
    }

    protected void updateLastBid() {
        lastBid = getLastBid();
    }

    protected void sellToMarket(Double lastBid) {
        client.sellMarket(tradeAmount);
        logger.info(String.format("Sold %d coins to market! Rate: %.8f", tradeAmount, lastBid));
        logger.info(String.format("Profit %.8f", (boughtPrice - lastBid) * tradeAmount));
        isBought = false;
    }

    protected double minimumProfitablePrice() {
        return boughtPrice + (boughtPrice * 0.0015);
    }

    protected double getLastAsk() {
        return client.getLastAsksAverage(tradeAmount, 3);
    }
}
