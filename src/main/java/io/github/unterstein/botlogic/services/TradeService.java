package io.github.unterstein.botlogic.services;

import io.github.unterstein.persistent.entity.Trade;
import io.github.unterstein.persistent.repository.TradeRepository;
import io.github.unterstein.statistic.TrendAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        setNotCalculateAbleParameters(sellPrice);
        calculateProfit();
        tradeRepository.save(trade);
        clearData();
    }

    public void addHalfSellOrder(Double sellPrice){
        setNotCalculateAbleParameters(sellPrice);
        calculateHalfProfit();
        tradeRepository.save(trade);
        clearData();
    }

    protected void setNotCalculateAbleParameters(Double sellPrice) {
        trade.setSellPrice(sellPrice);
        trade.setSellDate(new Date());
        trade.setSellDayTrend(trendAnalyzer.isDownDayTrend() ? "DOWN" : "UP");
        fee += sellPrice * 0.0005;
    }

    protected void clearData() {
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

    private void calculateHalfProfit() {
        Double boughtPrice = trade.getBoughtPrice();
        Double sellPrice = trade.getSellPrice();
        double profit = sellPrice - boughtPrice - fee;
        trade.setProfit(profit/2);
        double profitPercent = profit / boughtPrice * 100;
        trade.setProfitPercent(profitPercent/2);
    }

    public void initBuyOrderAfterHalfTrade(){
        trade = new Trade();
        List<Trade> allTrades = tradeRepository.findAll();
        Trade lastTrade = allTrades.get(allTrades.size() - 1);
        Double boughtPrice = lastTrade.getBoughtPrice();
        trade.setBoughtPrice(boughtPrice);
        trade.setBoughtDate(lastTrade.getBoughtDate());
        trade.setBuyDayTrend(trendAnalyzer.isDownDayTrend() ? "DOWN" : "UP");
        fee += boughtPrice * 0.0005;
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

}
