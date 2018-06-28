package io.github.unterstein.statistic.MACD;

import io.github.unterstein.BinanceTrader;
import io.github.unterstein.statistic.PricesAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public MACD() {
    }

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
        if (period == shortPeriod) {
            EMAs = shortEMAs;
        } else {
            EMAs = longEMAs;
        }
        if (EMAs.size() == 0) {
            double average = prices.stream().
                    skip(prices.size() - period).mapToDouble(price -> price).average().getAsDouble();
            result = average;
        } else {
            Double lastPrice = prices.getLast();
            Double lastShortEMA = EMAs.getLast();
            int increasedPeriod = period + 1;
            double EMACoefficient = 2.0 / increasedPeriod;
            double lastPricePart = lastPrice * EMACoefficient;
            double lastEMAPart = lastShortEMA * (1.0 - EMACoefficient);
            result = lastPricePart + lastEMAPart;
        }
        EMAs.addLast(result);
        if (EMAs.size() > 100) {
            EMAs.pollFirst();
        }
        return result;
    }

    public double MACD() {
        double MACD = getLastEMA(shortPeriod) - getLastEMA(longPeriod);
        MACDs.addLast(MACD);
        if (MACDs.size() > 100) {
            MACDs.pollFirst();
        }
        return MACD;
    }

    private double getLastEMA(Integer period) {
        LinkedList<Double> EMAs;
        if (period == shortPeriod) {
            EMAs = shortEMAs;
        } else {
            EMAs = longEMAs;
        }
        return EMAs.getLast();
    }

    public Double signal() {
        double signal;
        if (signals.size() == 0) {
            signal = MACDs.stream()
                    .skip(MACDs.size() - signalPeriod)
                    .mapToDouble(macd -> macd)
                    .average().getAsDouble();
        } else {
            Double signalKof = 2.0 / (signalPeriod + 1);
            signal = getLastMACD() * signalKof + (getLastSignal() * (1 - signalKof));
        }
        signals.addLast(signal);
        if (signals.size() > 100) {
            signals.pollFirst();
        }

        return signal;
    }

    public Double histogramm() {
        return getLastMACD() - getLastSignal();
    }

    protected Double getLastSignal() {
        return signals.getLast();
    }

    protected Double getLastMACD() {
        return MACDs.getLast();
    }


    public void calculateCurrentHistogram() {
        if (minutesFromStart >= shortPeriod) {
            EMA(shortPeriod);
        }
        if (minutesFromStart >= longPeriod) {
            EMA(longPeriod);
            MACD();
            if (minutesFromStart >= longPeriod + signalPeriod - 1) {
                signal();
                histograms.addLast(histogramm());
                if (histograms.size() > 100) {
                    histograms.pollFirst();
                }
            }
        }
    }

    public Double getLastHistogram() {
        if (histograms.size() > 0) {
            return histograms.getLast();
        }
        return 0.0;
    }

    public boolean isAccending() {
        Double lastHistogram = histograms.getLast();

        double previousAverage = histograms.stream().skip(histograms.size() - 4)
                .limit(3).mapToDouble(d -> d).average().getAsDouble();
        String direction = "";

        if (lastHistogram > previousAverage) {
            direction = "ascending";
            informMACDTrend(lastHistogram, previousAverage, direction);
            return true;
        } else {
            direction = "descending";
            informMACDTrend(lastHistogram, previousAverage, direction);
            return false;
        }
    }

    private void informMACDTrend(Double lastHistogram, double previousAverage, String direction) {
        logger.info(String.format("MACD is %s, last histo: %.10f, previous 3 average: %.10f",
                direction, lastHistogram, previousAverage));
    }

}