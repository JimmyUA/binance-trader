package geometry.lines;

import com.binance.api.client.domain.market.CandlestickInterval;
import geometry.analysis.HallLinesAnalyzer;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.Config;
import io.github.unterstein.TradingClient;
import io.github.unterstein.statistic.lines.LinesAnalyser;
import org.junit.Before;
import org.junit.Ignore;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({MockitoTestExecutionListener.class})
public class LinesCreatorTest {

    private LinesCreator creator;
    private HallLinesAnalyzer analyser;


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
}