package io.github.unterstein.botui.pages.home;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import io.github.unterstein.BinanceTrader;
import io.github.unterstein.botui.pages.base.BasePage;
import io.github.unterstein.botui.pages.home.panels.SellPanel;
import io.github.unterstein.remoteManagment.RemoteManager;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("/home")
@WicketHomePage
public class HomePage extends BasePage {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @SpringBean
    private RemoteManager remoteManager;

    @SpringBean
    private BinanceTrader binanceTrader;

    private NumberTextField<Integer> tradeAmountTF;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form form = new Form("form");
        Label tradeCurrencyLabel = new Label("tradeCurrency", "Trade Currency: " + remoteManager.getTradeCurrency());
        add(tradeCurrencyLabel);
        Button stopBotButton = getStopBotButton();
        form.add(stopBotButton);
        add(form);

        Label tradeAmountLabel = new Label("tradeAmountLabel", "Trade Amount");
        form.add(tradeAmountLabel);


        tradeAmountTF = new NumberTextField<>("tradeAmountTF", Model.of(binanceTrader.getTradeAmount()));
        tradeAmountTF.setOutputMarkupId(true);
        form.add(tradeAmountTF);

        AjaxButton saveButton = getSaveButton();

        SellPanel sellPanel = new SellPanel("sellPanel");
        form.add(saveButton);
        add(sellPanel);

    }


    private AjaxButton getSaveButton() {
        return new AjaxButton("saveButton", Model.of("Save")) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                Integer newTradeAmount = tradeAmountTF.getModelObject();
                binanceTrader.setTradeAmount(newTradeAmount);
                target.add(tradeAmountTF);
            }
        };
    }

    private Button getStopBotButton() {
        return new AjaxButton("stopBotButton", Model.of("STOP BOT")) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                remoteManager.stopBot();
                super.onSubmit(target);
            }
        };
    }

}
