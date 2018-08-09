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
        initLines(0, 1, 4, 5);
        creator.createChain(upLine, bottomLine);
    }

    @Test
    public void shouldBePlatoSituation() throws Exception {

        initLines(0, 7, 4, 5);
        PointChain chain = creator.createChain(upLine, bottomLine);

        assertEquals(PointsChainSituation.PLATO, chain.getSituation());
    }

    @Test
    public void shouldBeFrontBladeSituation() throws Exception {

        initLines(0, 7, 4, 8);
        PointChain chain = creator.createChain(upLine, bottomLine);

        assertEquals(PointsChainSituation.FRONT_BLADE, chain.getSituation());
    }

    @Test
    public void shouldBeBackBladeSituation() throws Exception {

        initLines(4, 9, 0, 8);
        PointChain chain = creator.createChain(upLine, bottomLine);

        assertEquals(PointsChainSituation.BACK_BLADE, chain.getSituation());
    }


    private void initLines(Integer upStartIndex, Integer upDefiningIndex,
                           Integer bottomStartIndex, Integer bottomDefiningIndex){
        Point firstStart = new Point(0.0, 0.1);
        firstStart.setIndex(upStartIndex);
        Point firstDefining = new Point(0.1, 0.2);
        firstDefining.setIndex(upDefiningIndex);
        Line firstLine = new Line(firstStart, firstDefining);
        upLine = new LineWithPastPeriods(firstLine, 4);

        Point secondStart = new Point(0.0, 0.1);
        secondStart.setIndex(bottomStartIndex);
        Point secondDefining = new Point(0.1, 0.2);
        secondDefining.setIndex(bottomDefiningIndex);
        Line secondLine = new Line(secondStart, secondDefining);
        bottomLine = new LineWithPastPeriods(secondLine, 4);
    }
}