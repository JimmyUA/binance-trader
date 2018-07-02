package io.github.unterstein.decision.maandrsi;


import io.github.unterstein.BinanceTrader;
import io.github.unterstein.statistic.RSI.RSI;
import io.github.unterstein.statistic.TrendAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Component
public class SellDecisionMakerMARSI {

    private static Logger logger = LoggerFactory.getLogger(SellDecisionMakerMARSI.class);


    @Autowired
    private TrendAnalyzer trendAnalyzer;

    @Autowired
    private RSI rsi;
    private Integer periods;


    public boolean isTrendChanged(Double bid) {
        if (isDownTrend(bid)) {
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
        return !trendAnalyzer.isUptrendByBid(bid);
    }

    public void setPeriods(Integer periods) {
        this.periods = periods;
    }

    public boolean isTooDangerous() {
        if (isRSITooLow() && trendAnalyzer.isDownTrendLongPeriod()){
            return true;
        } else {
            return false;
        }
    }


}
