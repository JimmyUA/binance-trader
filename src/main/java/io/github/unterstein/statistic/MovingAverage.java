package io.github.unterstein.statistic;

import java.util.ArrayList;
import java.util.List;

public class MovingAverage {

    private List<Double> samples;
    private Integer sampleCount;
    private Integer period;

    public MovingAverage(Integer period) {
        this.period = period;
        sampleCount = 0;
        samples = new ArrayList<>();
    }

    public Double addSample(Double value){
        int position = sampleCount % period;

        if (position >= samples.size()) {
            samples.add(value);
        } else {
            samples.set(position, value);
        }

        return 0.1;
    }
}
