package io.github.unterstein.statistic.RSI;

import io.github.unterstein.BinanceTrader;
import io.github.unterstein.statistic.PricesAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RSI {
    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);

    @Autowired
    private PricesAccumulator pricesAccumulator;
    private List<Double> gains;
    private List<Double> losses;
    private List<Double> prises;
    private Double currentGain;
    private Double currentLoss;
    private int periods;

    public void setPricesAccumulator(PricesAccumulator pricesAccumulator) {
        this.pricesAccumulator = pricesAccumulator;
    }

    public Double getRSI(int periods){
        this.periods = periods;
        initPrices();
        Double rs = RS();
        return 100 - (100 / (1 + rs));
    }

    private void initPrices() {
        prises = pricesAccumulator.get100Samples();
        initGainsAndLosses();
    }

    private void initGainsAndLosses() {
        gains = new ArrayList<>();
        losses = new ArrayList<>();
        for (int i = 0; i < periods; i++) {
            Double last = prises.get(prises.size() - (i + 1));
            Double previous = prises.get(prises.size() - (i + 2));
            double difference = last - previous;
            if (difference >= 0.0){
                gains.add(difference);
                if (i == 0){
                    currentGain = difference;
                    currentLoss = 0.0;
                }
            } else {
                difference = Math.abs(difference);
                losses.add(difference);
                if (i == 0){
                    currentGain = 0.0;
                    currentLoss = difference;
                }
            }
        }


    }


    private Double RS() {
        double onlyPreviousGain = previousAverageGain() * periods - 1;
        double fullGain = onlyPreviousGain + currentGain;
        double onlyPreviousLoss = previousAverageLoss() * periods - 1;
        double fullLoss = onlyPreviousLoss + currentLoss;
        double averageGain = fullGain / periods;
        double averageLoss = fullLoss / periods;
        return previousAverageGain() / previousAverageLoss();
    }


    private Double previousAverageLoss() {
        double averageLoss = totalLoss() / periods;
        return averageLoss;
    }

    private Double totalGain() {
        return gains.stream().mapToDouble(d -> d).sum();
    }

    private Double previousAverageGain() {
        double averageGain = totalGain() / periods;
        return averageGain;
    }

    private Double totalLoss() {
        return losses.stream().mapToDouble(d -> d).sum();
    }

}
