package io.github.unterstein.botlogic.decision.onetrend;

import io.github.unterstein.statistic.MarketAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SellDecisionMakerOneTrend {

    private static Logger logger = LoggerFactory.getLogger(SellDecisionMakerOneTrend.class);


    @Autowired
    MarketAnalyzer marketAnalyzer;



    public boolean isTimeToTryToSell() {
        if (marketAnalyzer.isDownTrendLongPeriodStarted() || marketAnalyzer.wasMACDCrossSignalDown() || marketAnalyzer.isDownDayTrend()){
            return true;
        }
        return false;
    }

    public boolean isCrossedStopLoss(double stopLossPrice, Double lastBid) {
        if (lastBid < stopLossPrice){
            logger.info(String.format(
                    "Too dangerous too keep holding coins lastBid: %.8f lower than stop loss: %.8f and price keep fail",
                    lastBid, stopLossPrice));
            return true;

        } else {
            return false;
        }
    }
}
