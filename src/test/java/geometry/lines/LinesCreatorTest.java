package geometry.lines;

import com.binance.api.client.domain.market.CandlestickInterval;
import geometry.analysis.HallLinesAnalyzer;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({MockitoTestExecutionListener.class})
public class LinesCreatorTest {

    private LinesCreator creator;
    private HallLinesAnalyzer analyser;

    @MockBean
    private TradingClient client;

    private List<Double> pricesForUp = Arrays.asList(0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 200.0,
            0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 100.0, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 60.0,
            0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1);

    private List<Double> pricesForBottom = Arrays.asList(1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 200.0,
            1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0,
            100.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 60.0,
            1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0);


    @Before
    public void setUp() throws Exception {
        creator = new LinesCreator();
        analyser = new HallLinesAnalyzer();

    }

    @Test
    public void shouldCreateParallelLine() throws Exception {

        Point first = new Point(0.0, 0.0);
        Point second = new Point(45.0, 0.0);
        Line firstLine = new Line(first, second);

        Point third = new Point(45.0, 2.1);

        Line parallelLine = creator.createParallelLine(firstLine, third);

        assertTrue(analyser.isParallel(firstLine, parallelLine));
    }

    @Test
    public void shouldCreateParallelLineInThisSituation() throws Exception {

        Point first = new Point(0.0, 1.0);
        Point second = new Point(2.0, 0.0);
        Line firstLine = new Line(first, second);

        Point third = new Point(3.0, 1.0);

        Line parallelLine = creator.createParallelLine(firstLine, third);

        assertTrue(analyser.isParallel(firstLine, parallelLine));
    }


    @Test
    public void shouldCreateUpHallLineCorrect() throws Exception {
        Point expectedPoint = new Point(27.0, 60.0);

        when(client.getPricesFromExchange(CandlestickInterval.FIVE_MINUTES)).thenReturn(pricesForUp);

        creator.setClient(client);

        LineWithPastPeriods upLine = creator.createHallUpLine(38L, CandlestickInterval.FIVE_MINUTES);

        assertEquals(expectedPoint, upLine.getDefiningPoint());
    }

    @Test
    public void shouldCreateBottomHallLineCorrect() throws Exception {
        Point expectedPoint = new Point(29.0, 60.0);

        when(client.getPricesFromExchange(CandlestickInterval.FIVE_MINUTES)).thenReturn(pricesForBottom);

        creator.setClient(client);

        LineWithPastPeriods upLine = creator.createHallBottomLine(40L, CandlestickInterval.FIVE_MINUTES);

        assertEquals(expectedPoint, upLine.getDefiningPoint());
    }

}