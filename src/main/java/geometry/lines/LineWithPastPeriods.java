package geometry.lines;

public class LineWithPastPeriods {

    private Line line;
    private Integer pastPeriods;

    public LineWithPastPeriods(Line line, Integer pastPeriods) {
        this.line = line;
        this.pastPeriods = pastPeriods;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Integer getPastPeriods() {
        return pastPeriods;
    }

    public void setPastPeriods(Integer pastPeriods) {
        this.pastPeriods = pastPeriods;
    }

    public Double predictPriceAfter(int periodsAfter) {
        return line.predictYAfterX(pastPeriods + periodsAfter);
    }

    public Integer getStartPointIndex(){
        return line.getStartPointIndex();
    }

    public Integer getDefiningPointIndex(){
        return line.getDefiningPointIndex();
    }

    public Point getStartPoint() {
        return line.getStartPoint();
    }

    public Point getDefiningPoint() {
        return line.getDefiningPoint();
    }

    @Override
    public String toString() {
        return "LineWithPastPeriods{" +
                "line=" + line +
                ", pastPeriods=" + pastPeriods +
                '}';
    }
}
