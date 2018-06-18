package io.github.unterstein;

import io.github.unterstein.decision.BuyDecisionMaker;
import io.github.unterstein.decision.SellDecisionMaker;
import io.github.unterstein.statistic.MA.MovingAverage;
import io.github.unterstein.statistic.PricesAccumulator;
import io.github.unterstein.statistic.RSI.RSI;
import io.github.unterstein.statistic.TrendAnalizer;
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

    @Bean
    public TradingClient tradingClient(){
        return new TradingClient(baseCurrency, tradeCurrency, apiKey, apiSecret);
    }

    @Bean
    public TrendAnalizer trendAnalizer(){
        return new TrendAnalizer();
    }

    @Bean
    public BinanceTrader binanceTrader(){
        BinanceTrader binanceTrader = new BinanceTrader(tradingClient(), trendAnalizer());
        binanceTrader.setTradeDifference(tradeDifference);
        binanceTrader.setTradeAmount(tradeAmount);
        binanceTrader.setTradeProfit(tradeProfit);
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
        buyDecisionMaker.setPeriods(Integer.parseInt(periods));
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
}
