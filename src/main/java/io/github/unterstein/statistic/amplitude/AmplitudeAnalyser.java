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
    private Double startPrice;
    public static long counter;


    public void start(Double startPrice){
        isStarted = true;
        this.startPrice = startPrice;
    }

    public void stop(){
        isStarted = false;
        Double max = linesAnalyser.getResistenceLineForPeriod(counter) - startPrice;
        Double min = linesAnalyser.getSupportLineForPeriod(counter) - startPrice;
        Double maxPercent = (max/startPrice) * 100;
        Double minPercent = (min/startPrice) * 100;
        Amplitude amplitude = new Amplitude();
        amplitude.setMax(max);
        amplitude.setMin(min);
        amplitude.setMaxPercent(maxPercent);
        amplitude.setMinPercent(minPercent);
        amplitudeService.save(amplitude);
        counter = 0;
    }

    public void notifyAddingPrice(){
        if(isStarted) {
            counter++;
        }
    }

}
