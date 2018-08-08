package io.github.unterstein.statistic;


import org.springframework.stereotype.Component;

@Component
public class StatisticDTO {

    private Double lastBidAverage;
    private Double lastAskAverage;
    private Double lastPrice;
    private Double tradingBalance;
    private Double RSI;
    private Double MACD;
    private Double signal;
    private Double histo;
    private String dayTrend;
    private String shortTrend;
    private String longTrend;
    private String momoTrend;

    public Double getLastBidAverage() {
        return lastBidAverage;
    }

    public StatisticDTO setLastBidAverage(Double lastBidAverage) {
        this.lastBidAverage = lastBidAverage;
        return this;
    }

    public Double getLastAskAverage() {
        return lastAskAverage;
    }

    public StatisticDTO setLastAskAverage(Double lastAskAverage) {
        this.lastAskAverage = lastAskAverage;
        return this;
    }

    public Double getLastPrice() {
        return lastPrice;
    }

    public StatisticDTO setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
        return this;
    }

    public Double getTradingBalance() {
        return tradingBalance;
    }

    public StatisticDTO setTradingBalance(Double tradingBalance) {
        this.tradingBalance = tradingBalance;
        return this;
    }

    public Double getRSI() {
        return RSI;
    }

    public StatisticDTO setRSI(Double RSI) {
        this.RSI = RSI;
        return this;
    }

    public Double getMACD() {
        return MACD;
    }

    public StatisticDTO setMACD(Double MACD) {
        this.MACD = MACD;
        return this;
    }

    public Double getSignal() {
        return signal;
    }

    public StatisticDTO setSignal(Double signal) {
        this.signal = signal;
        return this;
    }

    public String getDayTrend() {
        return dayTrend;
    }

    public StatisticDTO setDayTrend(String dayTrend) {
        this.dayTrend = dayTrend;
        return this;
    }

    public String getShortTrend() {
        return shortTrend;
    }

    public StatisticDTO setShortTrend(String shortTrend) {
        this.shortTrend = shortTrend;
        return this;
    }

    public String getLongTrend() {
        return longTrend;
    }

    public StatisticDTO setLongTrend(String longTrend) {
        this.longTrend = longTrend;
        return this;
    }

    public Double getHisto() {
        return histo;
    }

    public StatisticDTO setHisto(Double histo) {
        this.histo = histo;
        return this;
    }

    public String getMomoTrend() {
        return momoTrend;
    }

    public StatisticDTO setMomoTrend(String momoTrend) {
        this.momoTrend = momoTrend;
        return this;
    }
}
