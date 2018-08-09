package geometry.lines;

import org.junit.Test;

import static org.junit.Assert.*;

public class LineEquationTest {

    @Test
    public void shouldCalculateCorrect() throws Exception {
        Point first = new Point(0.0, 0.0);
        Point second = new Point(45.0, 0.0);

        LineEquation equation = new LineEquation(first, second);

        assertEquals(0.0, equation.getA(), 0.0001);
        assertEquals(45.0, equation.getB(), 0.0001);
        assertEquals(0.0, equation.getC(), 0.0001);
    }
}