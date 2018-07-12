package io.github.unterstein.decision.macd;

import io.github.unterstein.statistic.MACD.MACD;
import org.springframework.beans.factory.annotation.Autowired;

public class SellDecisionMakerMACD {

    @Autowired
    private MACD macd;

    public boolean isMACDAscending() {
        return macd.isAscending();
    }
}
