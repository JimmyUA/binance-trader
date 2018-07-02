package io.github.unterstein.decision.macd;

import io.github.unterstein.decision.BuyDecisionMaker;
import io.github.unterstein.statistic.MACD.MACD;
import io.github.unterstein.statistic.TrendAnalyzer;
import io.github.unterstein.strategy.MAandRSIStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static util.Slepper.sleepSeconds;

public class BuyDecisionMakerMACD {

    private static Logger logger = LoggerFactory.getLogger(MAandRSIStrategy.class);

    @Autowired
    TrendAnalyzer trendAnalyzer;

    @Autowired
    private MACD macd;

    private double minimumHistogram;

    public void setMinimumHistogram(double minimumHistogram) {
        this.minimumHistogram = minimumHistogram;
    }

    public boolean isRightMomentToBuy() {
        Double lastHistogram = macd.getLastHistogram();
        if (isMACDNearBottom() && lastHistogram < minimumHistogram){
            logger.info(String.format("Last histogram: %.10f is lower than minimum %.10f",
                    lastHistogram, minimumHistogram));
            while(macd.getLastHistogram() < lastHistogram){
                logger.info("Waiting MACD start grow");
                sleepSeconds(60);
            }
            logger.info("MACD is growing");
        return true;
        } else {
            return false;
        }
    }

    private boolean isMACDNearBottom() {
        Double minMACD = macd.getMinMACD();
        Double lastMACD = macd.getLastMACD();
        double minMACDLimit = minMACD + (minMACD * 0.05);
        if (lastMACD < minMACDLimit){
            logger.info(String.format("Last MACD is near minimum: %.10f is lower than minimum limit %.10f",
                    lastMACD, minMACDLimit));
            return true;
        } else {
            return false;
        }
    }

}
