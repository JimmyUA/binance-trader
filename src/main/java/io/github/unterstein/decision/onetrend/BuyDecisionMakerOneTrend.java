package io.github.unterstein.decision.onetrend;

import io.github.unterstein.decision.BuyDecisionMaker;
import io.github.unterstein.statistic.MACD.MACD;
import io.github.unterstein.statistic.MarketAnalyzer;
import io.github.unterstein.statistic.TrendAnalyzer;
import io.github.unterstein.strategy.MAandRSIStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static util.Slepper.sleepSeconds;

public class BuyDecisionMakerOneTrend implements BuyDecisionMaker {

    private static Logger logger = LoggerFactory.getLogger(MAandRSIStrategy.class);

    @Autowired
    private MarketAnalyzer marketAnalyzer;


    @Override
    public boolean isRightMomentToBuy(Double ask) {
       if (priceNotNearResistanceLine(ask) && isMACDBelowZero() &&
               wasMACDCrossSignal() && isUpTrend() &&
               isRsiHighEnough() && isMacdAscending()){
           return true;
       } else {
           return false;
       }
    }

    private boolean wasMACDCrossSignal() {
        return marketAnalyzer.wasMACDCrossSignal();
    }

    private boolean isMACDBelowZero() {
        return marketAnalyzer.isMaCDBelowZero();
    }

    private boolean isUpTrend() {
        return marketAnalyzer.isUpTrendOneTrend();
    }


    private boolean isMacdAscending() {
        return marketAnalyzer.isMACDAscending();
    }

    private boolean isRsiHighEnough() {
        return marketAnalyzer.isRSIHighEnough();
    }

    public boolean priceNotNearResistanceLine(Double ask) {
        return marketAnalyzer.priceNotNearResistanceLine(ask);
    }

}
