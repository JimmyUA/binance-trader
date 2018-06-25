package io.github.unterstein.statistic;

import io.github.unterstein.statistic.MACD.MACD;
import io.github.unterstein.statistic.RSI.RSI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.rsiPeriods;

@Component
public class MarketAnalyzer {

    private static final String newLineForHTML = "<br>";

    @Autowired
    private RSI rsi;

    @Autowired
    private TrendAnalyzer trendAnalyzer;

    @Autowired
    private MACD macd;

    public String getMarketConditions(){
        String message = "";
        Double rsiValue = rsi.getRSI(rsiPeriods);
        Double histogram = macd.getLastHistogram();
        message += String.format("RSI %d = %.8f<br>", rsiPeriods, rsiValue);
        message += String.format("Last histogram %.8f<br>", histogram);
        message += "Is up-trend short period: " + trendAnalyzer.isUpTrendShortPeriod() + newLineForHTML;
        message += "Is up-trend long period: " + trendAnalyzer.isUpTrendLongPeriod() + newLineForHTML;
        return message;
    }


}
