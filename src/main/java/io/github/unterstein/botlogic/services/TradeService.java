package io.github.unterstein.botlogic.services;

import io.github.unterstein.persistent.entity.Trade;
import io.github.unterstein.persistent.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    private Trade trade;

    public void addBuyOrder(Double boughtPrice){
        trade = new Trade();
        trade.setBoughtPrice(boughtPrice);
        trade.setBoughtDate(new Date());
    }

    public void addSellOrder(Double sellPrice){
        trade.setSellPrice(sellPrice);
        trade.setSellDate(new Date());
        calculateProfit();
        tradeRepository.save(trade);
        trade = null;
    }

    private void calculateProfit() {
        Double boughtPrice = trade.getBoughtPrice();
        Double sellPrice = trade.getSellPrice();
        double profit = sellPrice - boughtPrice;
        trade.setProfit(profit);
        double profitPercent = profit / boughtPrice * 100;
        trade.setProfitPercent(profitPercent);
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

}
