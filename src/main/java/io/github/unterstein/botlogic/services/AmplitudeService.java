package io.github.unterstein.botlogic.services;

import io.github.unterstein.persistent.entity.Amplitude;
import io.github.unterstein.persistent.repository.AmplitudeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
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

    public List<Amplitude> getAmplitudes() {
        amplitudes = amplitudeRepository.findAll();
        return amplitudes;
    }

    public Double getMaxMax(){
        getAmplitudes();
        return getMaxsStatistics().getMax();
    }

    public Double getMaxAverage(){
        getAmplitudes();
        getMaxsStatistics();
        return getMaxsStatistics().getAverage();
    }

    private DoubleSummaryStatistics getMaxsStatistics() {
        return getMaxsStream().summaryStatistics();
    }

    private DoubleStream getMaxsStream() {
        getAmplitudes();
        return amplitudes.stream()
                .mapToDouble(Amplitude::getMaxPercent);
    }

    public Double getMinMin(){
        getAmplitudes();
        return getMinsStatistics().getMin();
    }

    public Double getMinAverage() {
        getAmplitudes();
        return getMinsStatistics().getAverage();
    }

    private DoubleStream getMinsStream() {
        return amplitudes.stream()
                .mapToDouble(Amplitude::getMinPercent);
    }

    private DoubleSummaryStatistics getMinsStatistics() {
        return getMinsStream().summaryStatistics();
    }

}
