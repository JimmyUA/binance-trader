package io.github.unterstein.decision;


import io.github.unterstein.BinanceTrader;
import io.github.unterstein.TradingClient;
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
    private TradingClient tradingClient;

    @Autowired
    private RSI rsi;
    private Integer periods;

    public boolean isRightMomentToBuy(Double ask){
        if (priceNotNearResistanceLine(ask) && isUptrend(ask) && isUpTrendLongPeriod() && isRSIHighEnough() ){
            return true;
        } else {
            return false;
        }
    }

    public boolean priceNotNearResistanceLine(Double ask) {
        Double highestPrice = tradingClient.getHighestPrice();
        Double limit = highestPrice - (highestPrice * 0.01);
        logger.info(String.format("Current price is below highest price 24 hour price: %.8f percent", (highestPrice - ask)/highestPrice * 100));
        if (ask > limit){
            return false;
        }
        return true;
    }

    private boolean isUpTrendLongPeriod() {
        return trendAnalizer.isUpTrend();
    }

    private boolean isRSIHighEnough() {
        Double rsi = this.rsi.getRSI(21);
        logger.info(String.format("RSI is %.8f", rsi));
        return rsi > 50;
    }

    private boolean isUptrend(Double ask) {
        return trendAnalizer.isUptrendByAsk(ask);
    }


    public void setPeriods(Integer periods) {
        this.periods = periods;
    }
}
