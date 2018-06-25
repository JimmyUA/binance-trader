package io.github.unterstein.statistic.MACD;

import io.github.unterstein.BinanceTrader;
import io.github.unterstein.statistic.PricesAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.management.openmbean.TabularData;
import java.util.LinkedList;

import static io.github.unterstein.remoteManagment.ManagementConstants.minutesFromStart;

@Component
public class MACD {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);
    private Integer shortPeriod;
    private Integer longPeriod;
    private Integer signalPeriod;
    private LinkedList<Double> shortEMAs;
    private LinkedList<Double> longEMAs;
    private LinkedList<Double> MACDs;
    private LinkedList<Double> signals;
    private LinkedList<Double> histograms;

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
        signals = new LinkedList<>();
        histograms = new LinkedList<>();
    }

    //Used only for tests
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
        MACDs.addLast(MACD);
        if (MACDs.size() > 100){
            MACDs.pollFirst();
        }
        return MACD;
    }

    public Double signal(){
        double signal = MACDs.stream()
                .skip(MACDs.size() - signalPeriod)
                .mapToDouble(macd -> macd)
                .average().getAsDouble();
        signals.addLast(signal);
        if (signals.size() > 100) {
            signals.pollFirst();
        }

        return signal;
    }

    public Double histogramm(){
        return getLastMACD() - getLastSignal();
    }

    private Double getLastSignal() {
        return signals.getLast();
    }

    private Double getLastMACD() {
        return MACDs.getLast();
    }


    public void calculateCurrentHistogram(){
        MACD();
        if (minutesFromStart > longPeriod) {
            signal();
            histograms.addLast(histogramm());
            if (histograms.size() > 100) {
                histograms.pollFirst();
            }
        }
    }

    public Double getLastHistogram() {
        if (minutesFromStart > longPeriod && histograms.size() > 0) {
            return histograms.getLast();
        }
        return 0.0;
    }
}
