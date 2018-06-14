package io.github.unterstein.statistic;

import io.github.unterstein.BinanceTrader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public class MovingAverage {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);

    private LinkedList<Double> samples;
    private int samplesCapacity;

    public MovingAverage() {
        samples = new LinkedList<>();
        samplesCapacity = 15;
        for (int i = 0; i < samplesCapacity; i++) {
            samples.add(1.0);
        }
    }


    public void add(Double price){

        if (samples.size() < samplesCapacity){
            samples.add(price);
        } else{
            samples.pollFirst();
            samples.addLast(price);
        }
    }

    public boolean isUpTrend(){
        if (MA(5) >= MA(15)){
            logger.info("Up-trend detected");
            return true;
        } else {
            logger.info("Down-trend detected");
            return false;
        }
    }

    private double MA(int amount) {

        double sum = samples.stream().skip(samplesCapacity - amount).mapToDouble(d -> d).sum();
        return sum/amount;
    }

    public boolean isUpTrendByAsk(Double lastAsk) {
        return isUpTrend() && lastAsk >= MA(5);

    }

    public boolean isUpTrendByBid(Double lastBid) {
        return isUpTrend() && lastBid >= MA(5);
    }
}
