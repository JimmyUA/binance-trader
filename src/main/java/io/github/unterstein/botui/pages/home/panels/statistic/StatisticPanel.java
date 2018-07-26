package io.github.unterstein.botui.pages.home.panels.statistic;


import io.github.unterstein.statistic.StatisticDTO;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import util.DoubleHighPrecisionLabel;

public class StatisticPanel extends Panel{

    @SpringBean
    private StatisticDTO statisticDTO;


    public StatisticPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new Label("title", "STATISTIC"));

        add(new Label("balance", "Current Trade Balance: "));
        add(new DoubleHighPrecisionLabel("balanceValue", Model.of(statisticDTO.getTradingBalance()), 2));

        add(new Label("currentPrice", "Current Price: "));
        add(new DoubleHighPrecisionLabel("currentPriceValue", Model.of(statisticDTO.getLastPrice()), 10));

        add(new Label("lastBid", "Last Bid: "));
        add(new DoubleHighPrecisionLabel("lastBidValue", Model.of(statisticDTO.getLastBidAverage()), 10));

        add(new Label("lastAsk", "Last Ask: "));
        add(new DoubleHighPrecisionLabel("lastAskValue", Model.of(statisticDTO.getLastAskAverage()), 10));

        add(new Label("indicatorsTitle", "Indicators"));

        add(new Label("macd", "MACD: "));
        add(new DoubleHighPrecisionLabel("macdValue", Model.of(statisticDTO.getMACD()), 10));

        add(new Label("signal", "Signal: "));
        add(new DoubleHighPrecisionLabel("signalValue", Model.of(statisticDTO.getSignal()), 10));

        add(new Label("rsi", "RSI: "));
        add(new DoubleHighPrecisionLabel("rsiValue", Model.of(statisticDTO.getRSI()), 10));

        add(new Label("trendsTitle", "Trends"));

        add(new Label("short", "Short trend: "));
        add(new Label("shortValue", Model.of(statisticDTO.getShortTrend())));

        add(new Label("long", "Long trend: "));
        add(new Label("longValue", Model.of(statisticDTO.getLongTrend())));

        add(new Label("day", "Day trend: "));
        add(new Label("dayValue", Model.of(statisticDTO.getDayTrend())));
    }

}
