package io.github.unterstein;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.botlogic.decision.maandrsi.BuyDecisionMakerMARSI;
import io.github.unterstein.botlogic.decision.maandrsi.SellDecisionMakerMARSI;
import io.github.unterstein.botlogic.decision.momo.BuyDecisionMakerMoMo;
import io.github.unterstein.botlogic.decision.momo.SellDecisionMakerMoMo;
import io.github.unterstein.botlogic.decision.onetrend.BuyDecisionMakerOneTrend;
import io.github.unterstein.botlogic.decision.onetrend.SellDecisionMakerOneTrend;
import io.github.unterstein.botlogic.executor.TradeExecutor;
import io.github.unterstein.botlogic.strategy.MAandRSIStrategy;
import io.github.unterstein.botlogic.strategy.MoMoStrategy;
import io.github.unterstein.botlogic.strategy.OneTrendStrategy;
import io.github.unterstein.botlogic.strategy.Strategy;
import io.github.unterstein.remoteManagment.RemoteManager;
import io.github.unterstein.statistic.MA.MovingAverage;
import io.github.unterstein.statistic.MACD.MACD;
import io.github.unterstein.statistic.MarketAnalyzer;
import io.github.unterstein.statistic.PricesAccumulator;
import io.github.unterstein.statistic.TrendAnalyzer;
import io.github.unterstein.statistic.tasks.PriceFetchingTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Value("${TRADE_DIFFERENCE}")
    private double tradeDifference;

    @Value("${TRADE_PROFIT}")
    private double tradeProfit;

    @Value("${TRADE_AMOUNT}")
    private int tradeAmount;

    @Value("${BASE_CURRENCY}")
    private String baseCurrency;

    @Value("${TRADE_CURRENCY}")
    private String tradeCurrency;

    @Value("${API_KEY:8L4iCDQJXAvxE9kbFaAVh01dlw2mBOUtPeyKseEXzdSeRvVG8YwF887w77qtoWV1}")
    private String apiKey;

    @Value("${API_SECRET:uunJlIIW21rEcu16DWIowNc5xQqTKhxS5VquBgcT08cvMNzzDvkFw3aOAicDjCVW}")
    private String apiSecret;

    @Value("${RSI_PERIODS}")
    private String periods;

    @Value("${SPREAD_DIFFERENCE}")
    private double spreadDifference;

    //MACD properties

    @Value("${MACD.SHORT_PERIOD}")
    private int shortPeriod;

    @Value("${MACD.LONG_PERIOD}")
    private int longPeriod;

    @Value("${MACD.SIGNAL_PERIOD}")
    private int signalPeriod;

    @Value("${MACD.STRATEGY.MINIMUM_HISTO}")
    private double minimumHistoValue;

    @Value("${STRATEGY}")
    private String strategy;

    @Value("${MACD.LONG.SHORT_PERIOD}")
    private int shortPeriodLong;

    @Value("${MACD.LONG.LONG_PERIOD}")
    private int longPeriodLong;

    @Value("${MACD.LONG.SIGNAL_PERIOD}")
    private int signalPeriodLong;

    @Bean
    public TradingClient tradingClient() {
        return new TradingClient(baseCurrency, tradeCurrency, apiKey, apiSecret);
    }

    @Bean
    public TrendAnalyzer trendAnalizer() {
        return new TrendAnalyzer();
    }

    @Bean
    public BinanceTrader binanceTrader() {
        BinanceTrader binanceTrader = new BinanceTrader(tradingClient());
        binanceTrader.setTradeAmount(tradeAmount);
        binanceTrader.setTradeProfit(tradeProfit);
        binanceTrader.setSpreadDifference(spreadDifference);
        return binanceTrader;
    }

    @Bean
    public MovingAverage movingAverage() {
        return new MovingAverage();
    }

    @Bean
    public PricesAccumulator pricesAccumulator() {
        return new PricesAccumulator();
    }

    @Bean
    public BuyDecisionMakerMARSI buyDecisionMaker() {
        return new BuyDecisionMakerMARSI();
    }

    @Bean
    public SellDecisionMakerMARSI sellDecisionMaker() {
        SellDecisionMakerMARSI sellDecisionMaker = new SellDecisionMakerMARSI();
        sellDecisionMaker.setPeriods(Integer.parseInt(periods));
        return sellDecisionMaker;
    }


    @Bean(name = "short")
    public MACD macd() {
        MACD shortMacd = new MACD(shortPeriod, longPeriod, signalPeriod);
        shortMacd.setName("Short");
        return shortMacd;
    }

    @Bean(name = "long")
    public MACD longMacd() {
        MACD longMacd = new MACD(shortPeriodLong, longPeriodLong, signalPeriodLong);
        longMacd.setName("Long");
        return longMacd;
    }

    @Bean(name = "momo")
    public MACD momoMACD() {
        MACD momMACD = new MACD(12, 26, 9);
        momMACD.setName("MoMo");
        momMACD.setCandlestickInterval(CandlestickInterval.FIVE_MINUTES);
        return momMACD;
    }

    @Bean
    public PriceFetchingTask priceFetchingTask() {
        return new PriceFetchingTask();
    }

    @Bean
    public MarketAnalyzer marketAnalyzer() {
        return new MarketAnalyzer();
    }

    @Bean
    public TradeExecutor tradeExecutor() {
        TradeExecutor tradeExecutor = new TradeExecutor(strategy());
        tradeExecutor.setTradeAmount(tradeAmount);
        return tradeExecutor;
    }

    @Bean
    public Strategy strategy() {
        if (strategy.equals("ONE")) {
            return new OneTrendStrategy(buyDecisionMakerOneTrend());
        } else if(strategy.equals("MOMO")){
            return new MoMoStrategy(buyDecisionMakerMoMo());
        }
        return new OneTrendStrategy(buyDecisionMakerOneTrend());
    }


    @Bean
    public BuyDecisionMakerOneTrend buyDecisionMakerOneTrend() {
        return new BuyDecisionMakerOneTrend();
    }

    @Bean
    public SellDecisionMakerOneTrend sellDecisionMakerOneTrend() {
        return new SellDecisionMakerOneTrend();
    }


    @Bean
    public SellDecisionMakerMoMo sellDecisionMakerMoMo() {
        return new SellDecisionMakerMoMo();
    }

    @Bean
    public BuyDecisionMakerMoMo buyDecisionMakerMoMo() {
        return new BuyDecisionMakerMoMo();
    }

    @Bean
    public RemoteManager remoteManager() {
        return new RemoteManager();
    }


}
