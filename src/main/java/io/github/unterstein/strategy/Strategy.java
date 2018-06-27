package io.github.unterstein.strategy;

public interface Strategy {

    void buyProcess();

    void sellProcess();

    void setTradeAmount(int tradeAmount);

    boolean isBought();
}
