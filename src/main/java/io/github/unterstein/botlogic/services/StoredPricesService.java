package io.github.unterstein.botlogic.services;

import io.github.unterstein.persistent.entity.StoredPrice;
import io.github.unterstein.persistent.repository.StoredPricesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class StoredPricesService {

    @Autowired
    private StoredPricesRepository storedPricesRepository;


    public void save(Double price) {
        StoredPrice storedPrice = new StoredPrice(price);
        storedPricesRepository.save(storedPrice);
    }

    public LinkedList<Double> getPricesPortion(Long amount) {
        long total = storedPricesRepository.count();
       return storedPricesRepository.getPortion(total - amount);
    }

//    public LinkedList<StoredPrice> getStoredPricesPortion(Long amount) {
//        long total = storedPricesRepository.count();
//        return storedPricesRepository.getFullPricePortion(total - amount);
//    }
}
