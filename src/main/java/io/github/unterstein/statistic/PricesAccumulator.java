package io.github.unterstein.statistic;

import io.github.unterstein.botlogic.services.StoredPricesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class PricesAccumulator {

    @Autowired
    private StoredPricesService storedPricesService;

    public void add(Double price){
        storedPricesService.save(price);
    }

    public LinkedList<Double> getSamples(Long amount) {
        return storedPricesService.getPricesPortion(amount);
    }


    public LinkedList<Double> get100Samples() {
        return storedPricesService.getPricesPortion(100L);
    }

}
