package io.github.unterstein.botlogic.decision;

import io.github.unterstein.statistic.TrendAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StopLossDecider {

    private static Logger logger = LoggerFactory.getLogger(StopLossDecider.class);


    private double standardStopLossKof = 0.015;

    @Autowired
    private TrendAnalyzer trendAnalyzer;

    public double getStopLossKof(){
        double stopLossKof = standardStopLossKof;
        String trendDirection = "UP";
        if (trendAnalyzer.isDownDayTrend()){
            trendDirection = "Down";
        } else {
            stopLossKof *= 2;
        }
        logger.info(trendDirection + " day trend detected, stop loss kof: " + stopLossKof);
        return stopLossKof;
    }
}
