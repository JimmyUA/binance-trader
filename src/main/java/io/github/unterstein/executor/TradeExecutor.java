package io.github.unterstein.executor;

import io.github.unterstein.BinanceTrader;
import io.github.unterstein.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TradeExecutor {

    private static Logger logger = LoggerFactory.getLogger(TradeExecutor.class);


    private Strategy strategy;

    public TradeExecutor(Strategy strategy) {
        this.strategy = strategy;
    }

    public void buyProcess(){
        strategy.buyProcess();
    }

    public void sellProcess(){
        if (strategy.isBought()){
        strategy.sellProcess();
        } else {
            logger.info("Nothing is bought, waiting next tick");
        }
    }

    public void setTradeAmount(int tradeAmount){
        strategy.setTradeAmount(tradeAmount);
    }

}