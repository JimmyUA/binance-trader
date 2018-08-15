package geometry.lines;


public class Line {

    private Point start;
    private Point defining;
    private Double tan;
    private LineEquation lineEquation;

    public Line(Point start, Point defining) {
        this.start = start;
        this.defining = defining;
        if (start.getX() > defining.getX()){
            swapPoints();
        }
        this.lineEquation = calculateEquation();
        this.tan = calculateTan();
    }

    private void swapPoints() {
        Point temp = start;
        start = defining;
        defining = temp;
    }

    private LineEquation calculateEquation() {
        return new LineEquation(start, defining);
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

    public Double findYByX(Double X){
        double A = lineEquation.getA();
        double B = lineEquation.getB();
        double C = lineEquation.getC();

        double Y = ((-A * X) - C) / B;

        return Y;
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

    public boolean isLineDescending() {
        return !isLineAscending();
    }

    public Integer getStartPointIndex() {
        return start.getIndex();
    }

    public Integer getDefiningPointIndex() {
        return defining.getIndex();
    }

    public Point getStartPoint() {
        return start;
    }

    public Point getDefiningPoint() {
        return defining;
    }

    public LineEquation getLineEquation() {
        return lineEquation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;

        Line line = (Line) o;

        if (!start.equals(line.start)) return false;
        return defining.equals(line.defining);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + defining.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Line{" +
                "start=" + start +
                ", defining=" + defining +
                ", tan=" + tan +
                ", lineEquation=" + lineEquation +
                '}';
    }


}
