package io.github.unterstein.botlogic.services;

import io.github.unterstein.persistent.entity.Amplitude;
import io.github.unterstein.persistent.repository.AmplitudeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.DoubleStream;

@Service
public class AmplitudeService {

    @Autowired
    private AmplitudeRepository amplitudeRepository;

    private List<Amplitude> amplitudes = Collections.emptyList();


    public void save(Amplitude amplitude) {
        amplitudeRepository.save(amplitude);
    }

    public Double getMaxMax(){
        getAmplitudes();
        return getMaxsStream()
                .max().orElse(0.0);
    }

    private DoubleStream getMaxsStream() {
        getAmplitudes();
        return amplitudes.stream()
                .mapToDouble(Amplitude::getMax);
    }

    public List<Amplitude> getAmplitudes() {
        amplitudes = amplitudeRepository.findAll();
        return amplitudes;
    }

    public Double getMinMin(){
        getAmplitudes();
        return getMinsStream()
                .min().orElse(0.0);
    }

    private DoubleStream getMinsStream() {
        return amplitudes.stream()
                .mapToDouble(Amplitude::getMin);
    }

    public Double getMaxAverage(){
        getAmplitudes();
        return getMaxsStream().average().orElse(0.0);
    }

    public Double getMinAverage() {
        getAmplitudes();
        return getMinsStream().average().orElse(0.0);
    }
}
