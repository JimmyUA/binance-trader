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
        newSellProcess();
    }

    private void newSellProcess() {
        goalSellPrice = boughtPrice + (boughtPrice * 0.002);
        stopLossPrice = boughtPrice - (boughtPrice * 0.015);

        while (true){
            if (stopTicker){
                return;
            }
            sleepSeconds(3);
            updateLastBid();

            if (enoughProfit() && sellDecisionMaker.isTimeToTryToSell()){
                if (soldWithProfit()){
                    break;
                }
            } else if (sellDecisionMaker.isCrossedStopLoss(stopLossPrice, lastBid)){
                sellToMarket();
                break;
            }
        }
    }

    private boolean enoughProfit() {
        return lastBid > goalSellPrice;
    }

    private boolean soldWithProfit() {
        if (lastBid > goalSellPrice){
            logger.info(String.format("Last bid: %.8f is over goal price: %.8f, percentage: %.2f",
                    lastBid, goalSellPrice, ((lastBid - goalSellPrice)/goalSellPrice) * 100));
            sellToMarket();
            return true;
        } else {
            logger.info("Price did not grow enough, waiting");
            return false;
        }
    }


}
