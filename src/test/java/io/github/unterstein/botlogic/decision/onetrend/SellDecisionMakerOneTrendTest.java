package io.github.unterstein.botlogic.decision.onetrend;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.Config;
import io.github.unterstein.statistic.MarketAnalyzer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({MockitoTestExecutionListener.class})
@DatabaseSetup("/sql/tests/one_trade.xml")
public class SellDecisionMakerOneTrendTest {


    private SellDecisionMakerOneTrend sellDecisionMakerOneTrend;

    @MockBean
    private MarketAnalyzer marketAnalyzer;

    @Before
    public void setUp() throws Exception {
        sellDecisionMakerOneTrend = new SellDecisionMakerOneTrend();
        sellDecisionMakerOneTrend.setMarketAnalyzer(marketAnalyzer);
    }

    @Test
    public void needToSellByMACDShouldWorkOnlyAfterItWasAboveZero() throws Exception {
        sellDecisionMakerOneTrend.wasMACDOverZero = false;

        doReturn(true).when(marketAnalyzer).isMaCDBelowZero();
        assertFalse(sellDecisionMakerOneTrend.isNeedToSellByMACD());

        doReturn(false).when(marketAnalyzer).isMaCDBelowZero();
        assertFalse(sellDecisionMakerOneTrend.isNeedToSellByMACD());

        doReturn(true).when(marketAnalyzer).isMaCDBelowZero();
        assertTrue(sellDecisionMakerOneTrend.isNeedToSellByMACD());

    }
}