package io.github.unterstein;

import com.giffing.wicket.spring.boot.starter.app.WicketBootWebApplication;
import io.github.unterstein.decision.maandrsi.BuyDecisionMakerMARSI;
import io.github.unterstein.decision.maandrsi.SellDecisionMakerMARSI;
import io.github.unterstein.decision.macd.BuyDecisionMakerMACD;
import io.github.unterstein.decision.macd.SellDecisionMakerMACD;
import io.github.unterstein.decision.onetrend.BuyDecisionMakerOneTrend;
import io.github.unterstein.decision.onetrend.SellDecisionMakerOneTrend;
import io.github.unterstein.executor.TradeExecutor;
import io.github.unterstein.remoteManagment.RemoteManager;
import io.github.unterstein.statistic.MA.MovingAverage;
import io.github.unterstein.statistic.MACD.MACD;
import io.github.unterstein.statistic.MarketAnalyzer;
import io.github.unterstein.statistic.PriceFetchingTask;
import io.github.unterstein.statistic.PricesAccumulator;
import io.github.unterstein.statistic.RSI.RSI;
import io.github.unterstein.statistic.TrendAnalyzer;
import io.github.unterstein.strategy.MACDStrategy;
import io.github.unterstein.strategy.MAandRSIStrategy;
import io.github.unterstein.strategy.OneTrendStrategy;
import io.github.unterstein.strategy.Strategy;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.inject.Singleton;

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

    @Bean
    public TradingClient tradingClient(){
        return new TradingClient(baseCurrency, tradeCurrency, apiKey, apiSecret);
    }

    @Bean
    public TrendAnalyzer trendAnalizer(){
        return new TrendAnalyzer();
    }

    @Autowired
    private WicketBootWebApplication wicketBootWebApplication;

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
    public BuyDecisionMakerMARSI buyDecisionMaker(){
        return new BuyDecisionMakerMARSI();
    }

    @Bean
    public SellDecisionMakerMARSI sellDecisionMaker(){
        SellDecisionMakerMARSI sellDecisionMaker = new SellDecisionMakerMARSI();
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
        if (strategy.equals("MACD")) {
            return new MACDStrategy(buyDecisionMakerMACD());
        } else if (strategy.equals("ONE")){
            return new OneTrendStrategy(buyDecisionMakerOneTrend());
        }
        return new MAandRSIStrategy(buyDecisionMaker());
    }


    @Bean
    public BuyDecisionMakerMACD buyDecisionMakerMACD(){
        BuyDecisionMakerMACD buyDecisionMakerMACD = new BuyDecisionMakerMACD();
        buyDecisionMakerMACD.setMinimumHistogram(minimumHistoValue);
        return buyDecisionMakerMACD;
    }

    @Bean
    public SellDecisionMakerMACD sellDecisionMakerMACD(){
        return new SellDecisionMakerMACD();
    }

    @Bean
    public BuyDecisionMakerOneTrend buyDecisionMakerOneTrend(){
        return new BuyDecisionMakerOneTrend();
    }

    @Bean
    public SellDecisionMakerOneTrend sellDecisionMakerOneTrend(){
        return new SellDecisionMakerOneTrend();
    }


    @Bean
    public RemoteManager remoteManager(){
        return new RemoteManager();
    }

    @Bean
    @Singleton
    public WicketTester tester(){
        return new WicketTester((WebApplication) wicketBootWebApplication);
    }
}
