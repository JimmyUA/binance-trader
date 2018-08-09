package geometry.prediction;

import geometry.lines.Point;
import geometry.prediction.situation.PointsChainSituation;

public class PointChain {

    private PointsChainSituation pointsChainSituation;
    private Point startPoint;
    private Point secondPoint;
    private Point thirdPoint;
    private Point forthPoint;

    public PointChain setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
        return this;
    }

    public PointChain setSituation(PointsChainSituation situation) {
        this.pointsChainSituation = situation;
        return this;
    }

    public PointsChainSituation getSituation() {
        return pointsChainSituation;
    }

    public PointChain setSecondPoint(Point secondPoint) {
        this.secondPoint = secondPoint;
        return this;
    }

    public PointChain setThirdPoint(Point thirdPoint) {
        this.thirdPoint = thirdPoint;
        return this;
    }

    public PointChain setForthPoint(Point forthPoint) {
        this.forthPoint = forthPoint;
        return this;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getSecondPoint() {
        return secondPoint;
    }

    public Point getThirdPoint() {
        return thirdPoint;
    }

    public Point getForthPoint() {
        return forthPoint;
    }
}
