package io.github.unterstein.strategy;

import io.github.unterstein.TradingClient;
import io.github.unterstein.decision.maandrsi.BuyDecisionMaker;
import io.github.unterstein.decision.maandrsi.SellDecisionMaker;
import io.github.unterstein.decision.macd.BuyDecisionMakerMACD;
import io.github.unterstein.decision.macd.SellDecisionMakerMACD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MACDStrategy implements Strategy {

    private static Logger logger = LoggerFactory.getLogger(MAandRSIStrategy.class);

    private boolean isBought = false;



    @Autowired
    private TradingClient client;

    @Autowired
    private BuyDecisionMakerMACD buyDecisionMaker;

    @Autowired
    private SellDecisionMakerMACD sellDecisionMaker;
    private int tradeAmount;
    private Double boughtPrice;
    private Double goalSellPrice;
    private Double lastBid;
    private boolean wasOverGoalPrice = false;
    private double stopLossPrice;

    @Override
    public void buyProcess() {
        if (buyDecisionMaker.isRightMomentToBuy()){
            client.buyMarket(tradeAmount);
            boughtPrice = getLastAsk();
            logger.info(String.format("Bought %d coins from market! at %.8f rate", tradeAmount, boughtPrice));
            isBought = true;
        }
    }

    @Override
    public void sellProcess() {

    }

    @Override
    public void setTradeAmount(int tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    @Override
    public boolean isBought() {
        return isBought;
    }

    private double getLastAsk() {
        return client.getLastAsksAverage(tradeAmount, 3);
    }
}
