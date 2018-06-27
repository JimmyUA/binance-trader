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




}