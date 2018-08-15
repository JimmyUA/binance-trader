package geometry.prediction;

import com.binance.api.client.domain.market.CandlestickInterval;
import geometry.lines.InterceptionFinder;
import geometry.lines.Line;
import geometry.lines.LineWithPastPeriods;
import geometry.lines.Point;
import geometry.prediction.exception.TooLongFutureAccessRequestException;
import geometry.prediction.situation.PointsChainSituation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Scope("prototype")
public class PredictionHallInfo {

    @Autowired
    private InterceptionFinder interceptionFinder;

    private Line firstArrow;
    private Line secondArrow;
    private Line thirdArrow;
    private Line forthArrow;
    private Line fifthArrow;
    private LineWithPastPeriods upLine;
    private LineWithPastPeriods bottomLine;
    private long creationTime;
    private Integer intervalKof;
    private PointsChainSituation situation;
    private long creationPeriod;


    public PredictionPoint getMaxPricePrediction(){
        Integer currentIndex = calculateCurrentIndex();
        List<Line> ascendingArrows = getAscendingArrows();
        double price = ascendingArrows.stream()
                .map(arrow -> interceptionFinder.findInterception(arrow, upLine.getLine()))
                .filter(point -> point.getX() > currentIndex)
                .map(Point::getY)
                .mapToDouble(d -> d).max()
                .orElse(0.0);
        Double pointIndex = ascendingArrows.stream()
                .map(arrow -> interceptionFinder.findInterception(arrow, upLine.getLine()))
                .filter(point -> point.getX() > currentIndex).
                filter(point -> point.getY().equals(price))
                .findFirst()
                .orElse(new Point(0.0, 0.0))
                .getX();
        LocalDateTime pointTime = calculateTime(pointIndex);
        return new PredictionPoint()
                .setPrice(price)
                .setTime(pointTime);
    }

    public PredictionPoint getMinPricePrediction(){
        Integer currentIndex = calculateCurrentIndex();
        List<Line> descendingArrows = getDescendingArrows();
        double price = descendingArrows.stream()
                .map(arrow -> interceptionFinder.findInterception(arrow, bottomLine.getLine()))
                .filter(point -> point.getX() > currentIndex)
                .map(Point::getY)
                .mapToDouble(d -> d).min()
                .orElse(0.0);
        Double pointIndex = descendingArrows.stream()
                .map(arrow -> interceptionFinder.findInterception(arrow, upLine.getLine()))
                .filter(point -> point.getX() > currentIndex).
                filter(point -> point.getY().equals(price))
                .findFirst()
                .orElse(new Point(0.0, 0.0))
                .getX();
        LocalDateTime pointTime = calculateTime(pointIndex);
        return new PredictionPoint()
                .setPrice(price)
                .setTime(pointTime);
    }

    public PredictionPoint getFinalPrediction(){
        Point lastInterception = findLastArrowInterception();
        PredictionPoint predictionPoint = new PredictionPoint();
        predictionPoint.setPrice(lastInterception.getY());
        LocalDateTime pointTime = calculateTime(lastInterception.getX());
        predictionPoint.setTime(pointTime);
        return predictionPoint;
    }

    public Double predictPriceAfterPeriod(long period) throws TooLongFutureAccessRequestException {

        long periodLeftAfterCreation = calculatePeriodsLeftAfterCreation();
        long lastAvailableIndex = findLastAvailableIndex();

        long requestedIndex = creationPeriod + periodLeftAfterCreation + period;
        if (requestedIndex > lastAvailableIndex){
            throw new TooLongFutureAccessRequestException(String.format("Requested index %d more than available %d",
                    requestedIndex, lastAvailableIndex));
        }


        return findPriceByIndex(requestedIndex);
    }

    private Integer calculateCurrentIndex() {
        long currentTime = System.currentTimeMillis();
        return Math.toIntExact(((currentTime - creationTime) / intervalKof) + creationPeriod);
    }

    private List<Line> getAscendingArrows() {
        return Stream.of(firstArrow, secondArrow, thirdArrow, forthArrow, fifthArrow)
                .filter(Objects::nonNull)
                .filter(Line::isLineAscending).collect(Collectors.toCollection(LinkedList::new));
    }


