package io.github.unterstein.botlogic.strategy;

import io.github.unterstein.botlogic.decision.momo.BuyDecisionMakerMoMo;
import io.github.unterstein.botlogic.decision.momo.SellDecisionMakerMoMo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.stopTicker;
import static util.Slepper.sleepSeconds;

@Component
public class MoMoStrategy extends AbstractStrategy{

    @Autowired
    private SellDecisionMakerMoMo sellDecisionMaker;

    @Autowired
    public MoMoStrategy(BuyDecisionMakerMoMo buyDecisionMaker) {
        this.buyDecisionMaker = buyDecisionMaker;
    }

    @Override
    public void buyProcess() {
        super.buyProcess();
    }

    @Override
    public void sellProcess() {
        goalSellPrice = boughtPrice + (boughtPrice * 0.002);
        initStopLoss();

        while (true){
            if (stopTicker){
                return;
            }
            sleepSeconds(3);
            updateLastBid();

            if (sellDecisionMaker.isTimeToSell()){
                logger.info(String.format("Last bid: %.8f is over goal price: %.8f, percentage: %.2f",
                        lastBid, goalSellPrice, ((lastBid - goalSellPrice)/goalSellPrice) * 100));
                sellToMarket();
                break;
            } else if (sellDecisionMaker.isCrossedStopLoss(stopLossPrice, lastBid)){
                sellToMarket();
                break;
            }
        }
        amplitudeAnalyser.stop();
    }

    private void initStopLoss() {
        Double trackedEMA20 = ((BuyDecisionMakerMoMo) buyDecisionMaker).getTrackedEMA20();
        stopLossPrice = trackedEMA20 - (trackedEMA20 * 0.001);
    }

}
