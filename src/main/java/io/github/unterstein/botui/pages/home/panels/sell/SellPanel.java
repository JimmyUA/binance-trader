package io.github.unterstein.botui.pages.home.panels.sell;


import io.github.unterstein.BinanceTrader;
import io.github.unterstein.TradingClient;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class SellPanel extends Panel{

    @SpringBean
    private TradingClient tradingClient;

    @SpringBean
    private BinanceTrader binanceTrader;

    private NumberTextField<Integer> amountTF;


    public SellPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form<Void> form = new Form<>("form");
        Label label = new Label("label", String.format("Sell %s coins", tradingClient.getTradeCurrency()));
        amountTF = new NumberTextField<>("amountTF", Model.of(binanceTrader.getTradeAmount()));
        AjaxButton sellButton = getSellButton();

        add(form);
        form.add(label, amountTF, sellButton);
    }

    private AjaxButton getSellButton() {
        return new AjaxButton("sellButton", Model.of("SELL")) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                Integer amountToSell = amountTF.getModelObject();

                tradingClient.sellMarket(amountToSell);

                target.add(SellPanel.this.getPage());
            }
        };
    }
}