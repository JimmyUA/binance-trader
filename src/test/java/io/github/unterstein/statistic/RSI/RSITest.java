package io.github.unterstein.statistic.RSI;

import io.github.unterstein.statistic.PricesAccumulator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
//@SpringBootTest(classes = BinanceBotApplication.class)
public class RSITest {

    @MockBean
    private PricesAccumulator pricesAccumulator;
    private RSI rsi;

    @Before
    public void setUp() throws Exception {
        rsi = new RSI();
        rsi.setPricesAccumulator(pricesAccumulator);
    }

    @Ignore
    @Test
    public void shouldReturn_48_477() throws Exception {
        stubAccumulator();

        Double expected = 51.778656;
        assertEquals(expected, rsi.getRSI(14), 0.1);
    }

    @Ignore
    @Test
    public void shouldReturn_69_46() throws Exception {
        stubAccumulator2();

        Double expected = 69.46;
        assertEquals(expected, rsi.getRSI(14), 0.1);
    }

    private void stubAccumulator() {
        LinkedList<Double> prices = new LinkedList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                46.125, 47.125, 46.4375, 46.9375, 44.9375,
                44.250, 44.625, 45.75, 47.8125, 47.5625, 47.0, 44.5625, 46.3125, 47.6875, 46.6875));
        when(pricesAccumulator.getSamples()).thenReturn(prices);
    }

    private void stubAccumulator2() {
        LinkedList<Double> prices = new LinkedList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                45.15, 46.26, 46.5, 46.23, 46.08,
                46.03, 46.83, 47.69, 47.54, 49.25, 49.23, 48.2, 47.57, 47.61, 48.08));
        when(pricesAccumulator.getSamples()).thenReturn(prices);
    }
}