package io.github.unterstein.statistic.MA;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.TradingClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({ MockitoTestExecutionListener.class})
public class MovingAverageTest {



    private MovingAverage movingAverage;


    @MockBean
    private TradingClient client;

    @Before
    public void setUp() throws Exception {
        movingAverage = new MovingAverage();
        movingAverage.setClient(client);

        Stream<Double> stream = DoubleStream.generate(() -> 0.000000001).limit(500).boxed();
        when(client.getPricesFromExchangeReversed(CandlestickInterval.ONE_MINUTE))
                .thenReturn(stream.collect(Collectors.toList()));
    }

    @Test
    public void shouldCalculateMA156() throws Exception {
        Double expectedMA = 0.000000001;
        double ma = movingAverage.MA(156, CandlestickInterval.ONE_MINUTE);
        assertEquals(expectedMA, ma, 0.000000000001);
    }
}