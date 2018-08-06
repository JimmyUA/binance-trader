package io.github.unterstein.botlogic.decision.momo;

import io.github.unterstein.botlogic.decision.BuyDecisionMaker;
import io.github.unterstein.botlogic.decision.onetrend.BuyDecisionMakerOneTrend;
import io.github.unterstein.statistic.MarketAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.*;

@Component
public class BuyDecisionMakerMoMo implements BuyDecisionMaker{


    private static Logger logger = LoggerFactory.getLogger(BuyDecisionMakerOneTrend.class);

    @Autowired
    private MarketAnalyzer marketAnalyzer;

    @Override
    public boolean isRightMomentToBuy(Double ask) {
        if (dayTrendLimit()){
            logger.info("Trades are not allowed on down day trend!!!");
            return false;
        } else if(longMACDLimit()){
            logger.info("Long MACD is below Signal, trades are not allowed");
            return false;
        } else if(resistanceLineLimit(ask)){
            return false;
        } else if(isNegativeMACDRequired && isMACDOverZero()){
            return false;
        } else if(isMoMoTrendUp() && momoMACDHistogramCrossedZeroUp()){

        }
        return false;
    }

    private boolean momoMACDHistogramCrossedZeroUp() {
        return marketAnalyzer.momoMACDHistogramCrossedZeroUp();
    }

    private boolean isMoMoTrendUp() {
        return marketAnalyzer.isMoMoTrendUp();
    }

    private boolean longMACDLimit() {
        return isLongMACDIncluded && marketAnalyzer.wasLongMACDCrossSignalDown();
    }

    private boolean dayTrendLimit() {
        return isTradesOnDownDayTrendForbidden && marketAnalyzer.isDownDayTrend();
    }

    private boolean wasMACDCrossSignal() {
        return marketAnalyzer.wasMACDCrossSignalUp();
    }

    private boolean isMACDOverZero() {
        return !marketAnalyzer.isMaCDBelowZero();
    }

    private boolean isUpTrend() {
        return marketAnalyzer.isUpTrendOneTrend();
    }

    private boolean resistanceLineLimit(Double ask) {
        return isResistanceLineIncluded && marketAnalyzer.priceNearResistanceLine(ask, 3 * 60);
    }
}
