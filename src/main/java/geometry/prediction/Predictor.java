package geometry.prediction;

import com.binance.api.client.domain.market.CandlestickInterval;
import geometry.lines.LineWithPastPeriods;
import geometry.lines.LinesCreator;
import geometry.prediction.exception.BadPredictionSituationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Predictor {

    @Autowired
    private PointsChainCreator chainCreator;

    @Autowired
    private ArrowsCreator arrowsCreator;

    @Autowired
    private LinesCreator linesCreator;

    @Autowired
    private PredictionHallInfo prediction;

    public Double predictPriceAfterPeriodsOnLine(LineWithPastPeriods line, int periodsAfter){

        return line.predictPriceAfter(periodsAfter);
    }

    public PredictionHallInfo createPrediction(long period, CandlestickInterval interval) throws BadPredictionSituationException {
        LineWithPastPeriods upLine = linesCreator.createHallUpLine(period, interval);
        LineWithPastPeriods bottomLine = linesCreator.createHallBottomLine(period, interval);
        PredictionHallInfo prediction = getPredictionHallInfo(upLine, bottomLine);
        prediction.setCreationTime(System.currentTimeMillis());
        prediction.setInterval(interval);
        prediction.setCreationPeriod(period);
        return prediction;
    }

    protected PredictionHallInfo getPredictionHallInfo(LineWithPastPeriods upLine, LineWithPastPeriods bottomLine) throws BadPredictionSituationException {
        prediction.setUpLine(upLine);
        prediction.setBottomLine(bottomLine);
        PointChain pointChain = chainCreator.createChain(upLine, bottomLine);
        prediction.setSituation(pointChain.getSituation());
        arrowsCreator.createAndInjectArrows(prediction, pointChain);

        return prediction;
    }


    public void setChainCreator(PointsChainCreator chainCreator) {
        this.chainCreator = chainCreator;
    }

    public void setArrowsCreator(ArrowsCreator arrowsCreator) {
        this.arrowsCreator = arrowsCreator;
    }

}
