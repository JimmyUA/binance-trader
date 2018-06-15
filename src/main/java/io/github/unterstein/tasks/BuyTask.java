package io.github.unterstein.tasks;

import io.github.unterstein.BinanceTrader;
import io.github.unterstein.TradingClient;
import io.github.unterstein.statistic.TrendAnalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuyTask implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);


    @Autowired
    TradingClient client;

    @Autowired
    TrendAnalizer trendAnalizer;

    private Double goalSellPrice;
    private Double boughtPrice;
    private Integer tradeAmount;

    public BuyTask setBoughtPrice(Double boughtPrice) {
        this.boughtPrice = boughtPrice;
        return this;
    }

    public BuyTask setTradeAmount(Integer tradeAmount) {
        this.tradeAmount = tradeAmount;
        return this;
    }

    @Override
    public void run() {
        int rightMomentCounter = 0;
        Double lastBid = getLastBid();
        goalSellPrice = boughtPrice + (boughtPrice * 0.2 / 100);
        Double stopLossPrice = boughtPrice - (boughtPrice * 1.5 / 100);
        while (lastBid < goalSellPrice || trendAnalizer.isUptrendByBid(lastBid)) {
            logger.info(String.format("waiting price to rise enough, difference %.8f, Last maxBid: %.8f, goalSellPrice: %.8f\"",
                    goalSellPrice - lastBid, lastBid, goalSellPrice));
            sleepSeconds(3);
            lastBid = getLastBid();
            rightMomentCounter++;
            //if bid is too low - set limit order
            if (rightMomentCounter > 360 || lastBid < stopLossPrice) {
                break;
            }
        }
        client.sellMarket(tradeAmount);
        logger.info(String.format("Sold %d coins to market! Rate: %.8f", tradeAmount, lastBid));
        logger.info(String.format("Profit %.8f", (boughtPrice - lastBid) * tradeAmount));
    }

    private Double getLastBid() {
        return client.getLastBid();
    }

    private void sleepSeconds(int i) {
    }
}
