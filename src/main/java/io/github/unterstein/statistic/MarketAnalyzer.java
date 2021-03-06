package io.github.unterstein.statistic;

import io.github.unterstein.TradingClient;
import io.github.unterstein.statistic.MACD.MACD;
import io.github.unterstein.statistic.RSI.RSI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.rsiPeriods;

@Component
public class MarketAnalyzer {


    private static Logger logger = LoggerFactory.getLogger(MarketAnalyzer.class);

    private static final String newLineForHTML = "<br>";

    @Autowired
    private RSI rsi;

    @Autowired
    private TradingClient tradingClient;

    @Autowired
    private TrendAnalyzer trendAnalyzer;

    @Autowired
    private MACD macd;

    private int rsiPeriod;
    private Double highestPrice;


    public void setRsiPeriod(int rsiPeriod) {
        this.rsiPeriod = rsiPeriod;
    }

    public String getMarketConditions(){
        String message = "";
        Double rsiValue = rsi.getRSI(rsiPeriods);
        Double histogram = macd.getLastHistogram();
        message += String.format("RSI %d = %.8f<br>", rsiPeriods, rsiValue);
        message += String.format("Last histogram %.11f<br>", histogram);
        message += "Is up-trend short period: " + trendAnalyzer.isUpTrendShortPeriod() + newLineForHTML;
        message += "Is up-trend long period: " + trendAnalyzer.isUpTrendLongPeriod() + newLineForHTML;
        return message;
    }


    public boolean isUpTrend() {
        return trendAnalyzer.isUpTrend();
    }

    public boolean isUptrendByAsk(Double ask) {
        return trendAnalyzer.isUptrendByAsk(ask);
    }

    public boolean isRSIHighEnough() {
        Double rsi = getRSI();
        logger.info(String.format("RSI is %.8f", rsi));
        return rsi > 50  && rsi < 70;
    }

    private Double getRSI() {
        return rsi.getRSI(rsiPeriod);
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

    public boolean isMACDAscending() {
        return macd.isAscending();
    }

    public boolean isMACDBelowLastHistogram() {
        Double lastHistogram = macd.getLastHistogram();
        Double lastMACD = macd.getLastMACD();
        String direction = "";
        if (lastMACD < lastHistogram){
            direction = "below";
            logger.info(String.format("MACD %s histogram, MACD: %.10f, histogram: %.10f",
                    direction, lastMACD, lastHistogram));
            return true;
        } else {
            direction = "over";
            logger.info(String.format("MACD %s histogram, MACD: %.10f, histogram: %.10f",
                    direction, lastMACD, lastHistogram));
            return false;
        }

    }

    public boolean isUpTrendOneTrend() {
        return trendAnalyzer.isUpTrendOneTrend();
    }

    public boolean isMaCDBelowZero() {
        Double lastMACD = macd.getLastMACD();
        String direction = "";
        String message = "MACD %s 0, MACD: %.10f";
        if (lastMACD < 0){
            direction = "below";
            logger.info(String.format(message,
                    direction, lastMACD));
            return true;
        } else {
            direction = "over";
            logger.info(String.format(message,
                    direction, lastMACD));
            return false;
        }

    }

    public boolean wasMACDCrossSignal() {
        return macd.wasMACDCrossSignalUp();
    }

    public boolean isDownTrendLongPeriod() {
        return trendAnalyzer.isDownTrendLongPeriod();
    }

    public boolean wasMACDCrossSignalDown() {
        return macd.wasMACDCrossSignalDown();
    }

    public boolean isDownTrendLongPeriodStarted() {
        return trendAnalyzer.isDownTrendLongPeriodStarted();
    }

    public boolean isDownDayTrend() {
        return trendAnalyzer.isDownDayTrend();
    }
}
