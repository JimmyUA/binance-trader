package io.github.unterstein.statistic.MACD;

import io.github.unterstein.BinanceTrader;
import io.github.unterstein.statistic.PricesAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;

import static io.github.unterstein.remoteManagment.ManagementConstants.minutesFromStart;


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

    private Double minMACD;
    private Double maxMACD;

    @Autowired
    private PricesAccumulator pricesAccumulator;

    private boolean wasMACDCrossSignalUp;
    private Integer crossCounter;

    public MACD() {
    }

    public MACD(Integer shortPeriod, Integer longPeriod, Integer signalPeriod) {
        this.shortPeriod = shortPeriod;
        this.longPeriod = longPeriod;
        this.signalPeriod = signalPeriod;
        initLists();

        minMACD = 0.0;
        maxMACD = 0.0;
        wasMACDCrossSignalUp = false;
        crossCounter = 0;
    }

    private void initLists() {
        shortEMAs = new LinkedList<>();
        longEMAs = new LinkedList<>();
        MACDs = new LinkedList<>();
        signals = new LinkedList<>();
        histograms = new LinkedList<>();
    }

    public Double getMinMACD() {
        return minMACD;
    }

    public Double getMaxMACD() {
        return maxMACD;
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
        LinkedList<Double> prices = pricesAccumulator.get100Samples();
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

        if (signals.size() > 0) {
            checkMACDCrossedSignal();
        }
        minMACD = MACD < minMACD ? MACD : minMACD;
        maxMACD = MACD > maxMACD ? MACD : maxMACD;
        return MACD;
    }

    private void checkMACDCrossedSignal() {
        Double lastMACD = getLastMACD();
        Double lastSignal = getLastSignal();

        if (lastMACD > lastSignal){
            if (!wasMACDCrossSignalUp){
                crossCounter = 0;
                wasMACDCrossSignalUp = true;
            }
            crossCounter++;
        } else {
            wasMACDCrossSignalUp = false;
        }
    }

    private double getLastEMA(Integer period) {
        LinkedList<Double> EMAs;
        if (period.equals(shortPeriod)) {
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
                    .average().orElse(0.0);
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

    public Double getLastSignal() {
        if (minutesFromStart >= longPeriod) {
        return signals.getLast();
        } else {
            return 0.0;
        }
    }

    public Double getLastMACD() {
        if (minutesFromStart >= longPeriod) {
            return MACDs.getLast();
        } else {
            return 0.0;
        }
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

    public boolean isAscending() {
        Double lastHistogram = histograms.getLast();

        double previousAverage = histograms.stream().skip(histograms.size() - 4)
                .limit(3).mapToDouble(d -> d).average().orElse(0.0);
        String direction;

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

    public boolean wasMACDCrossSignalUp() {
        if (wasMACDCrossSignalUp){
            logger.info(String.format("MACD crossed Signal up%d minutes ago", crossCounter));
            crossCounter += 10;
            return true;
        } else {
            return false;
        }
    }

    public boolean wasMACDCrossSignalDown() {
        if (!wasMACDCrossSignalUp){
            logger.info("MACD crossed Signal down");
            return true;
        } else {
            logger.info("MACD didnot cross Signal down");
            return false;
        }
    }
}