package io.github.unterstein.statistic.lines;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.TradingClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({MockitoTestExecutionListener.class})
public class LinesAnalyserTest {


    private LinesAnalyser linesAnalyser;

    @MockBean
    private TradingClient client;

    @Before
    public void setUp() throws Exception {
        linesAnalyser = new LinesAnalyser();
        linesAnalyser.setClient(client);

        LinkedList<Double> list = DoubleStream.generate(() -> 0.0).limit(30).boxed()
                .collect(Collectors.toCollection(LinkedList::new));
        list.addLast(0.000000001);
        list.addLast(0.000000002);
        list.addLast(0.0000000015);

        doReturn(list).when(client).getPricesFromExchangeReversed(CandlestickInterval.ONE_MINUTE);
    }

    @Test
    public void shouldFindMaxPrice() throws Exception {
        Double expectedMaxPrice = 0.000000002;
        assertEquals(expectedMaxPrice, linesAnalyser.getResistanceLineForPeriod(50L, CandlestickInterval.ONE_MINUTE), 0.00000000001);
    }

    @Test
    public void shouldFindMinPrice() throws Exception {
        Double expectedMinPrice = 0.000000001;
        assertEquals(expectedMinPrice, linesAnalyser.getSupportLineForPeriod(50L, CandlestickInterval.ONE_MINUTE), 0.000000000001);
    }
}