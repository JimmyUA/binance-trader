package geometry.prediction;

import geometry.analysis.HallLinesAnalyzer;
import geometry.lines.*;
import geometry.prediction.situation.PointsChainSituation;
import geometry.prediction.situation.SituationDetector;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.Config;
import org.junit.Before;
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

    private         ArrowsCreator creator;
private         PointsChainCreator pointsChainCreator;
private         HallLinesAnalyzer linesAnalyzer;


    @Before
    public void setUp() throws Exception {
        creator = new ArrowsCreator();
        creator = new ArrowsCreator();
        pointsChainCreator = new PointsChainCreator();
        linesAnalyzer = new HallLinesAnalyzer();
        LinesCreator linesCreator = new LinesCreator();
        creator.setLinesCreator(linesCreator);
        creator.setInterceptionFinder(new InterceptionFinder());
    }

    @Test
    public void shouldCalculateAllArrowsCorrectOnPlatoSituation() throws Exception {

        doReturn(PointsChainSituation.PLATO).when(detector).detectSituation(anyInt(), anyInt(), anyInt(), anyInt());
        pointsChainCreator.setDetector(detector);

        Point bottomStart = new Point(1.0, 0.0);
        bottomStart.setIndex(2);
        Point bottomDefining = new Point(2.0, 0.0);
        bottomDefining.setIndex(3);
        Line up = new Line(bottomStart, bottomDefining);
        Point upStart = new Point(0.0, 1.0);
        upStart.setIndex(1);
        Point upDefining = new Point(3.0, 1.0);
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

        creator.setFirstArrow(expectedFirstArrow);

        Line expectedSecondArrow = new Line(bottomDefining, upDefining);
        assertEquals(expectedSecondArrow, creator.getSecondArrow());
        creator.setSecondArrow(expectedSecondArrow);


        Line thirdArrow = creator.getThirdArrow();
        assertTrue(linesAnalyzer.isParallel(thirdArrow, expectedFirstArrow));
        creator.setThirdArrow(thirdArrow);


        Line forthArrow = creator.getForthArrow();
        assertTrue(linesAnalyzer.isParallel(forthArrow, expectedSecondArrow));

    }

    @Test
    public void shouldCalculateAllArrowsCorrectOnReversedPlatoSituation() throws Exception {

        doReturn(PointsChainSituation.REVERSED_PLATO).when(detector).detectSituation(anyInt(), anyInt(), anyInt(), anyInt());
        pointsChainCreator.setDetector(detector);

        Point bottomStart = new Point(0.0, 0.0);
        bottomStart.setIndex(0);
        Point bottomDefining = new Point(3.0, 0.0);
        bottomDefining.setIndex(3);
        Line up = new Line(bottomStart, bottomDefining);
        Point upStart = new Point(1.0, 1.0);
        upStart.setIndex(1);
        Point upDefining = new Point(2.0, 1.0);
        upDefining.setIndex(2);
        Line bottom = new Line(upStart, upDefining);
        LineWithPastPeriods upLine = new LineWithPastPeriods(bottom, 100);
        LineWithPastPeriods bottomLine = new LineWithPastPeriods(up, 100);

        PredictionHallInfo prediction = new PredictionHallInfo();

        prediction.setUpLine(upLine);
        prediction.setBottomLine(bottomLine);

        PointChain pointChain = pointsChainCreator.createChain(upLine, bottomLine);

        creator.setInfoForTest(prediction, pointChain);

        Line expectedFirstArrow = new Line(bottomStart, upDefining);
        assertEquals(expectedFirstArrow, creator.getFirstArrow());

        creator.setFirstArrow(expectedFirstArrow);

        Line expectedSecondArrow = new Line(upDefining, bottomDefining);
        assertEquals(expectedSecondArrow, creator.getSecondArrow());
        creator.setSecondArrow(expectedSecondArrow);


        Line thirdArrow = creator.getThirdArrow();
        assertTrue(linesAnalyzer.isParallel(thirdArrow, expectedFirstArrow));
        creator.setThirdArrow(thirdArrow);


        Line forthArrow = creator.getForthArrow();
        assertTrue(linesAnalyzer.isParallel(forthArrow, expectedSecondArrow));

    }

    @Test
    public void shouldCalculateAllArrowsCorrectOnFrontBladeSituation() throws Exception {

        doReturn(PointsChainSituation.FRONT_BLADE).when(detector)
                .detectSituation(anyInt(), anyInt(), anyInt(), anyInt());
        pointsChainCreator.setDetector(detector);

        Point bottomStart = new Point(1.0, 0.0);
        bottomStart.setIndex(2);
        Point bottomDefining = new Point(3.0, 0.0);
        bottomDefining.setIndex(4);
        Line up = new Line(bottomStart, bottomDefining);
        Point upStart = new Point(0.0, 1.0);
        upStart.setIndex(1);
        Point upDefining = new Point(2.0, 1.0);
        upDefining.setIndex(3);
        Line bottom = new Line(upStart, upDefining);
        LineWithPastPeriods upLine = new LineWithPastPeriods(bottom, 100);
        LineWithPastPeriods bottomLine = new LineWithPastPeriods(up, 100);

        PredictionHallInfo prediction = new PredictionHallInfo();

        prediction.setUpLine(upLine);
        prediction.setBottomLine(bottomLine);

        PointChain pointChain = pointsChainCreator.createChain(upLine, bottomLine);

        creator.setInfoForTest(prediction, pointChain);

        Line expectedFirstArrow = new Line(upStart, bottomStart);
        assertEquals(expectedFirstArrow, creator.getFirstArrow());

        creator.setFirstArrow(expectedFirstArrow);

        Line expectedSecondArrow = new Line(bottomStart, upDefining);
        assertEquals(expectedSecondArrow, creator.getSecondArrow());
        creator.setSecondArrow(expectedSecondArrow);

        Line expectedThirdArrow = new Line(upDefining, bottomDefining);
        Line thirdArrow = creator.getThirdArrow();
        assertEquals(expectedThirdArrow, creator.getThirdArrow());
        creator.setThirdArrow(thirdArrow);


        Line forthArrow = creator.getForthArrow();
        assertTrue(linesAnalyzer.isParallel(forthArrow, expectedSecondArrow));
        creator.setForthArrow(forthArrow);

        Line fifthArrow = creator.getFifthArrow();
        assertTrue(linesAnalyzer.isParallel(fifthArrow, thirdArrow));

    }


    @Test
    public void shouldCalculateAllArrowsCorrectOnBackBladeSituation() throws Exception {

        doReturn(PointsChainSituation.BACK_BLADE).when(detector)
                .detectSituation(anyInt(), anyInt(), anyInt(), anyInt());
        pointsChainCreator.setDetector(detector);

        Point bottomStart = new Point(0.0, 0.0);
        bottomStart.setIndex(1);
        Point bottomDefining = new Point(2.0, 0.0);
        bottomDefining.setIndex(3);
        Line up = new Line(bottomStart, bottomDefining);
        Point upStart = new Point(1.0, 1.0);
        upStart.setIndex(2);
        Point upDefining = new Point(3.0, 1.0);
        upDefining.setIndex(4);
        Line bottom = new Line(upStart, upDefining);
        LineWithPastPeriods upLine = new LineWithPastPeriods(bottom, 100);
        LineWithPastPeriods bottomLine = new LineWithPastPeriods(up, 100);

        PredictionHallInfo prediction = new PredictionHallInfo();

        prediction.setUpLine(upLine);
        prediction.setBottomLine(bottomLine);

        PointChain pointChain = pointsChainCreator.createChain(upLine, bottomLine);

        creator.setInfoForTest(prediction, pointChain);

        Line expectedFirstArrow = new Line(bottomStart, upStart);
        assertEquals(expectedFirstArrow, creator.getFirstArrow());

        creator.setFirstArrow(expectedFirstArrow);

        Line expectedSecondArrow = new Line(upStart, bottomDefining);
        assertEquals(expectedSecondArrow, creator.getSecondArrow());
        creator.setSecondArrow(expectedSecondArrow);

        Line expectedThirdArrow = new Line(bottomDefining, upDefining);
        Line thirdArrow = creator.getThirdArrow();
        assertEquals(expectedThirdArrow, creator.getThirdArrow());
        creator.setThirdArrow(thirdArrow);


        Line forthArrow = creator.getForthArrow();
        assertTrue(linesAnalyzer.isParallel(forthArrow, expectedSecondArrow));
        creator.setForthArrow(forthArrow);

        Line fifthArrow = creator.getFifthArrow();
        assertTrue(linesAnalyzer.isParallel(fifthArrow, thirdArrow));

    }
}