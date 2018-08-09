package geometry.prediction;

import geometry.lines.Line;
import geometry.lines.LineWithPastPeriods;

public class PredictionHallInfo {

    private Line firstArrow;
    private Line secondArow;
    private Line thirdArrow;
    private Line forthArrow;
    private Line fifthArrow;
    private LineWithPastPeriods upLine;
    private LineWithPastPeriods bottomLine;


    public void setUpLine(LineWithPastPeriods upLine) {
        this.upLine = upLine;
    }

    public void setBottomLine(LineWithPastPeriods bottomLine) {
        this.bottomLine = bottomLine;
    }

    public LineWithPastPeriods getBottomLine() {
        return bottomLine;
    }

    public LineWithPastPeriods getUpLine() {
        return upLine;
    }

    public Line getFirstArrow() {
        return firstArrow;
    }

    public void setFirstArrow(Line firstArrow) {
        this.firstArrow = firstArrow;
    }

    public Line getSecondArow() {
        return secondArow;
    }

    public void setSecondArow(Line secondArow) {
        this.secondArow = secondArow;
    }

    public Line getThirdArrow() {
        return thirdArrow;
    }

    public void setThirdArrow(Line thirdArrow) {
        this.thirdArrow = thirdArrow;
    }

    public Line getForthArrow() {
        return forthArrow;
    }

    public void setForthArrow(Line forthArrow) {
        this.forthArrow = forthArrow;
    }

    public Line getFifthArrow() {
        return fifthArrow;
    }

    public void setFifthArrow(Line fifthArrow) {
        this.fifthArrow = fifthArrow;
    }
}
