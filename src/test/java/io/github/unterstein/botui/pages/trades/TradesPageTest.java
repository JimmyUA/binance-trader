package io.github.unterstein.botui.pages.trades;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.TestConfig;
import io.github.unterstein.botlogic.services.TradeService;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Collections;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class, MockitoTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={"datasource"})
public class TradesPageTest {

    @Autowired
    private WicketTester tester;

    @MockBean
    private TradeService tradeService;

    private TradesPage tradesPage;


    @Before
    public void setUp() throws Exception {
        tradesPage = new TradesPage(tradeService);
        tester.startPage(tradesPage);
    }

    @Test
    public void shouldRenderPage() {
        tester.assertRenderedPage(TradesPage.class);
    }


    @Test
    public void mainLabelHasCorrectText() throws Exception {
        tester.assertVisible("label");
        tester.assertLabel("label", "Trades statistic");
    }

    @Test
    public void noTradesLabelVisibleIfNoTrades() throws Exception {
        when(tradeService.getAllTrades()).thenReturn(Collections.EMPTY_LIST);

        tester.assertVisible("noTrades");
    }

    @DatabaseSetup("/sql/tests/one_trade.xml")
    @Test
    public void noTradesLabelInvisibleIfTradesFound() throws Exception {
       tester.startPage(TradesPage.class);

        tester.assertVisible("noTrades");
    }

}