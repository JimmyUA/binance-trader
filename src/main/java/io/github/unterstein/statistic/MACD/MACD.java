package io.github.unterstein.statistic.MACD;

import io.github.unterstein.BinanceTrader;
import io.github.unterstein.statistic.PricesAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.management.openmbean.TabularData;
import java.util.LinkedList;

@Component
public class MACD {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);
    private Integer shortPeriod;
    private Integer longPeriod;
    private Integer signalPeriod;
    private LinkedList<Double> shortEMAs;
    private LinkedList<Double> longEMAs;
    private LinkedList<Double> MACDs;

    @Autowired
    private PricesAccumulator pricesAccumulator;
    private String none;

    public MACD(){}

    public MACD(Integer shortPeriod, Integer longPeriod, Integer signalPeriod) {
        this.shortPeriod = shortPeriod;
        this.longPeriod = longPeriod;
        this.signalPeriod = signalPeriod;
        shortEMAs = new LinkedList<>();
        longEMAs = new LinkedList<>();
        MACDs = new LinkedList<>();
    }

    protected void setPricesAccumulator(PricesAccumulator pricesAccumulator) {
        this.pricesAccumulator = pricesAccumulator;
    }

    protected double shortEMA() {
        return EMA(shortPeriod);
    }

    protected double longEMA() {
        return EMA(longPeriod);
    }

    private double EMA(Integer period) {
        double result;
        LinkedList<Double> prices = pricesAccumulator.getSamples();
        LinkedList<Double> EMAs;
        if (period > shortPeriod){
            EMAs = shortEMAs;
        } else {
            EMAs = longEMAs;
        }
        if(EMAs.size() == 0) {
            double sum = prices.stream().
                    skip(prices.size() - period).mapToDouble(price -> price).sum();
            result = sum / period;
        } else {
            Double lastPrice = prices.getLast();
            Double lastShortEMA = EMAs.getLast();
            int increasedShortPeriod = period + 1;
            double EMACoefficient = 2.0 / increasedShortPeriod;
            double lastPricePart = lastPrice * EMACoefficient;
            double lastEMAPart = lastShortEMA * (1.0 - EMACoefficient);
            result = lastPricePart + lastEMAPart;
        }
        EMAs.addLast(result);
        if (EMAs.size() > 100){
            EMAs.pollFirst();
        }
        return result;
    }

    public double MACD() {
        double MACD = EMA(shortPeriod) - EMA(longPeriod);
        MACDs.add(MACD);
        if (MACDs.size() > 100){
            MACDs.pollFirst();
        }
        return MACD;
    }

    public Double signal(){
        return MACDs.stream()
                .skip(MACDs.size() - signalPeriod)
                .mapToDouble(macd -> macd)
                .average().getAsDouble();
    }

    public Double histogramm(){
        return getLastMACD() - signal();
    }

    private Double getLastMACD() {
        return MACDs.getLast();
    }
}
