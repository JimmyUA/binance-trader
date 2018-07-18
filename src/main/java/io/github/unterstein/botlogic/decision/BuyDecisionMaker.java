package io.github.unterstein.botlogic.decision;

public interface BuyDecisionMaker {
    boolean isRightMomentToBuy(Double ask);
}
