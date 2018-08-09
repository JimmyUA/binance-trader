package geometry.prediction;

import geometry.lines.InterceptionFinder;
import geometry.lines.Line;
import geometry.lines.LinesCreator;
import geometry.lines.Point;
import geometry.prediction.situation.PointsChainSituation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArrowsCreator {

    @Autowired
    private LinesCreator linesCreator;

    @Autowired
    private InterceptionFinder interceptionFinder;

    private PredictionHallInfo prediction;
    private PointChain pointChain;
    private Line firstArrow;
    private Line secondArrow;
    private Line thirdArrow;
    private Line forthArrow;
    private Line fifthArrow;

    public void createAndInjectArrows(PredictionHallInfo prediction, PointChain pointChain) {
        this.prediction = prediction;
        this.pointChain = pointChain;

        firstArrow = getFirstArrow();
        secondArrow = getSecondArrow();
        thirdArrow = getThirdArrow();
        forthArrow = getForthArrow();
        fifthArrow = getFifthArrow();

        injectArrows();
    }

    private void injectArrows() {
        prediction.setFirstArrow(firstArrow);
        prediction.setSecondArow(secondArrow);
        prediction.setThirdArrow(thirdArrow);
        prediction.setForthArrow(forthArrow);
        prediction.setFifthArrow(fifthArrow);
    }

    protected Line getFifthArrow() {
        Line lastArrowInterceptedLine;
        if (pointChain.getSituation().equals(PointsChainSituation.BACK_BLADE)) {
            lastArrowInterceptedLine = prediction.getBottomLine().getLine();
        } else {
            lastArrowInterceptedLine = prediction.getUpLine().getLine();
        }
        Point startPoint = interceptionFinder.findInterception(forthArrow, lastArrowInterceptedLine);
        return linesCreator.createParallelLine(secondArrow, startPoint);
    }

    protected Line getForthArrow() {
        Point startPoint;
        if (pointChain.getSituation().equals(PointsChainSituation.PLATO)) {
            startPoint = interceptionFinder.findInterception(thirdArrow, prediction.getBottomLine().getLine());
        } else {
            startPoint = pointChain.getForthPoint();
        }
        return linesCreator.createParallelLine(secondArrow, startPoint);
    }

    protected Line getThirdArrow() {
        if (!pointChain.getSituation().equals(PointsChainSituation.PLATO)) {
            return new Line(pointChain.getThirdPoint(), pointChain.getForthPoint());
        } else {
            return linesCreator.createParallelLine(firstArrow, pointChain.getThirdPoint());
        }
    }

    protected Line getSecondArrow() {
        return new Line(pointChain.getSecondPoint(), pointChain.getThirdPoint());

    }

    protected Line getFirstArrow() {
        return new Line(pointChain.getStartPoint(), pointChain.getSecondPoint());
    }

    protected void setInfoForTest(PredictionHallInfo prediction, PointChain pointChain){
        this.prediction = prediction;
        this.pointChain = pointChain;
    }
}
