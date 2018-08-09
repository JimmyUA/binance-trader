package geometry.lines;

public class Point {

    private Double x;
    private Double y;

    private Integer index;

    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;

        Point point = (Point) o;

        if (!getX().equals(point.getX())) return false;
        return getY().equals(point.getY());
    }

    @Override
    public int hashCode() {
        int result = getX().hashCode();
        result = 31 * result + getY().hashCode();
        return result;
    }
}
