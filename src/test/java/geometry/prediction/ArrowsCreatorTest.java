package geometry.prediction;

import geometry.lines.Line;
import geometry.lines.LineWithPastPeriods;
import geometry.lines.Point;
import geometry.prediction.situation.PointsChainSituation;
import geometry.prediction.situation.SituationDetector;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({MockitoTestExecutionListener.class})
public class ArrowsCreatorTest {

    @MockBean
    private SituationDetector detector;

    @Test
    public void shouldCalculateAllArrowsCorrectOnPlatoSituation() throws Exception {
        ArrowsCreator creator = new ArrowsCreator();
        PointsChainCreator pointsChainCreator = new PointsChainCreator();

        doReturn(PointsChainSituation.PLATO).when(detector).detectSituation(anyInt(), anyInt(), anyInt(), anyInt());
        pointsChainCreator.setDetector(detector);

        Point bottomStart = new Point(0.0, 0.0);
        bottomStart.setIndex(2);
        Point bottomDefining = new Point(45.0, 0.0);
        bottomDefining.setIndex(3);
        Line up = new Line(bottomStart, bottomDefining);
        Point upStart = new Point(0.0, 1.0);
        upStart.setIndex(1);
        Point upDefining = new Point(45.0, 1.0);
        upDefining.setIndex(4);
        Line bottom = new Line(upStart, upDefining);
        LineWithPastPeriods upLine = new LineWithPastPeriods(bottom, 100);
        LineWithPastPeriods bottomLine = new LineWithPastPeriods(up, 100);

        PredictionHallInfo prediction = new PredictionHallInfo();

        prediction.setUpLine(upLine);
        prediction.setBottomLine(bottomLine);

        PointChain pointChain = pointsChainCreator.createChain(upLine, bottomLine);

        creator.setInfoForTest(prediction, pointChain);

        Line expectedFirstArrow = new Line(upStart, bottomDefining);
        assertEquals(expectedFirstArrow, creator.getFirstArrow());
    }
}