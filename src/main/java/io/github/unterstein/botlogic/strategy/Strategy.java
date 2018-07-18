package io.github.unterstein.botlogic.strategy;

public interface Strategy {

    void buyProcess();

    void sellProcess();

    void setTradeAmount(int tradeAmount);

    boolean isBought();
}
