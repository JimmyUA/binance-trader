package io.github.unterstein.statistic.amplitude;

import io.github.unterstein.botlogic.services.AmplitudeService;
import io.github.unterstein.botlogic.services.StoredPricesService;
import io.github.unterstein.persistent.entity.Amplitude;
import io.github.unterstein.statistic.lines.LinesAnalyser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class AmplitudeAnalyser {

    @Autowired
    private AmplitudeService amplitudeService;

    @Autowired
    private LinesAnalyser linesAnalyser;

    private static boolean isStarted = false;
    public static long counter;


    public void start(){
        isStarted = true;
    }

    public void stop(){
        isStarted = false;
        Double max = linesAnalyser.getResistenceLineForPeriod(counter);
        Double min = linesAnalyser.getSupportLineForPeriod(counter);
        Amplitude amplitude = new Amplitude();
        amplitude.setMax(max);
        amplitude.setMin(min);
        amplitudeService.save(amplitude);
        counter = 0;
    }

    public void notifyAddingPrice(){
        if(isStarted) {
            counter++;
        }
    }

}
