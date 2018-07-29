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



    public boolean isTimeToTryToSell() {
        return marketAnalyzer.isDownTrendLongPeriodStarted() || (marketAnalyzer.wasMACDCrossSignalDown() && isMACDAboveZero());
    }

    private boolean isMACDAboveZero() {
        return marketAnalyzer.isMaCDBelowZero();
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
        if (ManagementConstants.isMACDStopLossAllowed && marketAnalyzer.isMaCDBelowZero()){
            logger.info("MACD fall below zero, no sence to wait profit here!");
            return true;
        }
        return false;
    }
}
