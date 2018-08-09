package geometry.lines;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InterceptionFinderTest {

    private InterceptionFinder finder;


    @Before
    public void setUp() throws Exception {
        finder = new InterceptionFinder();

    }

    @Test
    public void shouldFindInterceptionCorrect() throws Exception {

        Point first = new Point(20.0, 500.0);
        Point second = new Point(45.0, 0.0);
        Line firstLine = new Line(first, second);

        Point third = new Point(45.0, 2.1);

        Line secondLine = new Line(first, third);

        assertEquals(first, finder.findInterception(firstLine, secondLine));
    }

    @Test
    public void shouldFindInterceptionInCoordinatesStartCorrect() throws Exception {

        Point first = new Point(0.0, 0.0);
        Point second = new Point(45.0, 0.0);
        Line firstLine = new Line(first, second);

        Point third = new Point(45.0, 2.1);

        Line secondLine = new Line(first, third);

        assertEquals(first, finder.findInterception(firstLine, secondLine));
    }
}