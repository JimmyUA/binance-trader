package geometry.lines;

import org.springframework.stereotype.Component;

@Component
public class InterceptionFinder {

    public Point findInterception(Line firstLine, Line secondLine){
        LineEquation firstLineEquation = firstLine.getLineEquation();
        double A1 = firstLineEquation.getA();
        double B1 = firstLineEquation.getB();
        double C1 = firstLineEquation.getC();

        LineEquation secondLineEquation = secondLine.getLineEquation();

        double A2 = secondLineEquation.getA();
        double B2 = secondLineEquation.getB();
        double C2 = secondLineEquation.getC();

        double firstMember = C2 * A1;
        double secondMember = A2 * C1;
        double y = (secondMember - firstMember)/((A1*B2)-(A2*B1));
        if(y == -0.0){
            y = 0.0;
        }
        double upExpression = -(B1 * y) - C1;
        double x;
        if (upExpression == 0.0){
            x = 0.0;
        } else {
            x = upExpression / A1;
        }

        return new Point(x, y);
    }
}
