package io.github.unterstein;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.account.request.OrderStatusRequest;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.OrderBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TradingClient {
  private static Logger logger = LoggerFactory.getLogger(TradingClient.class);

  private BinanceApiRestClient client;
  private String baseCurrency;
  private String tradeCurrency;
  private String symbol;

  public TradingClient() {
  }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getTradeCurrency() {
        return tradeCurrency;
    }

    public TradingClient(String baseCurrency, String tradeCurrency, String key, String secret) {
    this.baseCurrency = baseCurrency;
    this.tradeCurrency = tradeCurrency;
    BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(key, secret);
    client = factory.newRestClient();
    symbol = tradeCurrency + baseCurrency;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  // The bid price represents the maximum price that a buyer is willing to pay for a security.
  // The ask price represents the minimum price that a seller is willing to receive.
  public OrderBook getOrderBook() {
    return client.getOrderBook(symbol, 10);
  }

  public AssetBalance getBaseBalance() {
    return client.getAccount().getAssetBalance(baseCurrency);
  }

  public AssetBalance getTradingBalance() {
    return client.getAccount().getAssetBalance(tradeCurrency);
  }

  public double assetBalanceToDouble(AssetBalance balance) {
    return Double.valueOf(balance.getFree()) + Double.valueOf(balance.getLocked());
  }

  public double assetFreeBalanceToDouble(AssetBalance balance) {
    return Double.valueOf(balance.getFree());
  }

  public double getAllTradingBalance() {
    AssetBalance tradingBalance = getTradingBalance();
    return assetBalanceToDouble(tradingBalance);
  }

  public double getFreeTradingBalance() {
    AssetBalance tradingBalance = getTradingBalance();
    return assetBalanceToDouble(tradingBalance);
  }

  public boolean tradingBalanceAvailable(AssetBalance tradingBalance) {
    return assetBalanceToDouble(tradingBalance) > 1;
  }

  public List<AssetBalance> getBalances() {
    return client.getAccount().getBalances();
  }

  public List<Order> getOpenOrders() {
    OrderRequest request = new OrderRequest(symbol);
    return client.getOpenOrders(request);
  }

  public void cancelAllOrders() {
    getOpenOrders().forEach(order -> client.cancelOrder(new CancelOrderRequest(symbol, order.getOrderId())));
  }

  // * GTC (Good-Til-Canceled) orders are effective until they are executed or canceled.
  // * IOC (Immediate or Cancel) orders fills all or part of an order immediately and cancels the remaining part of the order.
  public NewOrderResponse buy(int quantity, double price) {
    String priceString = String.format("%.8f", price).replace(",", ".");
    logger.info(String.format("Buying %d for %s\n", quantity, priceString));
    NewOrder order = new NewOrder(symbol, OrderSide.BUY, OrderType.LIMIT, TimeInForce.GTC, "" + quantity, priceString);
    return client.newOrder(order);
  }

  public NewOrderResponse buyMarket(int quantity) {
    if (quantity > 0) {
      logger.info("Buying from MARKET with quantity " + quantity);
      NewOrder order = new NewOrder(symbol, OrderSide.BUY, OrderType.MARKET, null, "" + quantity);
      return client.newOrder(order);
    } else {
      logger.info("not executing - 0 quantity bought");
      return new NewOrderResponse();
    }
  }

  public void setLimitOrder(int quantity, double price) {
    String priceString = String.format("%.8f", price).replace(",", ".");
    logger.info(String.format("Selling %d for %s\n", quantity, priceString));
    NewOrder order = new NewOrder(symbol, OrderSide.SELL, OrderType.LIMIT, TimeInForce.GTC, "" + quantity, priceString);
    client.newOrder(order);
  }

  public void sellMarket(int quantity) {
    if (quantity > 0) {
      logger.info("Selling to MARKET with quantity " + quantity);
      NewOrder order = new NewOrder(symbol, OrderSide.SELL, OrderType.MARKET, null, "" + quantity);
      client.newOrder(order);
    } else {
      logger.info("not executing - 0 quantity setLimitOrder");
    }
  }

  public Order getOrder(long orderId) {
    return client.getOrderStatus(new OrderStatusRequest(symbol, orderId));
  }

  public double lastPrice() {
    return Double.valueOf(client.get24HrPriceStatistics(symbol).getLastPrice());
  }

  public void cancelOrder(long orderId) {
    logger.info("Cancelling order " + orderId);
    client.cancelOrder(new CancelOrderRequest(symbol, orderId));
  }

  public void panicSell(double lastKnownAmount, double lastKnownPrice) {
    logger.error("!!!! PANIC SELL !!!!");
    logger.warn(String.format("Probably selling %.8f for %.8f", lastKnownAmount, lastKnownPrice));
    cancelAllOrders();
    sellMarket(Double.valueOf(getTradingBalance().getFree()).intValue());
  }

  public OrderBook getLatestOrderBook() {
    return getOrderBook();
  }

  public Double getLastAsk(Integer coinsAmount) {
    OrderBook orderBook = getLatestOrderBook();
    return Double.valueOf(orderBook.getAsks().stream()
            .filter(ask -> Double.parseDouble(ask.getQty()) > coinsAmount)
            .findFirst().get().getPrice());
  }

  public Double getLastBid(Integer coinsAmount) {
    OrderBook orderBook = getLatestOrderBook();
    return Double.valueOf(orderBook.getBids().stream()
            .filter(bid -> Double.parseDouble(bid.getQty()) > coinsAmount)
            .findFirst().get().getPrice());
  }

  public Double getLastBidsAverage(Integer coinsAmount, Integer bidsAmount){
    OrderBook orderBook = getLatestOrderBook();
    return orderBook.getBids().stream()
            .filter(bid -> Double.parseDouble(bid.getQty()) > coinsAmount)
            .limit(bidsAmount)
            .mapToDouble(bid -> Double.parseDouble(bid.getPrice()))
            .average().getAsDouble();
  }

  public Double getLastAsksAverage(Integer coinsAmount, Integer asksAmount){
    OrderBook orderBook = getLatestOrderBook();
    return orderBook.getAsks().stream()
            .filter(ask -> Double.parseDouble(ask.getQty()) > coinsAmount)
            .limit(asksAmount)
            .mapToDouble(ask -> Double.parseDouble(ask.getPrice()))
            .average().getAsDouble();
  }

  public Double getHighestPrice() {
    String highestPrice = client.get24HrPriceStatistics(symbol).getHighPrice();
    return Double.parseDouble(highestPrice);
  }

  public List<Candlestick> getCandleStickBars(CandlestickInterval interval){
    return client.getCandlestickBars(symbol, interval);
  }

  public List<Double> getPricesFromExchangeReversed(CandlestickInterval interval){
    Stream<Double> originalStream = getPricesStream(interval);
    return reverse(originalStream).collect(Collectors.toList());
  }

  public List<Double> getPricesFromExchange(CandlestickInterval interval){
    return getPricesStream(interval).collect(Collectors.toList());
  }

  private Stream<Double> getPricesStream(CandlestickInterval interval) {
    List<Candlestick> candleStickBars = getCandleStickBars(interval);
    return candleStickBars.stream()
            .map(Candlestick::getClose).mapToDouble(Double::valueOf)
            .boxed();
  }


  public static <T> Stream<T> reverse(Stream<T> stream) {
    return stream
            .collect(Collector.of(
                    () -> new ArrayDeque<T>(),
                    ArrayDeque::addFirst,
                    (q1, q2) -> { q2.addAll(q1); return q2; })
            )
            .stream();
  }

}
