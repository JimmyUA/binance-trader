package io.github.unterstein.strategy;

import io.github.unterstein.decision.maandrsi.BuyDecisionMaker;
import io.github.unterstein.decision.maandrsi.SellDecisionMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.stopTicker;
import static util.Slepper.sleepSeconds;

@Component
public class MAandRSIStrategy extends AbstractStrategy{


    @Autowired
    private BuyDecisionMaker buyDecisionMaker;

    @Autowired
    private SellDecisionMaker sellDecisionMaker;


    @Override
    public void buyProcess() {
        if (buyDecisionMaker.isRightMomentToBuy(getLastAsk())) {
            client.buyMarket(tradeAmount);
            boughtPrice = getLastAsk();
            logger.info(String.format("Bought %d coins from market! at %.8f rate", tradeAmount, boughtPrice));
            isBought = true;
        }
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
