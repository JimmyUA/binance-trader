package io.github.unterstein;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import io.github.unterstein.statistic.TrendAnalyzer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BinanceBotApplication.class)
public class BinanceTraderTest {

    @MockBean
    private TradingClient tradingClient;
    @MockBean
    private TrendAnalyzer trendAnalyzer;
    private BinanceTrader binanceTrader;
    private int tradeAmount;
    private double tradeDifference;
    private double tradeProfit;

    private OrderBook orderBook;
    private OrderBookEntry ask;
    private OrderBookEntry bid;
    private AssetBalance etherBalance;
    private Order order;
    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);



    @Before
    public void setUp() throws Exception {
        tradeAmount = 10;
        tradeDifference = 0.00000001;
        tradeProfit = 1.3;
        binanceTrader = new BinanceTrader(tradingClient, trendAnalyzer);

        binanceTrader.setClient(tradingClient);

        orderBook = new OrderBook();
        ask = new OrderBookEntry();
        bid = new OrderBookEntry();

        etherBalance = new AssetBalance();
        etherBalance.setAsset("ETH");
        etherBalance.setFree("0.3");
        etherBalance.setLocked("0.0");

        order = new Order();
    }


    @Test
    public void newOrderNoBurst() throws Exception {
        OrderBook orderBook = new OrderBook();
        OrderBookEntry ask = new OrderBookEntry();
        ask.setPrice("0.4");
        OrderBookEntry bid = new OrderBookEntry();
        bid.setPrice("0.036");
        orderBook.setAsks(Arrays.asList(ask));
        orderBook.setBids(Arrays.asList(bid));
        when(tradingClient.getOrderBook()).thenReturn(orderBook);
        when(tradingClient.lastPrice()).thenReturn(0.3);
        AssetBalance assetBalance = new AssetBalance();
        assetBalance.setAsset("2.0");
        assetBalance.setFree("2.0");
        assetBalance.setLocked("0.0");
        when(tradingClient.getTradingBalance()).thenReturn(assetBalance);
        when(tradingClient.getAllTradingBalance()).thenReturn(2.0);
        double buyPrice = Double.valueOf(bid.getPrice()) + tradeDifference;
        NewOrderResponse orderResponse = new NewOrderResponse();
        orderResponse.setOrderId(1L);
        when(tradingClient.buy(tradeAmount, buyPrice)).thenReturn(orderResponse);
        when(tradingClient.tradingBalanceAvailable(anyObject())).thenReturn(true);
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);
        when(tradingClient.getOrder(anyLong())).thenReturn(order);

        binanceTrader.tick();
    }

    @Test
    public void DATASellImitation09_06_19_20() throws Exception {
        tradeAmount = 120;
        tradeProfit = 1.0;
        binanceTrader = new BinanceTrader(tradingClient, trendAnalyzer);
        binanceTrader.setClient(tradingClient);


        prepareFirstTick();
        binanceTrader.tick();
        prepareSecondTick();
        binanceTrader.tick();
        prepareThirdTick();
        binanceTrader.tick();
        prepareFoursTick();
        binanceTrader.tick();
        prepareFifthTick();
        binanceTrader.tick();

    }

    private void prepareFifthTick() {
        setAskAndBid("0.00015557", "0.00015551");
        when(tradingClient.lastPrice()).thenReturn(0.00015557);
    }

    private void prepareFoursTick() {
        setAskAndBid("0.00015854", "0.00015613");
        when(tradingClient.lastPrice()).thenReturn(0.00015854);
        AssetBalance tradingBalance = getZeroTradingBalance("DATA");
        tradingBalance.setFree("60");
        tradingBalance.setLocked("60");
        when(tradingClient.getTradingBalance()).thenReturn(tradingBalance);
        when(tradingClient.getAllTradingBalance()).thenReturn(Double.valueOf(tradingBalance.getFree()) + Double.valueOf(tradingBalance.getLocked()));
        order.setStatus(OrderStatus.PARTIALLY_FILLED);
    }

    private void prepareThirdTick() {
        setAskAndBid("0.00015854", "0.00015614");
        when(tradingClient.getOrderBook()).thenReturn(orderBook);
        when(tradingClient.lastPrice()).thenReturn(0.00015854);
    }

    private void prepareSecondTick() {
        setAskAndBid("0.00015854", "0.00015614");
        when(tradingClient.getOrderBook()).thenReturn(orderBook);
        when(tradingClient.lastPrice()).thenReturn(0.00015854);
        AssetBalance tradingBalance = getZeroTradingBalance("DATA");
        when(tradingClient.getTradingBalance()).thenReturn(tradingBalance);
        when(tradingClient.getAllTradingBalance()).thenReturn(Double.valueOf(tradingBalance.getFree()) + Double.valueOf(tradingBalance.getLocked()));
        order.setOrderId(1L);
        order.setStatus(OrderStatus.NEW);
        when(tradingClient.getOrder(1L)).thenReturn(order);
    }

    private void prepareFirstTick() {
        binanceTrader.setTrackingLastPrice(0.00015500);
        setAskAndBid("0.00015805", "0.00015613");
        when(tradingClient.getOrderBook()).thenReturn(orderBook);
        when(tradingClient.lastPrice()).thenReturn(0.00015854);
        AssetBalance tradingBalance = getZeroTradingBalance("DATA");
        when(tradingClient.getTradingBalance()).thenReturn(tradingBalance);
        when(tradingClient.getAllTradingBalance()).thenReturn(Double.valueOf(tradingBalance.getFree()) + Double.valueOf(tradingBalance.getLocked()));
        NewOrderResponse orderResponse = new NewOrderResponse();
        orderResponse.setOrderId(1L);
        when(tradingClient.buy(anyInt(), anyDouble())).thenReturn(orderResponse).thenCallRealMethod();


    }

    private AssetBalance getZeroTradingBalance(String assetName) {
        AssetBalance tradingBalance = new AssetBalance();
        tradingBalance.setAsset(assetName);
        tradingBalance.setFree("0.0");
        tradingBalance.setLocked("0.0");
        return tradingBalance;
    }

    private void setAskAndBid(String askValue, String bidValue){
        ask.setPrice(askValue);
        bid.setPrice(bidValue);
        orderBook.setAsks(Arrays.asList(ask));
        orderBook.setBids(Arrays.asList(bid));
    }

}