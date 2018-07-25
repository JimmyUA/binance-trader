package io.github.unterstein.botlogic.services;

import io.github.unterstein.persistent.entity.Trade;
import io.github.unterstein.persistent.repository.TradeRepository;
import io.github.unterstein.statistic.TrendAnalyzer;
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

    @Autowired
    private TrendAnalyzer trendAnalyzer;

    private Trade trade;
    private double fee;

    public void addBuyOrder(Double boughtPrice){
        trade = new Trade();
        trade.setBoughtPrice(boughtPrice);
        trade.setBoughtDate(new Date());
        trade.setBuyDayTrend(trendAnalyzer.isDownDayTrend() ? "DOWN" : "UP");
        fee += boughtPrice * 0.0005;
    }

    public void addSellOrder(Double sellPrice){
        trade.setSellPrice(sellPrice);
        trade.setSellDate(new Date());
        trade.setSellDayTrend(trendAnalyzer.isDownDayTrend() ? "DOWN" : "UP");
        fee += sellPrice * 0.0005;
        calculateProfit();
        tradeRepository.save(trade);
        trade = null;
        fee = 0;
    }

    private void calculateProfit() {
        Double boughtPrice = trade.getBoughtPrice();
        Double sellPrice = trade.getSellPrice();
        double profit = sellPrice - boughtPrice - fee;
        trade.setProfit(profit);
        double profitPercent = profit / boughtPrice * 100;
        trade.setProfitPercent(profitPercent);
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

}
