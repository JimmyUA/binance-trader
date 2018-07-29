package io.github.unterstein.statistic.RSI;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.TradingClient;
import io.github.unterstein.statistic.PricesAccumulator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({ MockitoTestExecutionListener.class})
public class RSITest {

    @MockBean
    private TradingClient client;
    private RSI rsi;

    @Before
    public void setUp() throws Exception {
        rsi = new RSI();


doReturn(DoubleStream.of(46.6875, 47.6875, 46.3125, 44.5625,
        47.0, 47.5625, 47.8125, 45.75, 44.625, 44.250, 44.9375, 46.9375,
        46.4375, 47.125, 46.125)
        .boxed().collect(Collectors.toList())).when(client).getPricesFromExchangeReversed(CandlestickInterval.ONE_MINUTE);
        rsi.setClient(client);
    }


    @Test
    public void shouldReturn_48_477() throws Exception {

        Double expected = 51.778656;
        assertEquals(expected, rsi.getRSI(14), 0.1);
    }

}