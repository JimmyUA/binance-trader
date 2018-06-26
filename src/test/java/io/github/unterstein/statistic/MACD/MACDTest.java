package io.github.unterstein.statistic.MACD;

import io.github.unterstein.statistic.PricesAccumulator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class MACDTest {

    @MockBean
    private PricesAccumulator pricesAccumulator;
    private MACD macd;

    @Before
    public void setUp() throws Exception {
        macd = new MACD(63, 26, 11);
        macd.setPricesAccumulator(pricesAccumulator);
        stubAccumulator();
    }

    @Test
    public void shouldCalculateShortEMACorrect() throws Exception {
        double expectedFirstShortEMA = 440.8975;
        double expectedSecondShortEMA = 439.310192;
        assertEquals(expectedFirstShortEMA, macd.shortEMA(), 0.01);
        assertEquals(expectedSecondShortEMA, macd.shortEMA(), 0.01);
    }

    @Test
    public void shouldCalculateLongEMACorrect() throws Exception {
        stubAccumulatorForLongTest();
        double expectedFirstLongEMA = 443.289615;
        double expectedSecondLongEMA = 443.940755;
        assertEquals(expectedFirstLongEMA, macd.longEMA(), 0.01);
        assertEquals(expectedSecondLongEMA, macd.longEMA(), 0.01);
    }

    @Test
    public void shouldCalculateMACDHundreedTImes() {
        stubAccumulatorForLongTest();
        for (int i = 0; i < 63; i++) {
            macd.calculateCurrentHistogram();
            pricesAccumulator.add(440.23 + i);
        }
        macd.signal();
        macd.histogramm();
    }

    private void stubAccumulator() {
        LinkedList<Double> prices = new LinkedList<>(Arrays.asList(10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                459.99, 448.85, 446.06, 450.81, 442.8,
                448.97, 444.57, 441.4, 430.47, 420.05, 431.14, 425.66));

        LinkedList<Double> updatedPrices = new LinkedList<>(Arrays.asList(10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                459.99, 448.85, 446.06, 450.81, 442.8,
                448.97, 444.57, 441.4, 430.47, 420.05, 431.14, 425.66, 430.58));
        when(pricesAccumulator.getSamples()).thenReturn(prices).thenReturn(updatedPrices);
    }

    private void stubAccumulatorForLongTest() {
        LinkedList<Double> prices = new LinkedList<>(Arrays.asList(
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                459.99, 448.85, 446.06, 450.81, 442.8,
                448.97, 444.57, 441.4, 430.47, 420.05, 431.14, 425.66, 430.58, 431.72, 437.87,
                428.43, 428.35, 432.5, 443.66, 455.72, 454.49, 452.08, 452.73, 461.91,
                463.58, 461.14));

        LinkedList<Double> updatedPrices = new LinkedList<>(Arrays.asList(
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
                459.99, 448.85, 446.06, 450.81, 442.8,
                448.97, 444.57, 441.4, 430.47, 420.05, 431.14, 425.66, 430.58, 431.72, 437.87,
                428.43, 428.35, 432.5, 443.66, 455.72, 454.49, 452.08, 452.73, 461.91,
                463.58, 461.14, 452.08));
        when(pricesAccumulator.getSamples()).thenReturn(prices).thenReturn(updatedPrices);
    }
}