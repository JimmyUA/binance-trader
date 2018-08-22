package io.github.unterstein.statistic.spread;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.*;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.persistent.entity.Spread;
import org.junit.Ignore;
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
@DatabaseTearDown(value = {"/sql/tests/emptyDB.xml"}, type = DatabaseOperation.TRUNCATE_TABLE)
public class SpreadTrackerTest {

    @Autowired
    private SpreadTracker tracker;

    @DatabaseSetup("/sql/tests/tenSpreadsOnePointZero.xml")
    @DatabaseTearDown(value = {"/sql/tests/emptyDB.xml"}, type = DatabaseOperation.TRUNCATE_TABLE)
    @Test
    public void shouldReturnCorrectAverageForTenMinutes() throws Exception {
        double expectedAverage = 1.0;

        assertEquals(expectedAverage, tracker.getAverageForPeriod(10), 0.0001);
    }

    @Ignore
    @DatabaseSetup("/sql/tests/emptyDB.xml")
    @ExpectedDatabase("/sql/tests/oneSpread.xml")
    @Test
    public void shouldAddSpreadToDB() throws Exception {
        Spread spread = new Spread();
        spread.setSpread(1.0);
        tracker.addSpread(spread);
    }
}