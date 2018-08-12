package geometry.prediction;

import geometry.lines.Line;
import geometry.lines.LineWithPastPeriods;
import geometry.lines.Point;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application.properties")
public class PredictorTestWithContext {

    @Autowired
    private Predictor predictor;

    @Test
    public void checkPrediction() throws Exception {


        Point first = new Point(10.0, 100.0);
        Point second = new Point(50.0, 200.0);
        Line simpleLine = new Line(first, second);
        LineWithPastPeriods bottomLine = new LineWithPastPeriods(simpleLine, 11);


        Point firstUp = new Point(1.0, 600.0);
        Point secondUp = new Point(30.0, 500.0);
        Line simpleLineUp = new Line(firstUp, secondUp);
        LineWithPastPeriods upLine = new LineWithPastPeriods(simpleLineUp, 12);


       predictor.getPredictionHallInfo(upLine, bottomLine);
    }
}