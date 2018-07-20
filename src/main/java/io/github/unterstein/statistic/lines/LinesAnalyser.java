package io.github.unterstein.statistic.lines;

import io.github.unterstein.botlogic.services.StoredPricesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Stream;

@Component
public class LinesAnalyser {

    @Autowired
    private StoredPricesService storedPricesService;

    public Double getResistenceLineForPeriod(Long period){
        LinkedList<Double> pricesPortion = storedPricesService.getPricesPortion(period);
        Stream<Double> stream;
        stream = pricesPortion.stream();

        return stream.mapToDouble(Double::new).boxed().max(Comparator.naturalOrder()).get();
    }

    public Double getSupportLineForPeriod(Long period){
        LinkedList<Double> pricesPortion = storedPricesService.getPricesPortion(period);
        Stream<Double> stream;
        stream = pricesPortion.stream();

        return stream.mapToDouble(Double::new).boxed().min(Comparator.naturalOrder()).get();
    }
}
