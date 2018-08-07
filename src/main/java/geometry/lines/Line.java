package geometry.lines;


public class Line {

    private Point start;
    private Point defining;
    private Double tg;

    public Line(Point start, Point defining) {
        this.start = start;
        this.defining = defining;
        this.tg = calculateTan();
    }

    protected Double calculateTan() {
        Point parallelToStart = new Point(defining.getX(), start.getY());
        Double a = parallelToStart.getX() - start.getX();
        Double b = Math.abs(parallelToStart.getY() - defining.getY());

        tg = b / a;
        return tg;
    }


    public Double predictYAfterX(double x) {
        Point predictedParallelToStart = new Point(defining.getX() + x, start.getY());
        Double a = predictedParallelToStart.getX() - start.getX();
        Double b = a * tg;

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
