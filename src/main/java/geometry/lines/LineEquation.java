package geometry.lines;

public class LineEquation {

    private Double a;
    private Double b;
    private Double c;

    public LineEquation(Point start, Point defining) {
        Double x1 = start.getX();
        Double x2 = defining.getX();
        Double y1 = start.getY();
        Double y2 = defining.getY();
        this.a = y1 - y2;
        this.b = x2 - x1;
        this.c = (x1*y2) - (x2*y1);
    }

    public Double getA() {
        return a;
    }

    public Double getB() {
        return b;
    }

    public Double getC() {
        return c;
    }
}
