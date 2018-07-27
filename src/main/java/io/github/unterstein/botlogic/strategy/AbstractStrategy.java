package io.github.unterstein.botlogic.strategy;

import io.github.unterstein.TradingClient;
import io.github.unterstein.botlogic.decision.BuyDecisionMaker;
import io.github.unterstein.botlogic.decision.StopLossDecider;
import io.github.unterstein.botlogic.services.TradeService;
import io.github.unterstein.statistic.amplitude.AmplitudeAnalyser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractStrategy implements Strategy {

    protected static Logger logger = LoggerFactory.getLogger(Strategy.class);

    protected boolean isBought = false;

    protected BuyDecisionMaker buyDecisionMaker;

    @Autowired
    protected TradingClient client;

    @Autowired
    protected TradeService tradeService;

    @Autowired
    protected StopLossDecider stopLossDecider;

    @Autowired
    protected AmplitudeAnalyser amplitudeAnalyser;

    protected int tradeAmount;
    protected Double boughtPrice;
    protected Double goalSellPrice;
    protected Double lastBid;
    protected boolean wasOverGoalPrice = false;
    protected double stopLossPrice;



    @Override
    public void buyProcess() {
        double lastAsk = getLastAsk();
        if (buyDecisionMaker.isRightMomentToBuy(lastAsk)){
            client.buyMarket(tradeAmount);
            tradeService.addBuyOrder(lastAsk);
            boughtPrice = lastAsk;
            logger.info(String.format("Bought %d coins from market! at %.8f rate", tradeAmount, boughtPrice));
            isBought = true;
            amplitudeAnalyser.start(boughtPrice);
        }
    }

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

    protected void sellToMarket() {
        updateLastBid();
        client.sellMarket(tradeAmount);
        tradeService.addSellOrder(lastBid);
        logger.info(String.format("Sold %d coins to market! Rate: %.8f", tradeAmount, lastBid));
        logger.info(String.format("Profit %.8f", (boughtPrice - lastBid) * tradeAmount));
        isBought = false;
        wasOverGoalPrice = false;
    }

    protected double minimumProfitablePrice() {
        return boughtPrice + (boughtPrice * 0.0015);
    }

    protected double getLastAsk() {
        return client.getLastAsksAverage(tradeAmount, 3);
    }
}