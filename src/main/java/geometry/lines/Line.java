package geometry.lines;


public class Line {

    private Point start;
    private Point defining;
    private Double tan;

    public Line(Point start, Point defining) {
        this.start = start;
        this.defining = defining;
        this.tan = calculateTan();
    }

    protected Double calculateTan() {
        Point parallelToStart = new Point(defining.getX(), start.getY());
        Double a = parallelToStart.getX() - start.getX();
        Double b = Math.abs(parallelToStart.getY() - defining.getY());

        tan = b / a;
        return tan;
    }

    public Double getTan() {
        return tan;
    }



    public Double predictYAfterX(double x) {
        Point predictedParallelToStart = new Point(defining.getX() + x, start.getY());
        Double a = predictedParallelToStart.getX() - start.getX();
        Double b = a * tan;

        if (isLineAscending()) {
            return start.getY() + b;
        } else {
            return start.getY() - b;
        }
    }

    public boolean isLineAscending() {
        return start.getY() < defining.getY();
    }
}
