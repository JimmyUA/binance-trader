package io.github.unterstein.statistic;

import io.github.unterstein.TradingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.minutesFromStart;

@Component
public class PriceFetchingTask implements Runnable{

    @Autowired
    private PricesAccumulator pricesAccumulator;

    @Autowired
    private TradingClient tradingClient;


    @Override
    public void run() {
        double lastPrice = tradingClient.lastPrice();
        pricesAccumulator.add(lastPrice);
        minutesFromStart++;
    }
}
