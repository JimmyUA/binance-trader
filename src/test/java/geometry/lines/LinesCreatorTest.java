package geometry.lines;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.Config;
import io.github.unterstein.TradingClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({MockitoTestExecutionListener.class})
public class LinesCreatorTest {

    @MockBean
    private TradingClient client;

    private LinesCreator creator;

    private LinkedList<Double> prices = new LinkedList<>(Arrays.asList(
            459.99, 448.85, 446.06, 450.81, 442.8,
            600.00, 100.00, 441.4, 430.47, 420.05, 431.14, 425.66, 430.58, 431.72, 437.87,
            428.43, 428.35, 432.5, 443.66, 455.72, 454.49, 452.08, 452.73, 461.91,
            463.58, 500.00, 200.00, 442.66, 428.91, 429.79, 431.99, 427.72, 423.2,
            426.21, 426.98, 435.69, 434.33, 429.8));

    @Before
    public void setUp() throws Exception {
        creator = new LinesCreator();
        doReturn(prices).when(client).getPricesFromExchange(CandlestickInterval.FIVE_MINUTES);
        creator.setClient(client);
    }

    @Test
    public void shouldPredictCorrectByUpLine() throws Exception {
        Double expectedPrice = 400.0;

        LineWithPastPeriods line = creator.createHallUpLine(35L, CandlestickInterval.FIVE_MINUTES);
        assertEquals(expectedPrice, line.predictPriceAfter(8));
    }

    @Test
    public void shouldPredictCorrectByBottomLine() throws Exception {
        Double expectedPrice = 300.0;

        LineWithPastPeriods line = creator.createHallBottomLine(35L, CandlestickInterval.FIVE_MINUTES);
        assertEquals(expectedPrice, line.predictPriceAfter(9));
    }
}