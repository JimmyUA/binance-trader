package io.github.unterstein.statistic.MACD;

import com.binance.api.client.domain.market.CandlestickInterval;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.Config;
import io.github.unterstein.TradingClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Arrays;
import java.util.LinkedList;

import static io.github.unterstein.remoteManagment.ManagementConstants.minutesFromStart;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class, MockitoTestExecutionListener.class})
@DbUnitConfiguration(databaseConnection = {"datasource"})
@DatabaseSetup("/sql/tests/one_trade.xml")
public class MACDTest {

    private LinkedList<Double> prices = new LinkedList<>(Arrays.asList(
            459.99, 448.85, 446.06, 450.81, 442.8,
            448.97, 444.57, 441.4, 430.47, 420.05, 431.14, 425.66, 430.58, 431.72, 437.87,
            428.43, 428.35, 432.5, 443.66, 455.72, 454.49, 452.08, 452.73, 461.91,
            463.58, 461.14, 452.08, 442.66, 428.91, 429.79, 431.99, 427.72, 423.2,
            426.21, 426.98, 435.69, 434.33, 429.8));

    @MockBean
    @Qualifier("short")
    private MACD mockMacd;

    @MockBean
    private TradingClient client;

    private MACD macd;

    private int shortPeriod = 12;
    private int longPeriod = 26;
    private int signalPeriod = 9;


    @Before
    public void setUp() throws Exception {
        macd = new MACD(shortPeriod, longPeriod, signalPeriod);
        macd.setClient(client);
        when(client.getPricesFromExchange(CandlestickInterval.ONE_MINUTE)).thenReturn(prices);
        macd.setClient(client);
        macd.initPrices();

    }

    @Test
    public void shouldCalculateFirstEMACorrectly() throws Exception {
        Double expectedFirstShortEMA = 440.8975;
        double firstShortEMA = macd.firstEMA(shortPeriod);

        assertEquals(expectedFirstShortEMA, firstShortEMA, 0.001);
    }


    @Test
    public void shouldCalculateShortEMACorrectly() throws Exception {
        Double expectedShortEMA = 433.8582;
        double shortEMA = macd.EMA(shortPeriod);

        assertEquals(expectedShortEMA, shortEMA, 0.001);
    }

    @Test
    public void shouldCalculateMACDCorrectly() throws Exception {
        Double expectedMACD = -2.40263;
        double MACD = macd.MACD();

        assertEquals(expectedMACD, MACD, 0.001);
    }

    @Test
    public void shouldCalculateFirstSignalCorrectly() throws Exception {
        Double expectedFirstSignal = 3.03752;
        macd.MACD();
        double firstSignal = macd.firstSignal();

        assertEquals(expectedFirstSignal, firstSignal, 0.001);
    }

    @Test
    public void shouldCalculateSignalCorrectly() throws Exception {
        Double expectedSignal = -0.15201;
        macd.MACD();
        double signal = macd.signal();

        assertEquals(expectedSignal, signal, 0.001);
    }

    @Test
    public void shouldCalculateHistogramCorrectly() throws Exception {
        Double expectedHistogram = -2.25061;
        double histogram = macd.histogramm();

        assertEquals(expectedHistogram, histogram, 0.001);
    }

    @Ignore
    @Test
    public void shouldBeAccending() {
        for (int i = 0; i < 38; i++) {
            minutesFromStart++;
            macd.histogramm();
        }
        assertTrue(macd.isAscending());
    }

    private Double getPrice() {
        return prices.pollFirst();
    }


    @Test
    public void shouldTurnCounterToZero() throws Exception {
        int expectedCounterValue = 1;

        when(mockMacd.MACD()).thenReturn(1.0);
        when(mockMacd.signal()).thenReturn(0.2);
        doCallRealMethod().when(mockMacd).checkMACDCrossedSignal();
        mockMacd.wasMACDCrossSignalUp = false;
        mockMacd.crossCounter = 150;

        mockMacd.checkMACDCrossedSignal();

        assertEquals(expectedCounterValue, mockMacd.crossCounter);
    }

    @Test
    public void shouldTurnCrossedToFalse() throws Exception {
        boolean expectedCrossedValue = false;

        when(mockMacd.MACD()).thenReturn(1.0);
        when(mockMacd.signal()).thenReturn(1.2);
        doCallRealMethod().when(mockMacd).checkMACDCrossedSignal();
        mockMacd.wasMACDCrossSignalUp = true;

        mockMacd.checkMACDCrossedSignal();

        assertFalse(mockMacd.wasMACDCrossSignalUp);
    }

}