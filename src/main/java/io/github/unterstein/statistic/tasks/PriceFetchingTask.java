package io.github.unterstein.statistic.tasks;

import io.github.unterstein.BinanceTrader;
import io.github.unterstein.TradingClient;
import io.github.unterstein.statistic.MACD.MACD;
import io.github.unterstein.statistic.PricesAccumulator;
import io.github.unterstein.statistic.RSI.RSI;
import io.github.unterstein.statistic.StatisticDTO;
import io.github.unterstein.statistic.TrendAnalyzer;
import io.github.unterstein.statistic.amplitude.AmplitudeAnalyser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static io.github.unterstein.remoteManagment.ManagementConstants.isStartedTrading;
import static io.github.unterstein.remoteManagment.ManagementConstants.minutesFromStart;

@Component
public class PriceFetchingTask implements Runnable{

    private static Logger logger = LoggerFactory.getLogger(PriceFetchingTask.class);


    @Autowired
    @Qualifier("short")
    private MACD macd;

    @Autowired
    @Qualifier("momo")
    private MACD macdMOMO;

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

    @Autowired
    private AmplitudeAnalyser amplitudeAnalyser;

    private Double lastPrice;

    @Override
    public void run() {
        if(isStartedTrading) {
            try {
                lastPrice = tradingClient.lastPrice();
                updateStatisticDTO();
                amplitudeAnalyser.notifyAddingPrice();
                macd.histogramm();
                macdMOMO.histogramm();
                minutesFromStart++;
            } catch (Exception e) {
                logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
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
                .setMomoTrend(trendAnalyzer.isMoMotrendUp() ? "UP" : "Down")
                .setRSI(rsi.getRSI(21))
                .setMACD(macd.MACD())
                .setSignal(macd.signal())
                .setHisto(macd.getLastHisto())
                .setTradingBalance(tradingClient.getFreeTradingBalance());
    }
}
