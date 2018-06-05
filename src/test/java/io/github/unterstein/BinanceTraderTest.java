package io.github.unterstein;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BinanceBotApplication.class)
public class BinanceTraderTest {

    @MockBean
    private TradingClient tradingClient;
    private BinanceTrader binanceTrader;
    private int tradeAmount;
    private double tradeDifference;
    private double tradeProfit;


    @Before
    public void setUp() throws Exception {
        tradeAmount = 10;
        tradeDifference = 0.000001;
        tradeProfit = 1.3;
        binanceTrader = new BinanceTrader(tradeDifference, tradeProfit, tradeAmount,
                "ETH", "XMR",  "key", "secret");

        binanceTrader.setClient(tradingClient);
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
}