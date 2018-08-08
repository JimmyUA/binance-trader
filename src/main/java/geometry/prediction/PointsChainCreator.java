package geometry.prediction;

import geometry.lines.LineWithPastPeriods;
import geometry.lines.Point;

public class PointsChainCreator {
    public static PointChain createChain(LineWithPastPeriods upLine, LineWithPastPeriods bottomLine) {
        Integer upStartIndex = upLine.getStartPointIndex();
        Integer upDefiningIndex = upLine.getDefiningPointIndex();

        Integer bottomStartIndex = bottomLine.getStartPointIndex();
        Integer bottomDefiningIndex = bottomLine.getDefiningPointIndex();


        Point startPoint = upLine.getStartPoint();
        PointChain pointChain = new PointChain()
                .setStartPoint(startPoint)
                .setSecondPoint(secondPoint)
                .setThirdPoint(secondPoint)
                .setForthPoint(secondPoint);


        return pointChain;
    }
}
