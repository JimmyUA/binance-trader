package io.github.unterstein.statistic.MACD;

import io.github.unterstein.BinanceTrader;
import io.github.unterstein.statistic.PricesAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MACD {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);

    @Autowired
    private PricesAccumulator pricesAccumulator;


}
