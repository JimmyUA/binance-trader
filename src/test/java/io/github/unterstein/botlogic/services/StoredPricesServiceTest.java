package io.github.unterstein.botlogic.services;

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

import java.util.LinkedList;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={"datasource"})
@DatabaseSetup("/sql/tests/fifty_prices.xml")
public class StoredPricesServiceTest {

    @Autowired
    private StoredPricesService storedPricesService;

    @Test
    public void shouldReturnLastPrice() throws Exception {
        LinkedList<Double> prices = storedPricesService.getPricesPortion(1L);
        assertTrue(prices.size() == 1);
    }
}