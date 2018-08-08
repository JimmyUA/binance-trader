package io.github.unterstein.statistic.lines;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.TradingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LinesAnalyser {

    @Autowired
    private TradingClient client;

    protected void setClient(TradingClient client) {
        this.client = client;
    }

    public Double getResistanceLineForPeriod(Long period, CandlestickInterval interval){
        Stream<Double> boxedPrices = getBoxedPricesStream(period, interval);


        return boxedPrices.max(Comparator.naturalOrder()).orElse(0.0);
    }

    public Double getSupportLineForPeriod(Long period, CandlestickInterval interval){
        Stream<Double> boxedPrices = getBoxedPricesStream(period, interval);
        return boxedPrices.min(Comparator.naturalOrder()).orElse(0.0);
    }

    private Stream<Double> getBoxedPricesStream(Long period, CandlestickInterval interval) {
        LinkedList<Double> pricesPortion = client.getPricesFromExchangeReversed(interval)
                .stream().skip(30).limit(period).collect(Collectors.toCollection(LinkedList::new));
        Stream<Double> stream;
        stream = pricesPortion.stream();

        return stream.mapToDouble(Double::new).boxed();
    }

}
