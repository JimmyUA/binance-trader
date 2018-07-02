package io.github.unterstein.decision;

public interface BuyDecisionMaker {
    boolean isRightMomentToBuy(Double ask);
}
