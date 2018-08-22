package io.github.unterstein.botlogic.services;

import io.github.unterstein.persistent.entity.Spread;
import io.github.unterstein.persistent.repository.SpreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SpreadService {

    @Autowired
    private SpreadRepository spreadRepository;

    public List<Double> getLastSpreads(int amount) {
        List<Spread> spreads = spreadRepository.findAll();
        return spreads.stream()
                .mapToDouble(Spread::getSpread).boxed()
                .skip(spreads.size() - amount)
                .collect(Collectors.toList());
    }

    public void addSpread(Spread spread) {
        spreadRepository.save(spread);
    }
}
