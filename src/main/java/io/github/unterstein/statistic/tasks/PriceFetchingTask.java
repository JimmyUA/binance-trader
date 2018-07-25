package io.github.unterstein.statistic.tasks;

import io.github.unterstein.BinanceTrader;
import io.github.unterstein.TradingClient;
import io.github.unterstein.statistic.MACD.MACD;
import io.github.unterstein.statistic.PricesAccumulator;
import io.github.unterstein.statistic.RSI.RSI;
import io.github.unterstein.statistic.StatisticDTO;
import io.github.unterstein.statistic.TrendAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static io.github.unterstein.remoteManagment.ManagementConstants.minutesFromStart;

@Component
public class PriceFetchingTask implements Runnable{

    private static Logger logger = LoggerFactory.getLogger(PriceFetchingTask.class);


    @Autowired
    private PricesAccumulator pricesAccumulator;

    @Autowired
    private MACD macd;

    @Autowired
    private RSI rsi;

    @Autowired
    private TradingClient tradingClient;

    @Autowired
    private BinanceTrader trader;

    @Autowired
    private StatisticDTO statisticDTO;

    @Autowired
    private TrendAnalyzer trendAnalyzer;

    private Double lastPrice;

    @Override
    public void run() {
        try {
            lastPrice = tradingClient.lastPrice();
            pricesAccumulator.add(lastPrice);
            minutesFromStart++;
            macd.calculateCurrentHistogram();
            updateStatisticDTO();
        } catch (Exception e){
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    private void updateStatisticDTO() {
        int tradeAmount = trader.getTradeAmount();
        statisticDTO.setLastPrice(lastPrice)
                .setLastAskAverage(tradingClient.getLastAsksAverage(tradeAmount, 3))
                .setLastBidAverage(tradingClient.getLastBidsAverage(tradeAmount, 3))
                .setDayTrend(trendAnalyzer.isDownDayTrend() ? "Down" : "UP")
                .setShortTrend(trendAnalyzer.isUpTrendShortPeriod() ? "UP" : "Down")
                .setLongTrend(trendAnalyzer.isUpTrendLongPeriod() ? "UP" : "Down")
                .setRSI(rsi.getRSI(21))
                .setMACD(macd.getLastMACD())
                .setSignal(macd.getLastSignal())
                .setTradingBalance(tradingClient.getFreeTradingBalance());
    }
}