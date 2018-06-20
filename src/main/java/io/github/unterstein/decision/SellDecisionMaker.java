package io.github.unterstein.decision;


import io.github.unterstein.BinanceTrader;
import io.github.unterstein.statistic.RSI.RSI;
import io.github.unterstein.statistic.TrendAnalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;
import static util.Slepper.sleepSeconds;

@Component
public class SellDecisionMaker {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);


    @Autowired
    private TrendAnalizer trendAnalizer;

    @Autowired
    private RSI rsi;
    private Integer periods;


    public boolean isRightMomentToSell(Double bid) {
        if (isDownTrend(bid) || isRSITooLow()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isRSITooLow() {
        Double rsi = getRSI();
        logger.info(String.format("RSI is %.8f", rsi));
        return rsi < 50;
    }

    private Double getRSI() {
        return this.rsi.getRSI(21);
    }

    private boolean isDownTrend(Double bid) {
        return !trendAnalizer.isUptrendByBid(bid);
    }

    public void setPeriods(Integer periods) {
        this.periods = periods;
    }

    public boolean isTooDangerous() {
        if (isRSITooLow() && trendAnalizer.isDownTrendLongPeriod()){
            return true;
        } else {
            return false;
        }
    }


}
