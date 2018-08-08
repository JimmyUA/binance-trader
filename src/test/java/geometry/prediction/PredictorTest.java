package geometry.prediction;

import geometry.lines.Line;
import geometry.lines.LineWithPastPeriods;
import geometry.lines.Point;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PredictorTest {

    private Predictor predictor;


    @Before
    public void setUp() throws Exception {
        predictor = new Predictor();
    }

    @Test
    public void shouldPredictCorrectByUpLine() throws Exception {
        Double expectedPrice = 400.0;

        Point first = new Point(0.0, 600.0);
        Point second = new Point(20.0, 500.0);
        Line simpleLine = new Line(first, second);
        LineWithPastPeriods line = new LineWithPastPeriods(simpleLine, 12);

        assertEquals(expectedPrice, predictor.predictPriceAfterPeriodsOnLine(line,8));
    }

    @Test
    public void shouldPredictCorrectByBottomLine() throws Exception {
        Double expectedPrice = 300.0;

        Point first = new Point(0.0, 100.0);
        Point second = new Point(20.0, 200.0);
        Line simpleLine = new Line(first, second);
        LineWithPastPeriods line = new LineWithPastPeriods(simpleLine, 11);


        assertEquals(expectedPrice, predictor.predictPriceAfterPeriodsOnLine(line, 9));
    }
}