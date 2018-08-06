package io.github.unterstein.botlogic.decision.momo;

import io.github.unterstein.statistic.MarketAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SellDecisionMakerMoMo {
    private static Logger logger = LoggerFactory.getLogger(SellDecisionMakerMoMo.class);

    @Autowired
    MarketAnalyzer marketAnalyzer;

    public boolean isCrossedStopLoss(double stopLossPrice, Double lastBid) {
        if (lastBid < stopLossPrice){
            logger.info(String.format(
                    "Too dangerous too keep holding coins lastBid: %.8f lower than stop loss: %.8f and price keep fail",
                    lastBid, stopLossPrice));
            return true;

        } else if(isDownMoMoTrend()){
            logger.info(
                    "Too dangerous too keep holding coins on DOWN MoMo trend");
            return true;
        }else {
            return false;
        }
    }

    private boolean isDownMoMoTrend() {
        return !marketAnalyzer.isMoMoTrendUp();
    }

    public boolean isTimeToSellFirstHalf() {
        return false;
    }

    public boolean isTimeToSellSecondHalf() {
        return false;
    }
}
