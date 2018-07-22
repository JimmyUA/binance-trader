package io.github.unterstein.botlogic.services;

import io.github.unterstein.persistent.entity.Amplitude;
import io.github.unterstein.persistent.repository.AmplitudeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.DoubleStream;

@Service
public class AmplitudeService {

    @Autowired
    private AmplitudeRepository amplitudeRepository;

    private List<Amplitude> amplitudes;


    public void save(Amplitude amplitude) {
        amplitudeRepository.save(amplitude);
    }

    public Double getMaxMax(){
        return getMaxsStream()
                .max().getAsDouble();
    }

    private DoubleStream getMaxsStream() {
        return amplitudes.stream()
                .mapToDouble(Amplitude::getMax);
    }

    private List<Amplitude> getAmplitudes() {
        return amplitudeRepository.findAll();
    }

    public Double getMinMin(){
        return getMinsStream()
                .min().getAsDouble();
    }

    private DoubleStream getMinsStream() {
        return amplitudes.stream()
                .mapToDouble(Amplitude::getMin);
    }

    public Double getMaxAvarege(){
        return getMaxsStream().average().getAsDouble();
    }

    public Double getMinAvarege(){
        return getMinsStream().average().getAsDouble();
    }
}
