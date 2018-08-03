package io.github.unterstein.botlogic.strategy;

import io.github.unterstein.botlogic.decision.momo.BuyDecisionMakerMoMo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoMoStrategy extends AbstractStrategy{

    @Autowired
    public MoMoStrategy(BuyDecisionMakerMoMo buyDecisionMaker) {
        this.buyDecisionMaker = buyDecisionMaker;
    }

    @Override
    public void buyProcess() {
        super.buyProcess();
    }

    @Override
    public void sellProcess() {

    }

    @Override
    public void setTradeAmount(int tradeAmount) {

    }

    @Override
    public boolean isBought() {
        return false;
    }
}
