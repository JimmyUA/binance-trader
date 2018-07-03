package io.github.unterstein.strategy;

import io.github.unterstein.decision.macd.BuyDecisionMakerMACD;
import io.github.unterstein.decision.macd.SellDecisionMakerMACD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.stopTicker;
import static util.Slepper.sleepSeconds;

@Component
public class MACDStrategy extends AbstractStrategy {


    @Autowired
    private BuyDecisionMakerMACD buyDecisionMaker;

    @Autowired
    private SellDecisionMakerMACD sellDecisionMaker;


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
