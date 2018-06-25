package io.github.unterstein.decision;


import io.github.unterstein.BinanceTrader;
import io.github.unterstein.TradingClient;
import io.github.unterstein.statistic.MarketAnalyzer;
import io.github.unterstein.statistic.RSI.RSI;
import io.github.unterstein.statistic.TrendAnalyzer;
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
        if (priceNotNearResistanceLine(ask) && isUptrend(ask) && isUpTrendLongPeriod() && marketAnalyzer.isRSIHighEnough()){
            return true;
        } else {
            return false;
        }
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
