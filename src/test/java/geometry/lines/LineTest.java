package geometry.lines;

import org.junit.Test;

import static org.junit.Assert.*;

public class LineTest {

    @Test
    public void tgShouldBeZero() throws Exception {

        Double expectedTan = 0.0;

        Point start = new Point(0.0, 0.0);
        Point defining = new Point(3.0, 0.0);
        Line line = new Line(start, defining);

        assertEquals(expectedTan, line.calculateTan());
    }

    @Test
    public void sinusShouldBeOne() throws Exception {

        Double expectedTan = 1.0;

        Point start = new Point(0.0, 0.0);
        Point defining = new Point(3.0, 3.0);
        Line line = new Line(start, defining);

        assertEquals(expectedTan, line.calculateTan());
    }

    @Test
    public void shouldPredictPointSixSix() throws Exception {
        Double expectedY = 6.0;

        Point start = new Point(0.0, 0.0);
        Point defining = new Point(3.0, 3.0);
        Line line = new Line(start, defining);

        assertEquals(expectedY, line.predictYAfterX(3.0));
    }

    @Test
    public void shouldFindZero() {
        double expectedY = 0.0;

        Point start = new Point(0.0, 0.0);
        Point defining = new Point(3.0, 0.0);
        Line line = new Line(start, defining);

        assertEquals(expectedY, line.findYByX(6.0), 0.0001 );
    }

    @Test
    public void shouldFindThree() {
        double expectedY = 3.0;

        Point start = new Point(0.0, 0.0);
        Point defining = new Point(1.0, 1.0);
        Line line = new Line(start, defining);

        assertEquals(expectedY, line.findYByX(3.0), 0.0001 );
    }
}