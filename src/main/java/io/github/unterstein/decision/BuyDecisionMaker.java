package io.github.unterstein.decision;


import io.github.unterstein.BinanceTrader;
import io.github.unterstein.statistic.RSI.RSI;
import io.github.unterstein.statistic.TrendAnalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BuyDecisionMaker {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);


    @Autowired
    private TrendAnalizer trendAnalizer;

    @Autowired
    private RSI rsi;

    public boolean isRightMomentToBuy(Double ask){
        if (isUptrend(ask) && isRSIHighEnough() && isUpTrendLongPeriod()){
            return true;
        } else {
            return false;
        }
    }

    private boolean isUpTrendLongPeriod() {
        return trendAnalizer.isUpTrend();
    }

    private boolean isRSIHighEnough() {
        Double rsi = this.rsi.getRSI(14);
        logger.info(String.format("RSI is %.8f", rsi));
        return rsi > 50;
    }

    private boolean isUptrend(Double ask) {
        return trendAnalizer.isUptrendByAsk(ask);
    }


}
