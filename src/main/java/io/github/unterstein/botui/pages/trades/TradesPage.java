package io.github.unterstein.botui.pages.trades;

import io.github.unterstein.botlogic.services.TradeService;
import io.github.unterstein.botui.pages.base.BasePage;
import io.github.unterstein.persistent.entity.Trade;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@MountPath("/trades")
public class TradesPage extends BasePage{

    @SpringBean
    protected TradeService tradeService;

    public TradesPage(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    public TradesPage(){}

    private List<Trade> trades;


    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new Label("label", "Trades statistic"));

        trades = initTrades();

        DataView<Trade> tradesView = new DataView<Trade>("view", new TradesDataProvider()) {
            @Override
            protected void populateItem(Item<Trade> item) {
                Trade currentTrade = item.getModelObject();
                item.add(new Label("id", currentTrade.getId()));
                item.add(new Label("buy", currentTrade.getBoughtPrice()));
                item.add(new Label("buyDate", currentTrade.getBoughtDate()));
                item.add(new Label("sell", currentTrade.getSellPrice()));
                item.add(new Label("sellDate", currentTrade.getSellDate()));
                item.add(new Label("profit", currentTrade.getProfit()));
                item.add(new Label("profitPercent", currentTrade.getProfitPercent()));
            }

        };


        tradesView.setItemsPerPage(10);

        add(tradesView);

        double totalProfit = trades.stream().mapToDouble(Trade::getProfit).reduce((p1, p2) -> p1 + p2).orElse(0.0);
        add(new Label("totalProfit", new Model<>(totalProfit)));

        double totalProfitPercent = trades.stream().mapToDouble(Trade::getProfitPercent).reduce((p1, p2) -> p1 + p2).orElse(0.0);
        add(new Label("totalProfitPercent", new Model<>(totalProfitPercent)));

        Label noTrades = new Label("noTrades", "No Trades were executed!");
        if (trades.size() > 0){
            noTrades.setVisible(false);
        }
        add(noTrades);

        Form<Void> form = new Form<Void>("form");

        PagingNavigator pager = new PagingNavigator("pager", tradesView);
        form.add(pager);
        add(form);
    }

    protected List<Trade> initTrades() {
        return tradeService.getAllTrades();
    }

    class TradesDataProvider implements IDataProvider<Trade>{

        @Override
        public Iterator<? extends Trade> iterator(long l, long l1) {
            List<Trade> selected = trades.stream().skip(l).limit(l1).collect(Collectors.toList());
            return selected.iterator();
        }

        @Override
        public long size() {
            return trades.size();
        }

        @Override
        public IModel<Trade> model(Trade trade) {
            return new Model<>(trade);
        }
    }
}
