package geometry.prediction;

import com.binance.api.client.domain.market.CandlestickInterval;
import geometry.lines.LineWithPastPeriods;
import geometry.lines.LinesCreator;
import geometry.prediction.exception.BadPredictionSituationException;
import io.github.unterstein.botlogic.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Predictor {

    protected static Logger logger = LoggerFactory.getLogger(Strategy.class);

    @Autowired
    private PointsChainCreator chainCreator;

    @Autowired
    private ArrowsCreator arrowsCreator;

    @Autowired
    private LinesCreator linesCreator;

    private PredictionHallInfo prediction;


    public Double predictPriceAfterPeriodsOnLine(LineWithPastPeriods line, int periodsAfter){

        return line.predictPriceAfter(periodsAfter);
    }

    public PredictionHallInfo createPrediction(long period, CandlestickInterval interval) throws BadPredictionSituationException {
        LineWithPastPeriods upLine = linesCreator.createHallUpLine(period, interval);
        LineWithPastPeriods bottomLine = linesCreator.createHallBottomLine(period, interval);
        PredictionHallInfo prediction;
        try {
            prediction = getPredictionHallInfo(upLine, bottomLine);
        } catch (BadPredictionSituationException e) {
            linesCreator.doubleClearRegionKof();
            upLine = linesCreator.createHallUpLine(period, interval);
            prediction = getPredictionHallInfo(upLine, bottomLine);
        }
        prediction.setCreationTime(System.currentTimeMillis());
        prediction.setInterval(interval);
        prediction.setCreationPeriod(period);
        return prediction;
    }

    protected PredictionHallInfo getPredictionHallInfo(LineWithPastPeriods upLine, LineWithPastPeriods bottomLine) throws BadPredictionSituationException {
        prediction= new PredictionHallInfo();
        prediction.setUpLine(upLine);
        prediction.setBottomLine(bottomLine);
        PointChain pointChain = chainCreator.createChain(upLine, bottomLine);
        prediction.setSituation(pointChain.getSituation());
        arrowsCreator.createAndInjectArrows(prediction, pointChain);

        return prediction;
    }

}
