package io.github.unterstein.statistic.MA;

import io.github.unterstein.remoteManagment.ManagementConstants;
import io.github.unterstein.statistic.PricesAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

import static io.github.unterstein.remoteManagment.ManagementConstants.*;

@Component
public class MovingAverage {

    private static Logger logger = LoggerFactory.getLogger(MovingAverage.class);

    @Autowired
    private PricesAccumulator pricesAccumulator;

    private boolean wasUpTrendLongPeriod = false;

    public MovingAverage() {

    }


    public boolean isUpTrendShortPeriod(){
        if (MA(5) >= MA(15)){
            logger.info("Up-trend detected short period");
            return true;
        } else {
            logger.info("Down-trend detected short period");
            return false;
        }
    }

    public boolean isUpTrendLongPeriod(){
        if (MA(15) >= MA(50)){
            logger.info("Up-trend detected long period");
            wasUpTrendLongPeriod = true;
            return true;
        } else {
            wasUpTrendLongPeriod = false;
            logger.info("Down-trend detected long period");
            return false;
        }
    }

    private double MA(int amount) {

        LinkedList<Double> samples = pricesAccumulator.getSamples();
        double sum = samples.stream().skip(samples.size() - amount).mapToDouble(d -> d).sum();
        return sum/amount;
    }

    public boolean isUpTrendByAsk(Double lastAsk) {
        return isUpTrendShortPeriod() && lastAsk >= MA(5);

    }

    public boolean isUpTrendByBid(Double lastBid) {
        return isUpTrendShortPeriod();
    }

    public boolean isUpTrendOneTrend() {
        if (MA(5) >= MA(15)){
            logger.info("Up-trend detected one trend");
            return true;
        } else {
            logger.info("Down-trend detected one trend");
            return false;
        }
    }

    public boolean isDownTrendLongPeriodStarted() {
        if (isDownTrendLongPeriod()){
            if (wasUpTrendLongPeriod){
                wasUpTrendLongPeriod = false;
                return true;
            }
        }
        return false;
    }

    private boolean isDownTrendLongPeriod() {
        return !isUpTrendLongPeriod();
    }

    public boolean isDownDayTrend() {
        return !isUpDayTrend();
    }

    private boolean isUpDayTrend() {
        if(minutesFromStart > 50 * 15) {
            return MA(5 * 15) >= MA(50 * 15);
        } else {
            return startDayTrend;
        }
    }
}
