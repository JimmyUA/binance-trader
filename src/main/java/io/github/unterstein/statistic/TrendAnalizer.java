package io.github.unterstein.statistic;

import io.github.unterstein.BinanceTrader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrendAnalizer {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);


    private static double[] trend = new double[400];
    private static int pointer = 0;

    public boolean isUpTrend() {
        double lastTenAverage = 0.0;
        double lastTenAmount = 0.0;
        if (pointer > 10){
            for (int i = 0; i < 10; i++) {
                lastTenAmount += trend[pointer - i];
            }
            lastTenAverage = lastTenAmount/10;
        }
        if (lastTenAverage > trend[0]) {
            logger.info("Up-trend detected");
            return true;
        }
        logger.info("Down-trend detected");
        return false;
    }

    public void setCurrentPrice(double price) {
        trend[pointer++] = price;
        if (pointer == 400) {
            pointer = 0;
        }
    }
}
