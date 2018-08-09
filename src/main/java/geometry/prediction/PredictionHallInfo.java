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
}
