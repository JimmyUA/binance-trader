package io.github.unterstein.strategy;

import io.github.unterstein.TradingClient;
import io.github.unterstein.decision.maandrsi.BuyDecisionMaker;
import io.github.unterstein.decision.maandrsi.SellDecisionMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.stopTicker;
import static util.Slepper.sleepSeconds;

@Component
public class MAandRSIStrategy implements Strategy{

    private static Logger logger = LoggerFactory.getLogger(MAandRSIStrategy.class);

    private boolean isBought = false;

    @Autowired
    private TradingClient client;

    @Autowired
    private BuyDecisionMaker buyDecisionMaker;

    @Autowired
    private SellDecisionMaker sellDecisionMaker;

    private int tradeAmount;
    private Double boughtPrice;
    private Double goalSellPrice;
    private Double lastBid;
    private boolean wasOverGoalPrice = false;
    private double stopLossPrice;

    @Override
    public void setTradeAmount(int tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    @Override
    public boolean isBought() {
        return isBought;
    }

    @Override
    public void buyProcess() {
        if (buyDecisionMaker.isRightMomentToBuy(getLastAsk())) {
            client.buyMarket(tradeAmount);
            boughtPrice = getLastAsk();
            logger.info(String.format("Bought %d coins from market! at %.8f rate", tradeAmount, boughtPrice));
            isBought = true;
        }
    }

    private double getLastAsk() {
        return client.getLastAsksAverage(tradeAmount, 3);
    }

    @Override
    public void sellProcess() {
        goalSellPrice = boughtPrice + (boughtPrice * 0.002);
        while (isUpTrending()) {
            sleepSeconds(3);
            updateLastBid();
            if (lastBid > goalSellPrice){
                wasOverGoalPrice = true;
            }
            logger.info(String.format("Market in Up-trend still, difference %.8f, Last maxBid: %.8f, goalSellPrice: %.8f\"",
                    goalSellPrice - lastBid, lastBid, goalSellPrice));
            if (stopTicker){
                return;
            }
        }
        if (wasOverGoalPrice && lastBid > minimumProfitablePrice()){
            logger.info("Trend changed and price was over goal price!");
            sellToMarket(lastBid);
            return;
        }
        goalSellPrice = goalSellPrice + (0.002 * goalSellPrice);
        stopLossPrice = boughtPrice - (boughtPrice * 0.015);
        while (true) {
            logger.info("Trend changed and price did not rise enough, waiting");
            sleepSeconds(3);
            updateLastBid();
            logger.info(String.format("Waiting while reach goal price, difference %.8f, Last maxBid: %.8f, goalSellPrice: %.8f\"",
                    goalSellPrice - lastBid, lastBid, goalSellPrice));
            if (lastBid > goalSellPrice) {
                logger.info("price is high enough");
                sellToMarket(lastBid);
                sleepSeconds(180);
                break;
            } else if (lastBid < stopLossPrice && sellDecisionMaker.isTooDangerous()) {
                logger.info(String.format("Too dangerous too keep holding coins lastBid: %.8f lower than stop loss: %.8f and price keep fail", lastBid, stopLossPrice));
                sellToMarket(lastBid);
            }
            if (stopTicker){
                return;
            }
        }

    }

    private boolean isUpTrending() {
        return !sellDecisionMaker.isTrendChanged(lastBid);
    }

    private void updateLastBid() {
        lastBid = getLastBid();
    }

    private double getLastBid() {
        return client.getLastBidsAverage(tradeAmount, 3);
    }

    private double minimumProfitablePrice() {
        return boughtPrice + (boughtPrice * 0.0015);
    }

    private void sellToMarket(Double lastBid) {
        client.sellMarket(tradeAmount);
        logger.info(String.format("Sold %d coins to market! Rate: %.8f", tradeAmount, lastBid));
        logger.info(String.format("Profit %.8f", (boughtPrice - lastBid) * tradeAmount));
        isBought = false;
    }
}
