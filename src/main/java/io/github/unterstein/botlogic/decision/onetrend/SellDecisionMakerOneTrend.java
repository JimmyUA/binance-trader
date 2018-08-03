package io.github.unterstein.botlogic.decision.onetrend;

import io.github.unterstein.remoteManagment.ManagementConstants;
import io.github.unterstein.statistic.MarketAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SellDecisionMakerOneTrend {

    private static Logger logger = LoggerFactory.getLogger(SellDecisionMakerOneTrend.class);


    @Autowired
    MarketAnalyzer marketAnalyzer;

    protected boolean wasMACDOverZero = false;


    public boolean isTimeToTryToSell() {
        return marketAnalyzer.isDownTrendLongPeriodStarted() || (marketAnalyzer.wasMACDCrossSignalDown() && isMACDAboveZero());
    }

    private boolean isMACDAboveZero() {
        return !marketAnalyzer.isMaCDBelowZero();
    }

    public boolean isCrossedStopLoss(double stopLossPrice, Double lastBid) {
        if (lastBid < stopLossPrice){
            logger.info(String.format(
                    "Too dangerous too keep holding coins lastBid: %.8f lower than stop loss: %.8f and price keep fail",
                    lastBid, stopLossPrice));
            return true;

        } else if(marketAnalyzer.isDownDayTrend()){
            logger.info(
                    "Too dangerous too keep holding coins on DOWN day trend");
            return true;
        }else {
            return false;
        }
    }


    public boolean isNeedToSellByMACD() {
        boolean isMACDBelowZero = marketAnalyzer.isMaCDBelowZero();
        if(!isMACDBelowZero){
            wasMACDOverZero = true;
        }
        if (ManagementConstants.isMACDStopLossAllowed && isMACDBelowZero && wasMACDOverZero){
            logger.info("MACD fall below zero, no sense to wait profit here!");
            wasMACDOverZero = false;
            return true;
        }
        return false;
    }

    protected void setMarketAnalyzer(MarketAnalyzer marketAnalyzer) {
        this.marketAnalyzer = marketAnalyzer;
    }
}