    private List<Line> getDescendingArrows() {
        return Stream.of(firstArrow, secondArrow, thirdArrow, forthArrow, fifthArrow)
                .filter(Objects::nonNull)
                .filter(Line::isLineDescending).collect(Collectors.toCollection(LinkedList::new));
    }



    private LocalDateTime calculateTime(Double index) {
        long timeFromCreation = (long) (index - creationPeriod) * intervalKof;
        long predictionPointTime = creationTime + timeFromCreation;
        return Instant.ofEpochMilli(predictionPointTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private Double findPriceByIndex(long requestedIndex) {
        Line goalArrow = findAppropriateArrow(requestedIndex);
        double x = requestedIndex;
        Point start = new Point(x, 0.0);
        Point defining = new Point(x, 10.0);
        Line verticalInterceptor = new Line (start, defining);
        Point interception = interceptionFinder.findInterception(goalArrow, verticalInterceptor);
        return interception.getY();
    }

    private Line findAppropriateArrow(long requestedIndex) {
        if (situation.equals(PointsChainSituation.BACK_BLADE) || situation.equals(PointsChainSituation.FRONT_BLADE)){
            long fifthStartIndex = fifthArrow.getStartPoint().getIndex();
            if (requestedIndex > fifthStartIndex){
                return fifthArrow;
            }
        }
            long forthStartIndex = forthArrow.getStartPoint().getIndex();
            long thirdStartIndex = thirdArrow.getStartPoint().getIndex();
            if (requestedIndex > forthStartIndex){
                return forthArrow;
            } else if (requestedIndex > thirdStartIndex){
                return thirdArrow;
            } else {
                throw new NoSuchElementException("Index was not found - something went wrong, if you see it check algorithm!");
            }
    }

    private long findLastAvailableIndex() {
        Point lastInterception = findLastArrowInterception();
        return lastInterception.getX().longValue();
    }

    private Point findLastArrowInterception() {
        Line lastArrow = choseLastArrow();
        Line goalHallLine = choseGoalHallLine();
        return interceptionFinder.findInterception(lastArrow, goalHallLine);
    }

    private Line choseGoalHallLine() {
        if (situation.equals(PointsChainSituation.PLATO) || situation.equals(PointsChainSituation.BACK_BLADE)){
            return upLine.getLine();
        } else {
            return bottomLine.getLine();
        }
    }

    private Line choseLastArrow() {
        Line lastArrow = fifthArrow;
        if (fifthArrow == null){
            lastArrow = forthArrow;
        }
        return lastArrow;
    }

    private long calculatePeriodsLeftAfterCreation(){
        long currentTime = System.currentTimeMillis();
        return (currentTime - creationTime) / intervalKof;
    }

    protected void setUpLine(LineWithPastPeriods upLine) {
        this.upLine = upLine;
    }

    protected void setBottomLine(LineWithPastPeriods bottomLine) {
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
        return secondArrow;
    }

    public void setSecondArow(Line secondArrow) {
        this.secondArrow = secondArrow;
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

    @Override
    public String toString() {
        return "PredictionHallInfo{" +
                "\nfirstArrow=" + firstArrow +
                ",\n secondArow=" + secondArrow +
                ",\n thirdArrow=" + thirdArrow +
                ",\n forthArrow=" + forthArrow +
                ",\n fifthArrow=" + fifthArrow +
                ",\n upLine=" + upLine +
                ",\n bottomLine=" + bottomLine +
                '}';
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public void setInterval(CandlestickInterval interval) {
       if(interval.equals(CandlestickInterval.ONE_MINUTE)){
           intervalKof = 1000 * 60;
       } else if(interval.equals(CandlestickInterval.FIVE_MINUTES)){
           intervalKof = 1000 * 60 * 5;
       }else if(interval.equals(CandlestickInterval.FIFTEEN_MINUTES)){
           intervalKof = 1000 * 60 * 15;
       }else if(interval.equals(CandlestickInterval.HALF_HOURLY)){
           intervalKof = 1000 * 60 * 30;
       }
    }

    public void setSituation(PointsChainSituation situation) {
        this.situation = situation;
    }

    public void setCreationPeriod(long creationPeriod) {
        this.creationPeriod = creationPeriod;
    }

    protected void setInterceptionFinder(InterceptionFinder interceptionFinder) {
        this.interceptionFinder = interceptionFinder;
    }
}
