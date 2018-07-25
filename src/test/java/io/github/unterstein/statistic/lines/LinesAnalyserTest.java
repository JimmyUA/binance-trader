package io.github.unterstein.statistic.lines;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import io.github.unterstein.BinanceBotApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={"datasource"})
@DatabaseSetup("/sql/tests/fifty_prices.xml")
public class LinesAnalyserTest {

    @Autowired
    private LinesAnalyser linesAnalyser;

    @Test
    public void shouldFindMaxPrice() throws Exception {
        Double expectedMaxPrice = 0.000000002;
        assertEquals(expectedMaxPrice, linesAnalyser.getResistanceLineForPeriod(50L), 0.00000000001);
    }

    @Test
    public void shouldFindMinPrice() throws Exception {
        Double expectedMaxPrice = 0.0000000001;
        assertEquals(expectedMaxPrice, linesAnalyser.getSupportLineForPeriod(50L), 0.000000000001);
    }
}