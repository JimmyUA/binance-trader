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
}
