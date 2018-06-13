package io.github.unterstein;

import com.binance.api.client.domain.account.AssetBalance;
import io.github.unterstein.infoAccumulator.LastPriceVSOrderBook;
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
import java.util.stream.Collectors;

import static io.github.unterstein.remoteManagment.ManagementConstants.shutDown;

@EnableScheduling
@SpringBootApplication
@RestController("/")
public class BinanceBotApplication {

  private static Logger logger = LoggerFactory.getLogger(BinanceBotApplication.class);

  private BinanceTrader trader;

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

  static LastPriceVSOrderBook lastPriceVSOrderBook;

  @Autowired
  private TradingClient tradingClient;

  @PostConstruct
  public void init() {
    logger.info(String.format("Starting app with diff=%.8f, profit=%.8f amount=%d base=%s trade=%s", tradeDifference, tradeProfit, tradeAmount, baseCurrency, tradeCurrency));
    trader = new BinanceTrader(tradeDifference, tradeProfit, tradeAmount, baseCurrency, tradeCurrency, apiKey, apiSecret);
    lastPriceVSOrderBook = new LastPriceVSOrderBook(tradingClient);
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

  public static void main(String[] args) {
    SpringApplication.run(BinanceBotApplication.class);
  }
}
