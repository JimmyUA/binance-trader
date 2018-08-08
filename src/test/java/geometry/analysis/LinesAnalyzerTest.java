package geometry.analysis;

import geometry.lines.Line;
import geometry.lines.Point;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LinesAnalyzerTest {

    private Line firstLine;
    private Line secondLine;

    @Before
    public void setUp() throws Exception {
        Point first = new Point(0.0, 0.0);
        Point second = new Point(45.0, 0.0);
        firstLine = new Line(first, second);
        Point third = new Point(45.0, 0.1);
        secondLine = new Line(first, third);
    }

    @Test
    public void sameLineShouldBeParallel() throws Exception {

        assertTrue(new HallLinesAnalyzer().isParallel(firstLine, firstLine));
    }

    @Test
    public void linesShouldBeParallel() throws Exception {
        Point first = new Point(0.0, 0.0);
        Point second = new Point(40.0, 0.1);
        firstLine = new Line(first, second);

        assertTrue(new HallLinesAnalyzer().isParallel(firstLine, secondLine));
    }

    @Test
    public void linesShouldBeNotParallel() throws Exception {
        Point first = new Point(0.0, 0.0);
        Point second = new Point(45.0, 5.0);
        firstLine = new Line(first, second);

        assertFalse(new HallLinesAnalyzer().isParallel(firstLine, secondLine));
    }

    @Test
    public void differentDirectedLinesShouldBeNotParallel() throws Exception {
        Point first = new Point(0.0, 0.0);
        Point second = new Point(45.0, 5.0);
        firstLine = new Line(first, second);

        first = new Point(0.0, 5.0);
        second = new Point(45.0, 0.0);
        secondLine = new Line(first, second);

        assertFalse(new HallLinesAnalyzer().isParallel(firstLine, secondLine));
    }
}