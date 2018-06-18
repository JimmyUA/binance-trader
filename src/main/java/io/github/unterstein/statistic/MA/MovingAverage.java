package io.github.unterstein.statistic.MA;

import io.github.unterstein.BinanceTrader;
import io.github.unterstein.statistic.PricesAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class MovingAverage {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);

    @Autowired
    private PricesAccumulator pricesAccumulator;

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
        if (MA(15) >= MA(100)){
            logger.info("Up-trend detected long period");
            return true;
        } else {
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
        return isUpTrendShortPeriod() && lastBid >= MA(5);
    }
}
