package io.github.unterstein;

import com.binance.api.client.domain.account.AssetBalance;
import io.github.unterstein.statistic.infoAccumulator.LastPriceVSOrderBook;
import io.github.unterstein.remoteManagment.ManagementConstants;
import io.github.unterstein.statistic.MarketAnalyzer;
import io.github.unterstein.statistic.PriceFetchingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
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

import static io.github.unterstein.remoteManagment.ManagementConstants.*;
import static io.github.unterstein.remoteManagment.ManagementConstants.shutDown;
import static io.github.unterstein.remoteManagment.ManagementConstants.sleepSomeTime;
import static io.github.unterstein.remoteManagment.ManagementConstants.stopTicker;

@EntityScan("io.github.unterstein.persistent.entity")
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

  @Value("${API_KEY:8L4iCDQJXAvxE9kbFaAVh01dlw2mBOUtPeyKseEXzdSeRvVG8YwF887w77qtoWV1}")
  private String apiKey;

  @Value("${API_SECRET:uunJlIIW21rEcu16DWIowNc5xQqTKhxS5VquBgcT08cvMNzzDvkFw3aOAicDjCVW}")
  private String apiSecret;

  @Value("${RSI_PERIODS}")
  private int rsiPeriods;

  @Value("${SPREAD_DIFFERENCE}")
  private double spreadDifference;

  @Value("${STRATEGY}")
  private String strategy;

  @Value("${ONE_TREND.STRATEGY.START_TREND}")
  private String dayTrend;

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
    initDayTrend();
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

  private void initDayTrend() {
    switch (dayTrend){
      case "UP":
        startDayTrend = false;
        break;
      case "DOWN":
        startDayTrend = true;
        break;
        default:startDayTrend = true;
    }
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


  @RequestMapping("/balance")
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
