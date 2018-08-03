package io.github.unterstein.statistic.EMA;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.TradingClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class ExponentialMovingAverage {


    private static LinkedList<Double> prices = new LinkedList<>();

    @Autowired
    private static TradingClient client;

    private static CandlestickInterval intervalValue;

    public static double EMA(Integer period, CandlestickInterval interval) {
        intervalValue = interval;
        LinkedList<Double> EMAs = new LinkedList<>();


        EMAs.add(firstEMA(period));

        Iterable<? extends Double> remainedPrices = prices.stream().skip(period).collect(Collectors.toList());


        for (Double currentPrice : remainedPrices
                ) {
            double currentEMA = notFirstEMA(period, EMAs.getLast(), currentPrice);
            EMAs.add(currentEMA);
        }

        return EMAs.getLast();
    }

    private static double firstEMA(int period) {
        initPrices();
        return prices.stream().
                limit(period).mapToDouble(price -> price).average().orElse(0.0);
    }

    private static void initPrices() {
        prices = getSamplesFromExchange();
    }

    private static LinkedList<Double> getSamplesFromExchange() {
        return new LinkedList<>(client.getPricesFromExchange(intervalValue));
    }

    private static double notFirstEMA(int period, Double previousEMA, Double currentPrice) {

        int increasedPeriod = period + 1;
        double EMACoefficient = 2.0 / increasedPeriod;
        double lastPricePart = currentPrice * EMACoefficient;
        double lastEMAPart = previousEMA * (1.0 - EMACoefficient);
        return lastPricePart + lastEMAPart;

    }
}
