package io.github.unterstein.statistic.MACD;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.BinanceTrader;
import io.github.unterstein.TradingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.unterstein.remoteManagment.ManagementConstants.minutesFromStart;


public class MACD {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);
    private Integer shortPeriod;
    private Integer longPeriod;
    private Integer signalPeriod;
    private LinkedList<Double> MACDs;
    private LinkedList<Double> signals;
    private LinkedList<Double> histograms;
    private LinkedList<Double> shortEMAs;
    private LinkedList<Double> longEMAs;


    private String name;

    @Autowired
    private TradingClient client;

    protected boolean wasMACDCrossSignalUp;
    protected boolean wasHistoCrossZeroUp;
    protected int crossCounter;
    protected int crossHistoCounter;
    private LinkedList<Double> prices;
    protected Double lastHisto;
    private CandlestickInterval candlestickInterval;

    public MACD() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public MACD(Integer shortPeriod, Integer longPeriod, Integer signalPeriod) {
        this.shortPeriod = shortPeriod;
        this.longPeriod = longPeriod;
        this.signalPeriod = signalPeriod;
         candlestickInterval = CandlestickInterval.ONE_MINUTE;
        initLists();

        crossCounter = 0;
        crossHistoCounter = 0;
    }

    private void initLists() {
        shortEMAs = new LinkedList<>();
        longEMAs = new LinkedList<>();
        MACDs = new LinkedList<>();
        signals = new LinkedList<>();
        histograms = new LinkedList<>();
    }

    public void setCandlestickInterval(CandlestickInterval candlestickInterval) {
        this.candlestickInterval = candlestickInterval;
    }

    //Used only for tests
    protected void setClient(TradingClient client) {
        this.client = client;
    }


    protected double EMA(Integer period) {
        LinkedList<Double> EMAs;
        if (period.equals(shortPeriod)) {
            EMAs = shortEMAs;
        } else {
            EMAs = longEMAs;
        }

        EMAs.add(firstEMA(period));

        Iterable<? extends Double> remainedPrices = prices.stream().skip(period).collect(Collectors.toList());


        for (Double currentPrice : remainedPrices
                ) {
            double currentEMA = notFirstEMA(period, EMAs.getLast(), currentPrice);
            EMAs.add(currentEMA);
        }

        return EMAs.getLast();
    }

    protected synchronized double firstEMA(int period) {
        initPrices();
        return prices.stream().
                limit(period).mapToDouble(price -> price).average().orElse(0.0);
    }

    protected synchronized double notFirstEMA(int period, Double previousEMA, Double currentPrice) {

        int increasedPeriod = period + 1;
        double EMACoefficient = 2.0 / increasedPeriod;
        double lastPricePart = currentPrice * EMACoefficient;
        double lastEMAPart = previousEMA * (1.0 - EMACoefficient);
        return lastPricePart + lastEMAPart;

    }

    protected synchronized void initPrices() {
        prices = getSamplesFromExchange();
    }

    private LinkedList<Double> getSamplesFromExchange() {
        return new LinkedList<>(client.getPricesFromExchange(candlestickInterval));
    }

    public synchronized double MACD() {
        initLists();
        return EMA(shortPeriod) - EMA(longPeriod);
    }

    protected synchronized double firstSignal() {
        int MACDamountBeforeFirstSignal = signalPeriod;
        List<Double> workingShortEMAs = shortEMAs.stream().skip(longPeriod - shortPeriod)
                .collect(Collectors.toList());
        List<Double> workingLongEMAs = longEMAs;

        MACDs = new LinkedList<>();

        for (int i = 0; i < workingLongEMAs.size(); i++) {
            MACDs.add(workingShortEMAs.get(i) - workingLongEMAs.get(i));
        }

        return MACDs.stream().limit(MACDamountBeforeFirstSignal).mapToDouble(Double::valueOf).average().orElse(0.0);
    }

    protected double notFirstSignal(Double previousSignal, Double currentMACD) {

        Double signalKof = 2.0 / (signalPeriod + 1);
        return currentMACD * signalKof + (previousSignal * (1 - signalKof));
    }

    public synchronized Double signal() {
        MACD();
        signals.add(firstSignal());


        Iterable<? extends Double> remainedMACDs = MACDs.stream().skip(signalPeriod).collect(Collectors.toList());


        for (Double currentMACD : remainedMACDs
                ) {
            double currentSignal = notFirstSignal(signals.getLast(), currentMACD);
            signals.add(currentSignal);
        }

        return signals.getLast();
    }

    protected void checkMACDCrossedSignal() {
        Double lastMACD = MACD();
        Double lastSignal = signal();

        if (lastMACD > lastSignal) {
            if (!wasMACDCrossSignalUp) {
                crossCounter = 0;
                wasMACDCrossSignalUp = true;
            }
            crossCounter++;
        } else {
            wasMACDCrossSignalUp = false;
        }
    }


    public synchronized Double histogramm() {
        double macd = MACD();
        Double signal = signal();
        lastHisto = macd - signal;
        checkCrosses();
        return lastHisto;
    }


    public synchronized boolean isAscending() {
        histogramm();
        Double lastHistogram = MACDs.getLast() - signals.getLast();

        List<Double> previousMACDs = MACDs.stream().skip(MACDs.size() - 4)
                .limit(3).mapToDouble(d -> d).boxed().collect(Collectors.toList());

        List<Double> previousSignals = signals.stream().skip(signals.size() - 4)
                .limit(3).mapToDouble(d -> d).boxed().collect(Collectors.toList());

        List<Double> previousHistograms = new ArrayList<>(3);

        for (int i = 0; i < previousMACDs.size() - 1; i++) {
            double currentHistogram = previousMACDs.get(i) - previousSignals.get(i);
            previousHistograms.add(currentHistogram);
        }

        double previousAverage = previousHistograms.stream().mapToDouble(Double::new).average().orElse(0.0);

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
        logger.info(String.format("%s MACD is %s, last histo: %.10f, previous 3 average: %.10f",
                name, direction, lastHistogram, previousAverage));
    }

    public boolean wasMACDCrossSignalUp() {
        if (isMSCDCrossSignalUp()) {
            logger.info(String.format("MACD crossed signal up %d minutes ago, purchase is allowed", crossCounter));
            return true;
        } else {
            logger.info("MACD is less than signal");
            return false;
        }
    }

    private boolean isMSCDCrossSignalUp() {

        return wasMACDCrossSignalUp;
    }

    public boolean wasMACDCrossSignalDown() {
        histogramm();
        if (!wasMACDCrossSignalUp) {
            logger.info(String.format("%s MACD crossed Signal down", name));
            return true;
        } else {
            logger.info(String.format("%s MACD did not cross Signal down", name));
            return false;
        }
    }


    public boolean wasHistoCrossZeroUp() {

        if (minutesFromStart < 5){
            return false;
        }
        if (wasHistoCrossZeroUp) {
            if(crossHistoCounter < 3){
            logger.info(String.format("%s Histo crossed Zero up, histo: %.10f", name, lastHisto));
            return true;}
            else {
                logger.info(String.format("%s Histo crossed Zero up, histo: %.10f, too long ago", name, lastHisto));
                return false;
            }
        } else {
            logger.info(String.format("%s Histo didnot cross Zero up, histo: %.10f", name, lastHisto));
            return false;
        }
    }

    protected void checkHistoCrossedZero() {

        logger.info(String.format("LastHisto is %.10f", lastHisto));
        if (lastHisto > 0.0) {
            if (!wasHistoCrossZeroUp) {
                crossHistoCounter = 0;
                wasHistoCrossZeroUp = true;
            }
            crossHistoCounter++;
        } else {
            wasHistoCrossZeroUp = false;
        }
    }

    public void checkCrosses() {
        checkMACDCrossedSignal();
        checkHistoCrossedZero();
    }
}