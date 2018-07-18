package io.github.unterstein.botlogic.strategy;

import io.github.unterstein.botlogic.decision.BuyDecisionMaker;
import io.github.unterstein.botlogic.decision.maandrsi.SellDecisionMakerMARSI;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.unterstein.remoteManagment.ManagementConstants.stopTicker;
import static util.Slepper.sleepSeconds;


public class MAandRSIStrategy extends AbstractStrategy{



    @Autowired
    private SellDecisionMakerMARSI sellDecisionMaker;

    @Autowired
    public MAandRSIStrategy(BuyDecisionMaker buyDecisionMaker) {
        this.buyDecisionMaker = buyDecisionMaker;
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
            sellToMarket();
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
                sellToMarket();
                sleepSeconds(180);
                break;
            } else if (lastBid < stopLossPrice && sellDecisionMaker.isTooDangerous()) {
                logger.info(String.format("Too dangerous too keep holding coins lastBid: %.8f lower than stop loss: %.8f and price keep fail", lastBid, stopLossPrice));
                sellToMarket();
                return;
            }
            if (stopTicker){
                return;
            }
        }

    }

    private boolean isUpTrending() {
        return !sellDecisionMaker.isTrendChanged(lastBid);
    }

}
