package io.github.unterstein.statistic.spread;

import io.github.unterstein.botlogic.services.SpreadService;
import io.github.unterstein.persistent.entity.Spread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpreadTracker {

    @Autowired
    private SpreadService spreadService;

    public double getAverageForPeriod(int period) {
        List<Double> spreads = spreadService.getLastSpreads(period);
        return spreads.stream().mapToDouble(d -> d).average().orElse(0.0);
    }

    public void addSpread(Spread spread) {
        spreadService.addSpread(spread);
    }


}
