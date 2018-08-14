package geometry.prediction;

import geometry.lines.Line;
import geometry.lines.LineWithPastPeriods;
import geometry.lines.Point;
import geometry.prediction.exception.BadPredictionSituationException;
import geometry.prediction.situation.PointsChainSituation;
import geometry.prediction.situation.SituationDetector;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PointsChainCreatorTest{


    private LineWithPastPeriods upLine;
    private LineWithPastPeriods bottomLine;
    private PointsChainCreator creator;

    @Before
    public void setUp() throws Exception {
        creator = new PointsChainCreator();
        creator.setDetector(new SituationDetector());

    }

    @Test(expected = BadPredictionSituationException.class)
    public void throwsExceptionIfSituationIsNotValid() throws Exception {
        Point firstStart = new Point(0.0, 0.1);
        Point firstDefining = new Point(0.1, 0.2);

        Point secondStart = new Point(0.0, 0.1);
        Point secondDefining = new Point(0.1, 0.2);

        initLines(firstStart, firstDefining, secondStart, secondDefining);

        creator.createChain(upLine, bottomLine);
    }

    @Test(expected = BadPredictionSituationException.class)
    public void throwsExceptionIfTwoBottomIndexesLowerThanTwoUp() throws Exception {
        Point firstStart = new Point(87.0, 0.1);
        Point firstDefining = new Point(88.1, 0.2);

        Point secondStart = new Point(13.0, 0.1);
        Point secondDefining = new Point(23.0, 0.2);

        initLines(firstStart, firstDefining, secondStart, secondDefining);

        creator.createChain(upLine, bottomLine);
    }

    @Test(expected = BadPredictionSituationException.class)
    public void throwsExceptionIfTwoUpIndexesLowerThanTwoBottom() throws Exception {
        Point firstStart = new Point(1.0, 0.1);
        Point firstDefining = new Point(13.1, 0.2);

        Point secondStart = new Point(83.0, 0.1);
        Point secondDefining = new Point(87.0, 0.2);

        initLines(firstStart, firstDefining, secondStart, secondDefining);

        creator.createChain(upLine, bottomLine);
    }

    @Test
    public void shouldBeReversedPlatoSituation() throws Exception {

        Point firstStart = new Point(1.0 , 0.1);
        Point firstDefining = new Point(2.0, 0.1);

        Point secondStart = new Point(0.0, 0.1);
        Point secondDefining = new Point(3.0, 0.1);

        initLines(firstStart, firstDefining, secondStart, secondDefining);
        PointChain chain = creator.createChain(upLine, bottomLine);

        assertEquals(PointsChainSituation.REVERSED_PLATO, chain.getSituation());
    }

    @Test
    public void shouldBePlatoSituation() throws Exception {

        Point firstStart = new Point(0.0, 0.1);
        Point firstDefining = new Point(4.0, 0.1);

        Point secondStart = new Point(1.0, 0.1);
        Point secondDefining = new Point(2.0, 0.1);

        initLines(firstStart, firstDefining, secondStart, secondDefining);
        PointChain chain = creator.createChain(upLine, bottomLine);

        assertEquals(PointsChainSituation.PLATO, chain.getSituation());
    }

    @Test
    public void shouldBeFrontBladeSituation() throws Exception {

        Point firstStart = new Point(0.0, 0.1);
        Point firstDefining = new Point(3.0, 0.1);

        Point secondStart = new Point(1.0, 0.1);
        Point secondDefining = new Point(4.0, 0.1);

        initLines(firstStart, firstDefining, secondStart, secondDefining);
        PointChain chain = creator.createChain(upLine, bottomLine);

        assertEquals(PointsChainSituation.FRONT_BLADE, chain.getSituation());
    }

    @Test
    public void shouldBeBackBladeSituation() throws Exception {

        Point firstStart = new Point(1.0, 0.1);
        Point firstDefining = new Point(3.0, 0.1);

        Point secondStart = new Point(0.0, 0.1);
        Point secondDefining = new Point(2.0, 0.1);

        initLines(firstStart, firstDefining, secondStart, secondDefining);
        PointChain chain = creator.createChain(upLine, bottomLine);

        assertEquals(PointsChainSituation.BACK_BLADE, chain.getSituation());
    }




    private void initLines(Point firstStart, Point firstDefining,
                           Point secondStart, Point secondDefining){

        Line firstLine = new Line(firstStart, firstDefining);
        upLine = new LineWithPastPeriods(firstLine, 4);


        Line secondLine = new Line(secondStart, secondDefining);
        bottomLine = new LineWithPastPeriods(secondLine, 4);
    }
}