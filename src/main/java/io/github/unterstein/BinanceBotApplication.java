package io.github.unterstein;

import com.binance.api.client.domain.account.AssetBalance;
import io.github.unterstein.infoAccumulator.LastPriceVSOrderBook;
import io.github.unterstein.remoteManagment.ManagementConstants;
import io.github.unterstein.statistic.MarketAnalyzer;
import io.github.unterstein.statistic.PriceFetchingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.github.unterstein.remoteManagment.ManagementConstants.shutDown;
import static io.github.unterstein.remoteManagment.ManagementConstants.sleepSomeTime;
import static io.github.unterstein.remoteManagment.ManagementConstants.stopTicker;

@EnableScheduling
@SpringBootApplication
@RestController("/")
public class BinanceBotApplication {

  private static Logger logger = LoggerFactory.getLogger(BinanceBotApplication.class);

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
  private int rsiPeriods;

  @Value("${SPREAD_DIFFERENCE}")
  private double spreadDifference;

  @Value("${STRATEGY}")
  private String strategy;

  static LastPriceVSOrderBook lastPriceVSOrderBook;


  @Autowired
  private TradingClient tradingClient;

  @Autowired
  private BinanceTrader trader;

  @Autowired
  private PriceFetchingTask priceFetchingTask;

  @Autowired
  private MarketAnalyzer marketAnalyzer;


  @PostConstruct
  public void init() {
    marketAnalyzer.setRsiPeriod(rsiPeriods);
    ManagementConstants.rsiPeriods = rsiPeriods;
    logger.info(String.format("Starting app with diff=%.8f, profit=%.8f amount=%d base=%s trade=%s", tradeDifference, tradeProfit, tradeAmount, baseCurrency, tradeCurrency));
    logger.info(String.format("Using strategy: %s", strategy.equals("") ? "MA default" : strategy));
    lastPriceVSOrderBook = new LastPriceVSOrderBook(tradingClient);
    logger.info("Starting fetching prices every minute");
    ScheduledExecutorService service = Executors
            .newSingleThreadScheduledExecutor();
    service.scheduleAtFixedRate(priceFetchingTask, 0, 1, TimeUnit.MINUTES);
  }

  // tick every 2 seconds
  @Scheduled(fixedRate = 2000)
  public void schedule() {
    trader.tick();
  }

//    @Scheduled(fixedRate = 1000)
//  public void info() {
//      lastPriceVSOrderBook.capturePriceAndOrderBookEntries();
//  }


  @RequestMapping("/")
  public List<AssetBalance> getBalances() {
    return trader.getBalances().stream().filter(assetBalance -> !assetBalance.getFree().startsWith("0.0000")).collect(Collectors.toList());
  }

  @RequestMapping("/stop")
  public String stop() {
    shutDown = true;
    return "Stopped!";
  }

  @RequestMapping("/sellAll")
  public String sellAll() {
    String tradingBalance = tradingClient.getTradingBalance().getFree();
    Double tradingBalanceDoubleValue = Double.parseDouble(tradingBalance);
    tradingClient.sellMarket(tradingBalanceDoubleValue.intValue());
    stopTicker = true;
    sleepSomeTime = true;
    return "All trading balance is sold!";
  }

  @RequestMapping("/buy")
  public String buy() {
    tradingClient.buyMarket(tradeAmount);
    String lastAsk = tradingClient.getOrderBook().getAsks().get(0).getPrice();
    return String.format("Bought %d coins at price %s", tradeAmount, lastAsk);
  }

  @RequestMapping("/sell")
  public String sell() {
    tradingClient.sellMarket(tradeAmount);
    String lastBid = tradingClient.getOrderBook().getBids().get(0).getPrice();
    stopTicker = true;
    sleepSomeTime = true;
    return String.format("Sold %d coins at price %s", tradeAmount, lastBid);
  }

  @RequestMapping("/stats")
  public String stats() {
    return marketAnalyzer.getMarketConditions();
  }

  public static void main(String[] args) {
    SpringApplication.run(BinanceBotApplication.class);
  }
}
