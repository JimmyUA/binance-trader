package io.github.unterstein.botlogic.strategy;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.botlogic.decision.momo.BuyDecisionMakerMoMo;
import io.github.unterstein.botlogic.decision.momo.SellDecisionMakerMoMo;
import io.github.unterstein.statistic.spread.SpreadTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.isSpreadTrackingIncluded;
import static io.github.unterstein.remoteManagment.ManagementConstants.stopTicker;
import static io.github.unterstein.statistic.EMA.ExponentialMovingAverage.EMA;
import static util.Slepper.sleepSeconds;

@Component
public class MoMoStrategy extends AbstractStrategy {

    @Autowired
    private SellDecisionMakerMoMo sellDecisionMaker;

    @Autowired
    private SpreadTracker spreadTracker;

    @Autowired
    public MoMoStrategy(BuyDecisionMakerMoMo buyDecisionMaker) {
        this.buyDecisionMaker = buyDecisionMaker;
    }

    private double lastPrice;

    private Boolean halfNotSold = true;

    @Override
    public void buyProcess() {
        double lastPrice = getLastPrice();
        double lastAsk = getLastAsk();
        if (buyDecisionMaker.isRightMomentToBuy(lastPrice)) {
            super.executePurchase(lastAsk);
        }
    }

    private double getLastPrice() {
        return client.lastPrice();
    }

    @Override
    public void sellProcess() {
        halfNotSold = true;
        initStopLoss();
        goalSellPrice = boughtPrice + (boughtPrice - stopLossPrice);

        while (true) {
            if (stopTicker) {
                return;
            }
            sleepSeconds(3);
            updateLastPrice();
            updateLastBid();

            if (halfNotSold && isTimeToSellFirstHalf()) {
                logger.info(String.format("Last price: %.8f is over goal price: %.8f, percentage: %.2f, selling first half",
                        lastBid, goalSellPrice, ((lastPrice - goalSellPrice) / goalSellPrice) * 100));
                sellToMarketHalf();
                halfNotSold = false;
            } else if (!halfNotSold && isTimeToSellSecondHalf()) {
                sellToMarketSecondHalf();
                break;
            }
            if (sellDecisionMaker.isCrossedStopLoss(stopLossPrice, lastPrice)) {
                sellToMarket();
                break;
            }

        }
        amplitudeAnalyser.stop();
    }

    private void sellToMarketSecondHalf() {
        updateLastBid();
        client.sellMarket(tradeAmount / 2);
        tradeService.addHalfSellOrder(lastBid);
        logger.info(String.format("Sold %d coins to market! Rate: %.8f", tradeAmount, lastBid));
        logger.info(String.format("Profit %.8f", (boughtPrice - lastBid) * tradeAmount / 2));
        isBought = false;
        wasOverGoalPrice = false;
    }


    private boolean isTimeToSellFirstHalf() {
        return lastBid > goalSellPrice;
    }


    private boolean isTimeToSellSecondHalf() {
        double ema20 = EMA(20, CandlestickInterval.FIVE_MINUTES);
        double secondHalfStopLoss = ema20 - (ema20 * (0.01 + getSpreadOrZero(60)));
        return lastPrice <= secondHalfStopLoss;
    }

    private void sellToMarketHalf() {
        updateLastPrice();
        updateLastBid();
        int half = tradeAmount / 2;
        client.sellMarket(half);
        tradeService.addHalfSellOrder(lastBid);
        tradeService.initBuyOrderAfterHalfTrade();
        logger.info(String.format("Sold %d coins to market! Rate: %.8f", half, lastBid));
        logger.info(String.format("Profit %.8f", (boughtPrice - lastBid) * tradeAmount / 2));
    }

    private void updateLastPrice() {
        lastPrice = client.lastPrice();
    }

    private void initStopLoss() {
        Double trackedEMA20 = ((BuyDecisionMakerMoMo) buyDecisionMaker).getTrackedEMA20();
        stopLossPrice = trackedEMA20 - (trackedEMA20 * (0.001 + getSpreadOrZero(60)));
    }

    protected double getSpreadOrZero(int period) {
        return isSpreadTrackingIncluded ? spreadTracker.getAverageForPeriod(period) : 0.0;
    }

}
