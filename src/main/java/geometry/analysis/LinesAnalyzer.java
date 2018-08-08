package geometry.analysis;

import geometry.lines.Line;

public class LinesAnalyzer {

    public boolean isLineAscending(Line line) {
        return line.isLineAscending();
    }

    public boolean isParallel(Line first, Line second){
        if (isDifferentDirected(first, second)){
            return false;
        }
        Double firstTan = first.getTan();
        Double secondTan = second.getTan();
        double tanDifference = firstTan - secondTan;

        return Math.abs(tanDifference) < 0.1;
    }

    private boolean isDifferentDirected(Line first, Line second) {
        return (first.isLineAscending() && !second.isLineAscending()) ||
                (!first.isLineAscending() && second.isLineAscending());
    }
}
