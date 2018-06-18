package io.github.unterstein.statistic.RSI;

import io.github.unterstein.statistic.PricesAccumulator;
import org.junit.Before;
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

    @Test
    public void shouldReturn_48_477() throws Exception {
        stubAccumulator();

        Double expected = 48.52320675105485;
        assertEquals(expected, rsi.getRSI(14));
    }

    private void stubAccumulator() {
        LinkedList<Double> prices = new LinkedList<>(Arrays.asList(46.125, 47.125, 46.4375, 46.9375, 44.9375,
                44.250, 44.625, 45.75, 47.8125, 47.5625, 47.0, 44.5625, 46.3125, 47.6875, 46.6875));
        when(pricesAccumulator.getSamples()).thenReturn(prices);
    }
}