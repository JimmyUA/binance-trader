package io.github.unterstein.statistic.MA;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.TradingClient;
import io.github.unterstein.statistic.PricesAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.unterstein.remoteManagment.ManagementConstants.minutesFromStart;
import static io.github.unterstein.remoteManagment.ManagementConstants.startDayTrend;

@Component
public class MovingAverage {

    private static Logger logger = LoggerFactory.getLogger(MovingAverage.class);

    @Autowired
    private PricesAccumulator pricesAccumulator;

    private boolean wasUpTrendLongPeriod = false;
    @Autowired
    private TradingClient client;

    public MovingAverage() {

    }


    public boolean isUpTrendShortPeriod(){
        if (MA(5, CandlestickInterval.ONE_MINUTE) >= MA(15, CandlestickInterval.ONE_MINUTE)){
            logger.info("Up-trend detected short period");
            return true;
        } else {
            logger.info("Down-trend detected short period");
            return false;
        }
    }

    public boolean isUpTrendLongPeriod(){
        if (MA(15, CandlestickInterval.ONE_MINUTE) >= MA(50, CandlestickInterval.ONE_MINUTE)){
            logger.info("Up-trend detected long period");
            wasUpTrendLongPeriod = true;
            return true;
        } else {
            wasUpTrendLongPeriod = false;
            logger.info("Down-trend detected long period");
            return false;
        }
    }

    protected double MA(int amount, CandlestickInterval interval) {

        LinkedList<Double> samples = getSamplesFromExchange(amount, interval);
        double sum = samples.stream().mapToDouble(d -> d).sum();
        return sum/amount;
    }

    private LinkedList<Double> getSamples(long amount) {
        return pricesAccumulator.getSamples(amount);
    }

    private LinkedList<Double> getSamplesFromExchange(long amount, CandlestickInterval interval) {

        List<Double> originalList = client.getPricesFromExchangeReversed(interval)
                .stream().limit(amount).collect(Collectors.toList());

        return new LinkedList<>(originalList);
    }

    public boolean isUpTrendByAsk(Double lastAsk) {
        return isUpTrendShortPeriod() && lastAsk >= MA(5, CandlestickInterval.ONE_MINUTE);

    }

    public boolean isUpTrendByBid(Double lastBid) {
        return isUpTrendShortPeriod();
    }

    public boolean isUpTrendOneTrend() {
        if (MA(5, CandlestickInterval.ONE_MINUTE) >= MA(50, CandlestickInterval.ONE_MINUTE)){
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
            boolean isDayTrendUP = MA(15, CandlestickInterval.FIFTEEN_MINUTES) >= MA(50, CandlestickInterval.FIFTEEN_MINUTES);
            logger.info(String.format("Day trend is %s", isDayTrendUP ? "UP" : "DOWN"));
            return isDayTrendUP;

    }
}
