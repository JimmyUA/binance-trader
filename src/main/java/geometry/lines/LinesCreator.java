package geometry.lines;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.TradingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class LinesCreator {

    @Autowired
    private TradingClient client;

    private int clearRegionKof = 5;

    public LineWithPastPeriods createHallUpLine(Long period, CandlestickInterval interval){

        LinkedList<Double> cutPrices = getCutPrices(period, interval);

        LinkedList<Double> cutPricesForIndexGetting = new LinkedList<>(cutPrices);

        double firstMax = cutPrices.stream().mapToDouble(d -> d).max().orElse(0.0);
        int firstMaxIndex = cutPricesForIndexGetting.indexOf(firstMax);
        clearRegion(firstMax, cutPrices, period);

        double secondMax = cutPrices.stream().mapToDouble(d -> d).max().orElse(0.0);
        int secondMaxIndex = cutPricesForIndexGetting.indexOf(secondMax);

        int maxIndex = getMaxIndex(firstMaxIndex, secondMaxIndex);

        Line line = getLine(firstMax, firstMaxIndex, secondMax, secondMaxIndex);
        line = correctLine(line, cutPricesForIndexGetting, "up", period);
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
        clearRegion(firstMin, cutPrices, period);

        double secondMin = cutPrices.stream().mapToDouble(d -> d).min().orElse(0.0);
        int secondMinIndex = cutPricesForIndexGetting.indexOf(secondMin);

        int minIndex = getMinIndex(firstMinIndex, secondMinIndex);

        Line line = getLine(firstMin, firstMinIndex, secondMin, secondMinIndex);

        line = correctLine(line, cutPricesForIndexGetting, "bottom", period);

        int pastPeriods = cutPricesForIndexGetting.size() - minIndex - 1;

        return new LineWithPastPeriods(line, pastPeriods);
    }


    private Line correctLine(Line line, LinkedList<Double> prices, String lineType, Long period) {
        Map<Double, Point> outPoints = new HashMap<>();
        int startIndex = line.getStartPoint().getX().intValue() + (int)(period/clearRegionKof);
        for (int i = startIndex; i < prices.size(); i++) {
            Double price = prices.get(i);            double X = i;
            Double Y = line.findYByX(X);
            double delta = 0.0;
            if (lineType.equals("bottom")) {
                if (price < Y && price > 0.0){
                delta = Y - price;
                outPoints.put(delta, new Point(X, price));
                }
            } else if (lineType.equals("up")){
                if (price > Y && Y > 0.0){
                delta = price - Y;
                outPoints.put(delta, new Point(X, price));
                }
            }
        }

        if(outPoints.size() == 0){
            return line;
        }

        double maxDelta = outPoints.keySet().stream().mapToDouble(d -> d).max().orElse(0.0);
        Point maxDeltaPoint = outPoints.get(maxDelta);

        if (maxDeltaPoint != null){
            line = new Line(line.getStartPoint(), maxDeltaPoint);
        }

        return line;
    }

    private void clearRegion(double price, LinkedList<Double> cutPrices, long period) {
        int index = cutPrices.indexOf(price);
        int regionKof = (int)period / clearRegionKof;
        int start = index - regionKof > 0 ? index - regionKof : 0;
        int end = index + regionKof < cutPrices.size() ? index + regionKof : cutPrices.size();
        cutPrices.subList(start, end).clear();

    }

    private int getMinIndex(int firstMinIndex, int secondMinIndex) {
        return firstMinIndex > secondMinIndex ? firstMinIndex : secondMinIndex;
    }

    private LinkedList<Double> getCutPrices(Long period, CandlestickInterval interval){
        LinkedList<Double> prices = new LinkedList<>(client.getPricesFromExchange(interval));

        if (period > prices.size()){
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

        if (firstMaxIndex < secondMaxIndex){
            xStart = firstMaxIndex;
            yStart = firstMax;
            xDefining = secondMaxIndex;
            yDefining = secondMax;
        } else {
            xStart = secondMaxIndex;
            yStart = secondMax;
            xDefining = firstMaxIndex;
            yDefining = firstMax;

        }

        Point start = new Point(xStart, yStart);
        Point defining = new Point(xDefining, yDefining);
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

    public void doubleClearRegionKof() {
        clearRegionKof *=2;
    }
}
