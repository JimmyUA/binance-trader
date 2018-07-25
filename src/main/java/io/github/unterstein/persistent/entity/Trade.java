package io.github.unterstein.persistent.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Entity(name = "trades")
public class Trade implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bought_price")
    private Double boughtPrice;

    @Column(name = "sell_price")
    private Double sellPrice;

    @Column(name = "bought_date")
    private Date boughtDate;

    @Column(name = "sell_date")
    private Date sellDate;

    @Column(name = "profit")
    private Double profit;

    @Column(name = "profit_percent")
    private Double profitPercent;

    @Column(name = "buy_day_trend")
    private String buyDayTrend;

    @Column(name = "sell_day_trend")
    private String sellDayTrend;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBoughtPrice() {
        return boughtPrice;
    }

    public void setBoughtPrice(Double boughtPrice) {
        this.boughtPrice = boughtPrice;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public Double getProfitPercent() {
        return profitPercent;
    }

    public void setProfitPercent(Double profitPercent) {
        this.profitPercent = profitPercent;
    }

    public Date getBoughtDate() {
        return boughtDate;
    }

    public void setBoughtDate(Date boughtDate) {
        this.boughtDate = boughtDate;
    }

    public Date getSellDate() {
        return sellDate;
    }

    public void setSellDate(Date sellDate) {
        this.sellDate = sellDate;
    }

    public String getBuyDayTrend() {
        return buyDayTrend;
    }

    public void setBuyDayTrend(String buyDayTrend) {
        this.buyDayTrend = buyDayTrend;
    }

    public String getSellDayTrend() {
        return sellDayTrend;
    }

    public void setSellDayTrend(String sellDayTrend) {
        this.sellDayTrend = sellDayTrend;
    }
}
