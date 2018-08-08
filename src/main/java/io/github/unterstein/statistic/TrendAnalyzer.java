package io.github.unterstein.statistic;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.statistic.MA.MovingAverage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.unterstein.statistic.EMA.ExponentialMovingAverage.EMA;

public class TrendAnalyzer {

    private static Logger logger = LoggerFactory.getLogger(TrendAnalyzer.class);


    @Autowired
    private MovingAverage movingAverage;

    public boolean isUpTrend() {
        return movingAverage.isUpTrendShortPeriod() && movingAverage.isUpTrendLongPeriod();
    }

    public boolean isUptrendByAsk(Double lastAsk){
        return movingAverage.isUpTrendByAsk(lastAsk);
    }

    public boolean isUptrendByBid(Double lastBid){
        return movingAverage.isUpTrendByBid(lastBid);
    }

    public boolean isDownTrendLongPeriod() {
        return !movingAverage.isUpTrendLongPeriod();
    }

    public boolean isUpTrendShortPeriod() {
        return movingAverage.isUpTrendShortPeriod();
    }

    public boolean isUpTrendLongPeriod() {
        return movingAverage.isUpTrendLongPeriod();
    }

    public boolean isUpTrendOneTrend() {
        return movingAverage.isUpTrendOneTrend();
    }

    public boolean isDownTrendLongPeriodStarted() {
        return movingAverage.isDownTrendLongPeriodStarted();
    }

    public boolean isDownDayTrend() {
        return movingAverage.isDownDayTrend();
    }

    public boolean isUpDayTrend() {
        return !isDownDayTrend();
    }

    public boolean isTrend50VS225Up() {
        return movingAverage.isTrend50VS225Up();
    }

    public boolean isMoMotrendUp() {
        Double ema20 = EMA(20, CandlestickInterval.FIVE_MINUTES);
        Double ema100 = EMA(100, CandlestickInterval.FIVE_MINUTES);
        if (ema20 > ema100){
            logger.info(String.format("MoMo up trend detected, EMA20: %.10f, EMA100: %.10f", ema20, ema100));
            return true;
        } else {
            logger.info(String.format("MoMo down trend detected, EMA20: %.10f, EMA100: %.10f", ema20, ema100));
            return false;
        }
    }
}
