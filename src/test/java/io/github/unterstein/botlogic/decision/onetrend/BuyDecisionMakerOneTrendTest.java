package io.github.unterstein.botlogic.decision.onetrend;

import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({MockitoTestExecutionListener.class})
public class BuyDecisionMakerOneTrendTest {


    @Test
    public void wasMACDCrossSignalUpShouldReturnTrue() throws Exception {
        
    }
}