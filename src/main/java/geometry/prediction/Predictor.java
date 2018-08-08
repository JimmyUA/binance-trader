package geometry.prediction;

import geometry.lines.LineWithPastPeriods;

public class Predictor {

    public Double predictPriceAfterPeriodsOnLine(LineWithPastPeriods line, int periodsAfter){

        return line.predictPriceAfter(periodsAfter);
    }

    public PredictionHallInfo getPredictionHallInfo(LineWithPastPeriods upLine, LineWithPastPeriods bottomLine){
        PredictionHallInfo prediction = new PredictionHallInfo();
        PointChain pointChain = PointsChainCreator.createChain(upLine, bottomLine);

        return prediction;
    }
}
