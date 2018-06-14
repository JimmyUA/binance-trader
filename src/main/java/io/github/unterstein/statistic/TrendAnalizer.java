package io.github.unterstein.statistic;

import io.github.unterstein.BinanceTrader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TrendAnalizer {

    @Autowired
    private MovingAverage movingAverage;

    public boolean isUpTrend() {
        return movingAverage.isUpTrend();
    }

    public boolean isUptrendByAsk(Double lastAsk){
        return movingAverage.isUpTrendByAsk(lastAsk);
    }

    public boolean isUptrendByBid(Double lastBid){
        return movingAverage.isUpTrendByBid(lastBid);
    }
}
