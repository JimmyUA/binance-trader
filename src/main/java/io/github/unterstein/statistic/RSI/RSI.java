package io.github.unterstein.statistic.RSI;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.BinanceTrader;
import io.github.unterstein.TradingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RSI {
    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);

    @Autowired
    private TradingClient client;

    private List<Double> gains;
    private List<Double> losses;
    private LinkedList<Double> prises;
    private Double currentGain;
    private Double currentLoss;
    private int periods;


    public Double getRSI(int periods){
        this.periods = periods;
        initPrices();
        Double rs = RS();
        return 100 - (100 / (1 + rs));
    }

    private void initPrices() {
        prises = client.getPricesFromExchangeReversed(CandlestickInterval.ONE_MINUTE).stream()
                .limit(periods * 2).collect(Collectors.toCollection(LinkedList::new));
        initGainsAndLosses();
    }

    private void initGainsAndLosses() {
        gains = new ArrayList<>();
        losses = new ArrayList<>();
        for (int i = 0; i < periods; i++) {
            Double last = prises.pollFirst();
            Double previous = prises.getFirst();
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
