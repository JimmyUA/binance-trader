package io.github.unterstein.statistic;

import io.github.unterstein.TradingClient;
import io.github.unterstein.statistic.MACD.MACD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.minutesFromStart;

@Component
public class PriceFetchingTask implements Runnable{

    private static Logger logger = LoggerFactory.getLogger(PriceFetchingTask.class);


    @Autowired
    private PricesAccumulator pricesAccumulator;

    @Autowired
    private MACD macd;

    @Autowired
    private TradingClient tradingClient;


    @Override
    public void run() {
        try {
            double lastPrice = tradingClient.lastPrice();
            pricesAccumulator.add(lastPrice);
            minutesFromStart++;
            macd.calculateCurrentHistogram();
        } catch (Exception e){
            logger.error(e.getMessage() + "\n" + e.getStackTrace());
        }
    }
}
