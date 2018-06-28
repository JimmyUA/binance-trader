package io.github.unterstein.strategy;

import io.github.unterstein.TradingClient;
import io.github.unterstein.decision.macd.BuyDecisionMakerMACD;
import io.github.unterstein.decision.macd.SellDecisionMakerMACD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.stopTicker;
import static util.Slepper.sleepSeconds;

@Component
public class MACDStrategy implements Strategy {

    private static Logger logger = LoggerFactory.getLogger(MAandRSIStrategy.class);

    private boolean isBought = false;



    @Autowired
    private TradingClient client;

    @Autowired
    private BuyDecisionMakerMACD buyDecisionMaker;

    @Autowired
    private SellDecisionMakerMACD sellDecisionMaker;
    private int tradeAmount;
    private Double boughtPrice;
    private Double goalSellPrice;
    private Double lastBid;
    private boolean wasOverGoalPrice = false;
    private double stopLossPrice;

    @Override
    public void buyProcess() {
        if (buyDecisionMaker.isRightMomentToBuy()){
            client.buyMarket(tradeAmount);
            boughtPrice = getLastAsk();
            logger.info(String.format("Bought %d coins from market! at %.8f rate", tradeAmount, boughtPrice));
            isBought = true;
        }
    }

    @Override
    public void sellProcess() {
        sleepSeconds(180);
        goalSellPrice = boughtPrice + (boughtPrice * 0.002);
        while (isUpTrending()) {
            sleepSeconds(3);
            updateLastBid();
            if (lastBid > goalSellPrice){
                wasOverGoalPrice = true;
            }
            if (stopTicker){
                return;
            }
        }
        if (wasOverGoalPrice && lastBid > minimumProfitablePrice()){
            logger.info("MACD trend changed and price was over goal price!");
            sellToMarket(lastBid);
            return;
        }
    }

    private boolean isUpTrending() {
        return sellDecisionMaker.isMACDAscending();
    }

    private void updateLastBid() {
        lastBid = getLastBid();
    }

    private double getLastBid() {
        return client.getLastBidsAverage(tradeAmount, 3);
    }

    private void sellToMarket(Double lastBid) {
        client.sellMarket(tradeAmount);
        logger.info(String.format("Sold %d coins to market! Rate: %.8f", tradeAmount, lastBid));
        logger.info(String.format("Profit %.8f", (boughtPrice - lastBid) * tradeAmount));
        isBought = false;
    }

    private double minimumProfitablePrice() {
        return boughtPrice + (boughtPrice * 0.0015);
    }

    @Override
    public void setTradeAmount(int tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    @Override
    public boolean isBought() {
        return isBought;
    }

    private double getLastAsk() {
        return client.getLastAsksAverage(tradeAmount, 3);
    }
}
