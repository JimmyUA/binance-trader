package geometry.prediction;

import geometry.lines.LineWithPastPeriods;
import geometry.lines.Point;
import geometry.prediction.exception.BadPredictionSituationException;
import geometry.prediction.situation.PointsChainSituation;
import geometry.prediction.situation.SituationDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PointsChainCreator {

    @Autowired
    private SituationDetector detector;

    private  Integer upStartIndex;
    private  Integer upDefiningIndex;
    private  Integer bottomStartIndex;
    private  Integer bottomDefiningIndex;

    public  PointChain createChain(LineWithPastPeriods upLine, LineWithPastPeriods bottomLine) throws BadPredictionSituationException {
        upStartIndex = upLine.getStartPointIndex();
        upDefiningIndex = upLine.getDefiningPointIndex();

        bottomStartIndex = bottomLine.getStartPointIndex();
        bottomDefiningIndex = bottomLine.getDefiningPointIndex();

        if (isValidSituation()) {
            PointsChainSituation situation = getPointsChainSituation();

            Point startPoint;
            Point secondPoint;
            Point thirdPoint;
            Point forthPoint = null;
            if (situation.equals(PointsChainSituation.PLATO)){
                startPoint = upLine.getStartPoint();
                secondPoint = bottomLine.getDefiningPoint();
                thirdPoint = upLine.getDefiningPoint();

            } else if(situation.equals(PointsChainSituation.FRONT_BLADE)){
                startPoint = upLine.getStartPoint();
                secondPoint = bottomLine.getStartPoint();
                thirdPoint = upLine.getDefiningPoint();
                forthPoint = bottomLine.getDefiningPoint();
            } else {
                startPoint = bottomLine.getStartPoint();
                secondPoint = upLine.getStartPoint();
                thirdPoint = bottomLine.getDefiningPoint();
                forthPoint = upLine.getDefiningPoint();
            }


            return new PointChain()
                    .setStartPoint(startPoint)
                    .setSecondPoint(secondPoint)
                    .setThirdPoint(thirdPoint)
                    .setForthPoint(forthPoint)
                    .setSituation(situation);
        } else {
            throw new BadPredictionSituationException("Points are not creating valid situation!");
        }
    }

    private PointsChainSituation getPointsChainSituation() {
        return detector.detectSituation(upStartIndex, upDefiningIndex, bottomStartIndex, bottomDefiningIndex);
    }

    private boolean isValidSituation() {

        return (bottomStartIndex > upStartIndex && bottomStartIndex < upDefiningIndex) ||
                bottomDefiningIndex > upStartIndex && bottomDefiningIndex < upDefiningIndex;
    }

    protected void setDetector(SituationDetector detector){
        this.detector = detector;
    }
}
