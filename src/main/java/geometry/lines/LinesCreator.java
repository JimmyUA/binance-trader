package geometry.lines;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.TradingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.stream.Collectors;

@Component
public class LinesCreator {

    @Autowired
    private TradingClient client;

    public LineWithPastPeriods createHallUpLine(Long period, CandlestickInterval interval){

        LinkedList<Double> cutPrices = getCutPrices(period, interval);

        LinkedList<Double> cutPricesForIndexGetting = new LinkedList<>(cutPrices);

        double firstMax = cutPrices.stream().mapToDouble(d -> d).max().orElse(0.0);
        int firstMaxIndex = cutPricesForIndexGetting.indexOf(firstMax);
        cutPrices.remove(firstMax);

        double secondMax = cutPrices.stream().mapToDouble(d -> d).max().orElse(0.0);
        int secondMaxIndex = cutPricesForIndexGetting.indexOf(secondMax);

        int maxIndex = getMaxIndex(firstMaxIndex, secondMaxIndex);

        Line line = getLine(firstMax, firstMaxIndex, secondMax, secondMaxIndex);
        int pastPeriods = cutPricesForIndexGetting.size() - maxIndex - 1;

        return new LineWithPastPeriods(line, pastPeriods);
    }

    private int getMaxIndex(int firstMaxIndex, int secondMaxIndex) {
        return getMinIndex(firstMaxIndex, secondMaxIndex);
    }

    public LineWithPastPeriods createHallBottomLine(long period, CandlestickInterval interval) {
        LinkedList<Double> cutPrices = getCutPrices(period, interval);

        LinkedList<Double> cutPricesForIndexGetting = new LinkedList<>(cutPrices);

        double firstMin = cutPrices.stream().mapToDouble(d -> d).min().orElse(0.0);
        int firstMinIndex = cutPricesForIndexGetting.indexOf(firstMin);
        cutPrices.remove(firstMin);

        double secondMin = cutPrices.stream().mapToDouble(d -> d).min().orElse(0.0);
        int secondMinIndex = cutPricesForIndexGetting.indexOf(secondMin);

        int minIndex = getMinIndex(firstMinIndex, secondMinIndex);

        Line line = getLine(firstMin, firstMinIndex, secondMin, secondMinIndex);
        int pastPeriods = cutPricesForIndexGetting.size() - minIndex - 1;

        return new LineWithPastPeriods(line, pastPeriods);
    }

    private int getMinIndex(int firstMinIndex, int secondMinIndex) {
        return firstMinIndex > secondMinIndex ? firstMinIndex : secondMinIndex;
    }

    private LinkedList<Double> getCutPrices(Long period, CandlestickInterval interval){
        LinkedList<Double> prices = new LinkedList<>(client.getPricesFromExchange(interval));

        if (period >= prices.size()){
            throw new IllegalArgumentException("period should be less than prices amount");
        }
        return prices.stream()
                .skip(prices.size() - period)
                .collect(Collectors.toCollection(LinkedList::new));
    }


    private Line getLine(Double firstMax, int firstMaxIndex, Double secondMax, int secondMaxIndex){
        double xStart;
        double yStart;
        double xDefining;
        double yDefining;

        int startIndex;
        int defIndex;

        if (firstMaxIndex < secondMaxIndex){
            xStart = firstMaxIndex;
            yStart = firstMax;
            xDefining = secondMaxIndex;
            yDefining = secondMax;
            startIndex = firstMaxIndex;
            defIndex = secondMaxIndex;
        } else {
            xStart = secondMaxIndex;
            yStart = secondMax;
            xDefining = firstMaxIndex;
            yDefining = firstMax;

            startIndex = secondMaxIndex;
            defIndex = firstMaxIndex;
        }

        Point start = new Point(xStart, yStart);
        start.setIndex(startIndex);
        Point defining = new Point(xDefining, yDefining);
        defining.setIndex(defIndex);
        return new Line(start, defining);
    }

    protected void setClient(TradingClient client) {
        this.client = client;
    }


    public Line createParallelLine(Line sourceLine, Point startPoint) {
        Point start = startPoint;

        Point defining = calculateDefiningPoint(startPoint, sourceLine);

        Line resultLine = new Line(start, defining);

        return resultLine;
    }

    private Point calculateDefiningPoint(Point startPoint, Line line) {
        double y1 = startPoint.getY();
        double x1 = startPoint.getX();
        double y2 = 0;

        if(y1 == 0.0){
            y2 = 1;
        }

        double A2 = y1 - y2;

        LineEquation lineEquation = line.getLineEquation();
        double A1 = lineEquation.getA();
        double B1 = lineEquation.getB();
        double C1 = lineEquation.getC();

        double B2 = (A2*B1)/A1;
        double x2 = B2 + x1;

        return new Point(x2, y2);
    }
}
