package geometry.prediction;

import com.binance.api.client.domain.market.CandlestickInterval;
import geometry.lines.*;
import geometry.prediction.situation.PointsChainSituation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PredictionHallInfoTest {




    @Test
    public void shouldPredictFinalPointCorrect () {
        LinesCreator linesCreator = new LinesCreator();
        InterceptionFinder interceptionFinder = new InterceptionFinder();
        PredictionHallInfo prediction = new PredictionHallInfo();
        prediction.setSituation(PointsChainSituation.REVERSED_PLATO);
        prediction.setInterceptionFinder(interceptionFinder);
        prediction.setCreationPeriod(10);
        prediction.setInterval(CandlestickInterval.FIVE_MINUTES);
        prediction.setCreationTime(System.currentTimeMillis());

        Point upStart = new Point(3.0, 20.0);
        Point upDefining = new Point(6.0, 25.0);
        Line upLine = new Line(upStart, upDefining);
        prediction.setUpLine(new LineWithPastPeriods(upLine, 7));

        Point bottomStart = new Point(2.0, 10.0);
        Point bottomDefining = new Point(8.0, 15.0);
        Line bottomLine = new Line(bottomStart, bottomDefining);
        prediction.setBottomLine(new LineWithPastPeriods(bottomLine, 7));

        Line firstArrow = new Line(bottomStart, upDefining);
        prediction.setFirstArrow(firstArrow);
        Line secondArrow = new Line(upDefining, bottomDefining);
        prediction.setSecondArow(secondArrow);

        Line thirdArrow = linesCreator.createParallelLine(firstArrow, bottomDefining);
        prediction.setThirdArrow(thirdArrow);

        Line forthArrow = linesCreator.createParallelLine(secondArrow,
                interceptionFinder.findInterception(thirdArrow, upLine));
        prediction.setForthArrow(forthArrow);

        Double expectedPrice = 23.0;

        assertEquals(expectedPrice, prediction.getFinalPrediction().getPrice(), 0.01);
        System.out.println(prediction.getMaxPricePrediction());
        System.out.println(prediction.getMinPricePrediction());
    }
}