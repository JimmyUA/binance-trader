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

import java.util.NoSuchElementException;

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
    private Integer intervalDivider;
    private PointsChainSituation situation;

    public PredictionPoint getFinalPrediction(){
        return new PredictionPoint();
    }

    public Double predictPriceAfterPeriod(long period) throws TooLongFutureAccessRequestException {

        long periodLeftAfterCreation = calculatePeriodsLeftAfterCreation();
        long lastAvailableIndex = findLastAvailableIndex();

        long requestedIndex = 500 + periodLeftAfterCreation + period;
        if (requestedIndex > lastAvailableIndex){
            throw new TooLongFutureAccessRequestException(String.format("Requested index %d more than available %d",
                    requestedIndex, lastAvailableIndex));
        }


        return findPriceByIndex(requestedIndex);
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
        return (currentTime - creationTime) / intervalDivider;
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
           intervalDivider = 1000 * 60;
       } else if(interval.equals(CandlestickInterval.FIVE_MINUTES)){
           intervalDivider = 1000 * 60 * 5;
       }else if(interval.equals(CandlestickInterval.FIFTEEN_MINUTES)){
           intervalDivider = 1000 * 60 * 15;
       }else if(interval.equals(CandlestickInterval.HALF_HOURLY)){
           intervalDivider = 1000 * 60 * 30;
       }
    }

    public void setSituation(PointsChainSituation situation) {
        this.situation = situation;
    }
}
