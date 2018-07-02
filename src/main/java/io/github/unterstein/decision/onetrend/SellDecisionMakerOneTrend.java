package io.github.unterstein.decision.onetrend;

import io.github.unterstein.BinanceTrader;
import io.github.unterstein.statistic.MACD.MACD;
import io.github.unterstein.statistic.RSI.RSI;
import io.github.unterstein.statistic.TrendAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SellDecisionMakerOneTrend {

    private static Logger logger = LoggerFactory.getLogger(SellDecisionMakerOneTrend.class);


    @Autowired
    private RSI rsi;

    @Autowired
    private TrendAnalyzer trendAnalyzer;


    public boolean isTrendChanged() {
        if (isDownTrend()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isDownTrend() {
        return trendAnalyzer.isDownTrendLongPeriod();
    }

    public boolean isTooDangerous() {
        if (isRSITooLow() && trendAnalyzer.isDownTrendLongPeriod()){
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

}
