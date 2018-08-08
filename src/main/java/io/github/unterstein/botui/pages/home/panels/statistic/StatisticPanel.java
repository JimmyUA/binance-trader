package io.github.unterstein.botui.pages.home.panels.statistic;


import io.github.unterstein.botui.pages.home.panels.LabelValuePanel;
import io.github.unterstein.statistic.StatisticDTO;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import util.DoubleHighPrecisionLabel;

import java.io.Serializable;

public class StatisticPanel extends Panel{

    @SpringBean
    private StatisticDTO statisticDTO;
    private String momoStrategy = "MOMO";
    private String oneStrategy = "ONE";



    public StatisticPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new Label("title", "STATISTIC"));

        LabelValuePanel balance =
                new LabelValuePanel("balance", "Current Trade Balance: ",
                        Model.of(statisticDTO.getTradingBalance()), 2);

        LabelValuePanel currentPrice =
                new LabelValuePanel("currentPrice", "Current Price: ",
                        Model.of(statisticDTO.getLastPrice()), 10);

        LabelValuePanel lastBid =
                new LabelValuePanel("lastBid", "Last Bid: ",
                        Model.of(statisticDTO.getLastBidAverage()), 10);

        LabelValuePanel lastAsk =
                new LabelValuePanel("lastAsk", "Last Ask: ",
                        Model.of(statisticDTO.getLastAskAverage()), 10);


        add(new Label("indicatorsTitle", "Indicators"));

        LabelValuePanel macd =
                new LabelValuePanel("macd", "MACD: ",
                        Model.of(statisticDTO.getMACD()), 10);

        LabelValuePanel signal =
                new LabelValuePanel("signal", "Signal: ",
                        Model.of(statisticDTO.getSignal()), 10);

        LabelValuePanel histo =
                new LabelValuePanel("histo", "Histo: ",
                        Model.of(statisticDTO.getHisto()), 10);
        histo.setStrategies(momoStrategy);

        LabelValuePanel rsi =
                new LabelValuePanel("rsi", "RSI: ",
                        Model.of(statisticDTO.getRSI()), 10);

        add(new Label("trendsTitle", "Trends"));

        LabelValuePanel shortTrend =
                new LabelValuePanel("short", "Short trend: ",
                        Model.of(statisticDTO.getShortTrend()));
        shortTrend.setStrategies(oneStrategy);

        LabelValuePanel longTrend =
                new LabelValuePanel("long", "Long trend: ",
                        Model.of(statisticDTO.getLongTrend()));
        longTrend.setStrategies(oneStrategy);

        LabelValuePanel day =
                new LabelValuePanel("day", "Day trend: ",
                        Model.of(statisticDTO.getDayTrend()));
        day.setStrategies(oneStrategy);

        LabelValuePanel momo =
                new LabelValuePanel("momo", "MoMo trend: ",
                        Model.of(statisticDTO.getMomoTrend()));
        momo.setStrategies(momoStrategy);

        add(balance, currentPrice, lastBid, lastAsk, macd, signal, histo, rsi,
                shortTrend, longTrend, day, momo);

    }


}
