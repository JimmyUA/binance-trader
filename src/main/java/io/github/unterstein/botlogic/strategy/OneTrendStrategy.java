package io.github.unterstein.botlogic.strategy;


import io.github.unterstein.botlogic.decision.onetrend.BuyDecisionMakerOneTrend;
import io.github.unterstein.botlogic.decision.onetrend.SellDecisionMakerOneTrend;
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
        initStopLoss();

        while (true){
            if (stopTicker){
                return;
            }
            sleepSeconds(3);
            updateLastBid();

            if (sellDecisionMaker.isTimeToTryToSell()){
                if (soldWithProfit()){
                    break;
                }
            } else if (sellDecisionMaker.isCrossedStopLoss(stopLossPrice, lastBid)){
                sellToMarket();
                break;
            } else if(sellDecisionMaker.isNeedToSellByMACD()){
                sellToMarket();
                break;
            }
        }
        amplitudeAnalyser.stop();
    }

    private void initStopLoss() {
        Double stopLossKof = stopLossDecider.getStopLossKof();
        stopLossPrice = boughtPrice - (boughtPrice * stopLossKof);
    }

    private boolean enoughProfit() {
        return lastBid > goalSellPrice;
    }

    private boolean soldWithProfit() {
        if (enoughProfit()){
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
