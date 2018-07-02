package io.github.unterstein.strategy;


import io.github.unterstein.decision.onetrend.BuyDecisionMakerOneTrend;
import io.github.unterstein.decision.onetrend.SellDecisionMakerOneTrend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.stopTicker;
import static util.Slepper.sleepSeconds;

@Component
public class OneTrendStrategy extends AbstractStrategy{


    @Autowired
    private SellDecisionMakerOneTrend sellDecisionMaker;

    @Autowired
    public OneTrendStrategy(BuyDecisionMakerOneTrend buyDecisionMaker) {
        this.buyDecisionMaker = buyDecisionMaker;
    }

    @Override
    public void buyProcess() {
        super.buyProcess();
    }

    @Override
    public void sellProcess() {
        goalSellPrice = boughtPrice + (boughtPrice * 0.002);
        while (isUpTrending()) {
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
        if (wasOverGoalPrice && lastBid > minimumProfitablePrice()){
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
                return;
            }
            if (stopTicker){
                return;
            }
        }
    }

    private boolean isUpTrending() {
        return !sellDecisionMaker.isTrendChanged();
    }
}
