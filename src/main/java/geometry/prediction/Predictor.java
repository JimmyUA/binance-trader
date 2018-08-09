package geometry.prediction;

import geometry.lines.LineWithPastPeriods;
import geometry.prediction.exception.BadPredictionSituationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Predictor {

    @Autowired
    private PointsChainCreator ChainCreator;

    @Autowired
    private ArrowsCreator arrowsCreator;

    public Double predictPriceAfterPeriodsOnLine(LineWithPastPeriods line, int periodsAfter){

        return line.predictPriceAfter(periodsAfter);
    }

    public PredictionHallInfo getPredictionHallInfo(LineWithPastPeriods upLine, LineWithPastPeriods bottomLine) throws BadPredictionSituationException {
        PredictionHallInfo prediction = new PredictionHallInfo();
        prediction.setUpLine(upLine);
        prediction.setBottomLine(upLine);
        PointChain pointChain = ChainCreator.createChain(upLine, bottomLine);
        arrowsCreator.createAndInjectArrows(prediction, pointChain);

        return prediction;
    }
}
