package io.github.unterstein.botlogic.decision.momo;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.botlogic.decision.BuyDecisionMaker;
import io.github.unterstein.botlogic.decision.onetrend.BuyDecisionMakerOneTrend;
import io.github.unterstein.statistic.MarketAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.*;
import static io.github.unterstein.statistic.EMA.ExponentialMovingAverage.EMA;
import static util.Slepper.sleepSeconds;

@Component
public class BuyDecisionMakerMoMo implements BuyDecisionMaker {


    private static Logger logger = LoggerFactory.getLogger(BuyDecisionMakerOneTrend.class);

    private Double trackedEMA20;

    @Autowired
    private MarketAnalyzer marketAnalyzer;

    @Override
    public boolean isRightMomentToBuy(Double ask) {
        if (resistanceLineLimit(ask)) {
            return false;
        } else if (isMoMoTrendUp() && momoMACDHistogramCrossedZeroUp()) {
            int time = 0;
            while (time < 5 * 60 * 60) {
                trackedEMA20 = EMA(20, CandlestickInterval.FIVE_MINUTES);
                if (ask > trackedEMA20 + trackedEMA20 * 0.005) {
                    logger.info("Burst detected after histo crossed 0");
                    return true;
                }
                sleepSeconds(3);
                time += 3;
            }
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


    private boolean isMACDOverZero() {
        return !marketAnalyzer.isMaCDBelowZero();
    }


    private boolean resistanceLineLimit(Double ask) {
        return isResistanceLineIncluded && marketAnalyzer.priceNearResistanceLine(ask, 3 * 60);
    }

    public Double getTrackedEMA20() {
        return trackedEMA20;
    }
}
