package io.github.unterstein.decision;


import io.github.unterstein.BinanceTrader;
import io.github.unterstein.TradingClient;
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
    private TrendAnalyzer trendAnalyzer;

    @Autowired
    private TradingClient tradingClient;

    @Autowired
    private RSI rsi;
    private Integer periods;
    private Double highestPrice;

    public boolean isRightMomentToBuy(Double ask){
        if (priceNotNearResistanceLine(ask) && isUptrend(ask) && isUpTrendLongPeriod() && isRSIHighEnough() ){
            return true;
        } else {
            return false;
        }
    }

    public boolean priceNotNearResistanceLine(Double ask) {
        if (highestPrice == null) {
            highestPrice = tradingClient.getHighestPrice();
        }
        Double limit = highestPrice - (highestPrice * 0.01);
        logger.info(String.format("Current price is below highest price 24 hour price: %.8f percent", (highestPrice - ask)/highestPrice * 100));
        if (ask > limit && isHighestPriceNotCrossed(ask)){
            return false;
        }
        return true;
    }

    private boolean isHighestPriceNotCrossed(Double ask) {
        if (ask < highestPrice + (highestPrice * 0.005)) {
            return true;
        } else {
            highestPrice += highestPrice * 0.015;
            return false;
        }
    }

    private boolean isUpTrendLongPeriod() {
        return trendAnalyzer.isUpTrend();
    }

    private boolean isRSIHighEnough() {
        Double rsi = getRSI();
        logger.info(String.format("RSI is %.8f", rsi));
        return rsi > 50;
    }

    private Double getRSI() {
        return this.rsi.getRSI(21);
    }

    private boolean isUptrend(Double ask) {
        return trendAnalyzer.isUptrendByAsk(ask);
    }


    public void setPeriods(Integer periods) {
        this.periods = periods;
    }

}
