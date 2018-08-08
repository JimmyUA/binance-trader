package geometry.prediction;

import geometry.lines.Point;

public class PointChain {

    private Point startPoint;

    public PointChain setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
        return this;
    }
}
