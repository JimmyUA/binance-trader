package geometry.prediction;

import geometry.lines.Line;
import geometry.lines.LinesCreator;
import geometry.prediction.situation.PointsChainSituation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArrowsCreator {

    @Autowired
    private LinesCreator linesCreator;

    private PredictionHallInfo prediction;
    private PointChain pointChain;
    private Line firstArrow;
    private Line secondArrow;
    private Line thirdArrow;

    public void createAndInjectArrows(PredictionHallInfo prediction, PointChain pointChain) {
        this.prediction = prediction;
        this.pointChain = pointChain;

        firstArrow = getFirstArrow();
        secondArrow = getSecondArrow();
        thirdArrow = getThirdArrow();
    }

    private Line getThirdArrow() {
        if (!pointChain.getSituation().equals(PointsChainSituation.PLATO)) {
            return new Line(pointChain.getThirdPoint(), pointChain.getForthPoint());
        } else {
            return linesCreator.createParallelLine(firstArrow, pointChain.getThirdPoint());
        }
    }

    private Line getSecondArrow() {
        return new Line(pointChain.getSecondPoint(), pointChain.getThirdPoint());

    }

    private Line getFirstArrow() {
        return new Line(pointChain.getStartPoint(), pointChain.getSecondPoint());
    }
}
