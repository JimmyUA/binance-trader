package io.github.unterstein.statistic.tasks;

import com.binance.api.client.domain.market.CandlestickInterval;
import geometry.prediction.PredictionHallInfo;
import geometry.prediction.Predictor;
import io.github.unterstein.BinanceTrader;
import io.github.unterstein.TradingClient;
import io.github.unterstein.persistent.entity.Spread;
import io.github.unterstein.statistic.MACD.MACD;
import io.github.unterstein.statistic.RSI.RSI;
import io.github.unterstein.statistic.StatisticDTO;
import io.github.unterstein.statistic.TrendAnalyzer;
import io.github.unterstein.statistic.amplitude.AmplitudeAnalyser;
import io.github.unterstein.statistic.spread.SpreadTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static io.github.unterstein.remoteManagment.ManagementConstants.isStartedTrading;
import static io.github.unterstein.remoteManagment.ManagementConstants.minutesFromStart;
import static io.github.unterstein.remoteManagment.ManagementConstants.strategyName;

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

    @Autowired
    private Predictor predictor;

    @Autowired
    private SpreadTracker spreadTracker;

    private Double lastPrice;

    private PredictionHallInfo predictionHallInfo;

    @Override
    public void run() {
        if(isStartedTrading) {
            try {
                lastPrice = tradingClient.lastPrice();
                updateStatisticDTO();
                addSpread();
                amplitudeAnalyser.notifyAddingPrice();
                macd.histogramm();
                macdMOMO.histogramm();
                predictionHallInfo = predictor.createPrediction(8 * 12, CandlestickInterval.FIVE_MINUTES);
                minutesFromStart++;
            } catch (Exception e) {
                logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    private void addSpread() {
        Spread spread = new Spread();
        Double ask = tradingClient.getLastAsksAverage(trader.getTradeAmount(), 3);
        Double bid = tradingClient.getLastBidsAverage(trader.getTradeAmount(), 3);
        spread.setSpread(ask - bid);
        spreadTracker.addSpread(spread);
    }

    private void updateStatisticDTO() {
        MACD workingMACD = macd;
        if(strategyName.equals("MOMO")){
            workingMACD = macdMOMO;
        }
        int tradeAmount = trader.getTradeAmount();
        statisticDTO.setLastPrice(lastPrice)
                .setLastAskAverage(tradingClient.getLastAsksAverage(tradeAmount, 3))
                .setLastBidAverage(tradingClient.getLastBidsAverage(tradeAmount, 3))
                .setDayTrend(trendAnalyzer.isDownDayTrend() ? "Down" : "UP")
                .setShortTrend(trendAnalyzer.isUpTrendShortPeriod() ? "UP" : "Down")
                .setLongTrend(trendAnalyzer.isUpTrendLongPeriod() ? "UP" : "Down")
                .setMomoTrend(trendAnalyzer.isMoMotrendUp() ? "UP" : "Down")
                .setRSI(rsi.getRSI(21))
                .setMACD(workingMACD.MACD())
                .setSignal(workingMACD.signal())
                .setHisto(workingMACD.getLastHisto())
                .setTradingBalance(tradingClient.getFreeTradingBalance());
    }
}
