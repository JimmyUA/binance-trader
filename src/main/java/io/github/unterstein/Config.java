package io.github.unterstein;

import io.github.unterstein.decision.BuyDecisionMaker;
import io.github.unterstein.decision.SellDecisionMaker;
import io.github.unterstein.executor.TradeExecutor;
import io.github.unterstein.statistic.MA.MovingAverage;
import io.github.unterstein.statistic.MACD.MACD;
import io.github.unterstein.statistic.MarketAnalyzer;
import io.github.unterstein.statistic.PriceFetchingTask;
import io.github.unterstein.statistic.PricesAccumulator;
import io.github.unterstein.statistic.RSI.RSI;
import io.github.unterstein.statistic.TrendAnalyzer;
import io.github.unterstein.strategy.MAandRSIStrategy;
import io.github.unterstein.strategy.Strategy;
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

    @Value("${API_KEY:33dlv4RMYPnGDVifHfouwmRPr06AxboXbaMVGFOJClFiOaEEEQyCQ1fHEz2MQJRv}")
    private String apiKey;

    @Value("${API_SECRET:fkIlfKwNV3YX0l3PUBoCyjffnhoa86M5vQ5ZSjhMUou8RI2oQeD1zz9YEfdGpB0y}")
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

    @Bean
    public TradingClient tradingClient(){
        return new TradingClient(baseCurrency, tradeCurrency, apiKey, apiSecret);
    }

    @Bean
    public TrendAnalyzer trendAnalizer(){
        return new TrendAnalyzer();
    }

    @Bean
    public BinanceTrader binanceTrader(){
        BinanceTrader binanceTrader = new BinanceTrader(tradingClient());
        binanceTrader.setTradeAmount(tradeAmount);
        binanceTrader.setTradeProfit(tradeProfit);
        binanceTrader.setSpreadDifference(spreadDifference);
        return binanceTrader;
    }

    @Bean
    public MovingAverage movingAverage(){
        return new MovingAverage();
    }

    @Bean
    public PricesAccumulator pricesAccumulator(){
        return new PricesAccumulator();
    }

    @Bean
    public BuyDecisionMaker buyDecisionMaker(){
        BuyDecisionMaker buyDecisionMaker = new BuyDecisionMaker();
        return buyDecisionMaker;
    }

    @Bean
    public SellDecisionMaker sellDecisionMaker(){
        SellDecisionMaker sellDecisionMaker = new SellDecisionMaker();
        sellDecisionMaker.setPeriods(Integer.parseInt(periods));
        return sellDecisionMaker;
    }

    @Bean
    public RSI rsi(){
        return new RSI();
    }

    @Bean
    public MACD macd(){
        return new MACD(shortPeriod, longPeriod, signalPeriod);
    }

    @Bean
    public PriceFetchingTask priceFetchingTask(){
        return new PriceFetchingTask();
    }

    @Bean
    public MarketAnalyzer marketAnalyzer(){
        return new MarketAnalyzer();
    }

    @Bean
    public TradeExecutor tradeExecutor(){
        TradeExecutor tradeExecutor = new TradeExecutor(strategy());
        tradeExecutor.setTradeAmount(tradeAmount);
        return tradeExecutor;
    }

    @Bean
    public Strategy strategy(){
        return new MAandRSIStrategy();
    }
}
