package io.github.unterstein.statistic;

import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class PricesAccumulator {


    private LinkedList<Double> samples;
    private int samplesCapacity;

    public PricesAccumulator() {
        samples = new LinkedList<>();
        samplesCapacity = 100;
        for (int i = 0; i < samplesCapacity; i++) {
            samples.add(1.0);
        }
    }

    public void add(Double price){

        if (samples.size() < samplesCapacity){
            samples.add(price);
        } else{
            samples.pollFirst();
            samples.addLast(price);
        }
    }

    public LinkedList<Double> getSamples() {
        return samples;
    }
}
