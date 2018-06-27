package io.github.unterstein.statistic.MACD;

import io.github.unterstein.TestConfig;
import io.github.unterstein.statistic.PricesAccumulator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.LinkedList;

import static io.github.unterstein.remoteManagment.ManagementConstants.minutesFromStart;
import static org.junit.Assert.assertEquals;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)

public class MACDTest {

    private LinkedList<Double> prices = new LinkedList<>(Arrays.asList(
            459.99, 448.85, 446.06, 450.81, 442.8,
            448.97, 444.57, 441.4, 430.47, 420.05, 431.14, 425.66, 430.58, 431.72, 437.87,
            428.43, 428.35, 432.5, 443.66, 455.72, 454.49, 452.08, 452.73, 461.91,
            463.58, 461.14, 452.08, 442.66, 428.91, 429.79, 431.99, 427.72, 423.2,
            426.21, 426.98));

    @Autowired
    @Qualifier("testAccumulator")
    private PricesAccumulator pricesAccumulator;
    private MACD macd;

    @Before
    public void setUp() throws Exception {
        macd = new MACD(12, 26, 9);
        macd.setPricesAccumulator(pricesAccumulator);

    }

    @Test
    public void shouldCalculateCorrect() throws Exception {
        Double expectedMACD = -2.07056;
        Double expectedSignal = 3.037526;
        Double expectedHistogram = -5.108084;
        for (int i = 0; i < 34; i++) {
            minutesFromStart++;
            pricesAccumulator.add(getPrice());
            macd.calculateCurrentHistogram();
        }
        assertEquals(expectedMACD, macd.getLastMACD(), 0.001);
        assertEquals(expectedSignal, macd.getLastSignal(), 0.001);
        assertEquals(expectedHistogram, macd.getLastHistogram(), 0.001);
    }

    private Double getPrice() {
        return prices.pollFirst();
    }


}