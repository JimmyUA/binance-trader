package io.github.unterstein.statistic;

import io.github.unterstein.botlogic.services.StoredPricesService;
import io.github.unterstein.statistic.amplitude.AmplitudeAnalyser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class PricesAccumulator {

    @Autowired
    private StoredPricesService storedPricesService;

    @Autowired
    private AmplitudeAnalyser amplitudeAnalyser;

    public void add(Double price){
        storedPricesService.save(price);
        amplitudeAnalyser.notifyAddingPrice();
    }

    public LinkedList<Double> getSamples(Long amount) {
        return storedPricesService.getPricesPortion(amount);
    }


    public LinkedList<Double> get100Samples() {
        return storedPricesService.getPricesPortion(100L);
    }

}
