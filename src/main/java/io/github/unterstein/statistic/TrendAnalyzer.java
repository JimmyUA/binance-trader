package io.github.unterstein.statistic;

import io.github.unterstein.statistic.MA.MovingAverage;
import org.springframework.beans.factory.annotation.Autowired;

public class TrendAnalyzer {

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
}
