package io.github.unterstein.botlogic.strategy;

import io.github.unterstein.botlogic.decision.BuyDecisionMaker;
import io.github.unterstein.botlogic.decision.macd.SellDecisionMakerMACD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.stopTicker;
import static util.Slepper.sleepSeconds;


public class MACDStrategy extends AbstractStrategy {


    @Autowired
    private SellDecisionMakerMACD sellDecisionMaker;

    @Autowired
    public MACDStrategy(BuyDecisionMaker buyDecisionMaker) {
        this.buyDecisionMaker = buyDecisionMaker;
    }


    @Override
    public void sellProcess() {
        sleepSeconds(180);
        goalSellPrice = boughtPrice + (boughtPrice * 0.002);
        while (isUpTrending()) {
            sleepSeconds(3);
            updateLastBid();
            if (lastBid > goalSellPrice){
                wasOverGoalPrice = true;
            }
            if (stopTicker){
                return;
            }
        }
        if (wasOverGoalPrice && lastBid > minimumProfitablePrice()){
            logger.info("MACD trend changed and price was over goal price!");
            sellToMarket();
            return;
        }
        stopLossPrice = boughtPrice - (boughtPrice * 0.015);
        while (true) {
            logger.info("MACD trend changed and price did not rise enough, waiting");
            sleepSeconds(3);
            updateLastBid();
            logger.info(String.format("Waiting while reach goal price, difference %.8f, Last maxBid: %.8f, goalSellPrice: %.8f\"",
                    goalSellPrice - lastBid, lastBid, goalSellPrice));
            if (lastBid > goalSellPrice) {
                logger.info("price is high enough");
                sellToMarket();
                sleepSeconds(180);
                break;
            } else if (lastBid < stopLossPrice) {
                logger.info(String.format("Too dangerous too keep holding coins lastBid: %.8f lower than stop loss: %.8f and price keep fail", lastBid, stopLossPrice));
                sellToMarket();
            }
            if (stopTicker){
                return;
            }
        }
    }

    private boolean isUpTrending() {
        return sellDecisionMaker.isMACDAscending();
    }
}
