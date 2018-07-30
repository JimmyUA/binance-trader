package io.github.unterstein.statistic.lines;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.github.unterstein.TradingClient;
import io.github.unterstein.botlogic.services.StoredPricesService;
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

    public Double getResistanceLineForPeriod(Long periodInMinutes){
        Stream<Double> boxedPrices = getBoxedPricesStream(periodInMinutes);


        return boxedPrices.max(Comparator.naturalOrder()).orElse(0.0);
    }

    public Double getSupportLineForPeriod(Long periodInMinutes){
        Stream<Double> boxedPrices = getBoxedPricesStream(periodInMinutes);
        return boxedPrices.min(Comparator.naturalOrder()).orElse(0.0);
    }

    private Stream<Double> getBoxedPricesStream(Long periodInMinutes) {
        LinkedList<Double> pricesPortion = client.getPricesFromExchangeReversed(CandlestickInterval.ONE_MINUTE)
                .stream().limit(periodInMinutes).collect(Collectors.toCollection(LinkedList::new));
        Stream<Double> stream;
        stream = pricesPortion.stream();

        return stream.mapToDouble(Double::new).boxed();
    }

}
