package io.github.unterstein.statistic.spread;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.botlogic.services.SpreadService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@DbUnitConfiguration(databaseConnection = {"datasource"})
public class SpreadServiceTest {

    @Autowired
    private SpreadService spreadService;

    @DatabaseSetup("/sql/tests/tenSpreadsOnePointZero.xml")
    @Test
    public void shouldReturnListOfThreeOnes() throws Exception {
        List<Double> expectedSpreads = Arrays.asList(1.0, 1.0, 1.0);


        assertEquals(expectedSpreads, spreadService.getLastSpreads(3));
    }
}