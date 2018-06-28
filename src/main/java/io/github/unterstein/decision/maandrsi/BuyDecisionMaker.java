package io.github.unterstein.decision.maandrsi;


import io.github.unterstein.BinanceTrader;
import io.github.unterstein.statistic.MarketAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BuyDecisionMaker {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);

    @Autowired
    private MarketAnalyzer marketAnalyzer;


    public boolean isRightMomentToBuy(Double ask){
        if (priceNotNearResistanceLine(ask) && isUptrend(ask)
                && isUpTrendLongPeriod() && isRsiHighEnough()
                && isMacdAscendingBelowZero()){
            return true;
        } else {
            return false;
        }
    }

    private boolean isMacdAscendingBelowZero() {
        return marketAnalyzer.isMACDAscending();
    }

    private boolean isRsiHighEnough() {
        return marketAnalyzer.isRSIHighEnough();
    }

    public boolean priceNotNearResistanceLine(Double ask) {
        return marketAnalyzer.priceNotNearResistanceLine(ask);
    }



    private boolean isUpTrendLongPeriod() {
        return marketAnalyzer.isUpTrend();
    }



    private boolean isUptrend(Double ask) {
        return marketAnalyzer.isUptrendByAsk(ask);
    }



}
