package io.github.unterstein.botui.pages.home;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import io.github.unterstein.BinanceTrader;
import io.github.unterstein.botui.pages.base.BasePage;
import io.github.unterstein.botui.pages.home.panels.buy.BuyPanel;
import io.github.unterstein.botui.pages.home.panels.choice.ChoicesPanel;
import io.github.unterstein.botui.pages.home.panels.sell.SellPanel;
import io.github.unterstein.botui.pages.home.panels.statistic.StatisticPanel;
import io.github.unterstein.remoteManagment.RemoteManager;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import static io.github.unterstein.remoteManagment.ManagementConstants.*;

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
    private StatisticPanel statisticPanel;
    private Form form;
    private Label isStartedLabel;



    @Override
    protected void onInitialize() {
        super.onInitialize();

        form = new Form("form");
        Label tradeCurrencyLabel = new Label("tradeCurrency", "Trade Currency: " + remoteManager.getTradeCurrency());
        add(tradeCurrencyLabel);
        Button stopBotButton = getStopBotButton();
        Button tradingSwitcherButton = getTradingSwitcherButton();
        form.add(stopBotButton, tradingSwitcherButton);
        add(form);

        Label tradeAmountLabel = new Label("tradeAmountLabel", "Trade Amount");
        form.add(tradeAmountLabel);

        Label versionLabel = new Label("versionLabel", "V 1.5.1");
        add(versionLabel);

        Label strategyLabel = new Label("strategyLabel", "Strategy: " + strategyName);
        add(strategyLabel);

        isStartedLabel = new Label("isStartedLabel",
                String.format("Trading is %s", isStartedTrading ? "started": "stopped"));
        isStartedLabel.setOutputMarkupId(true);
        add(isStartedLabel);


        tradeAmountTF = new NumberTextField<>("tradeAmountTF", Model.of(binanceTrader.getTradeAmount()));
        tradeAmountTF.setOutputMarkupId(true);
        form.add(tradeAmountTF);

        AjaxButton saveButton = getSaveButton();
        form.add(saveButton);



        initPanels();

    }



    private void initPanels() {
        SellPanel sellPanel = new SellPanel("sellPanel");
        BuyPanel buyPanel = new BuyPanel("buyPanel");
        statisticPanel = new StatisticPanel("statisticPanel");
        statisticPanel.setOutputMarkupId(true);
        ChoicesPanel choicesPanel = new ChoicesPanel("choicesPanel");
        add(sellPanel, buyPanel, statisticPanel, choicesPanel);
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

    private Button getTradingSwitcherButton() {
        AjaxButton button = new AjaxButton("tradingSwitcherButton", Model.of(isStartedTrading ? "STOP TRADING" : "START TRADING")) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                isStartedTrading = !isStartedTrading;
                target.add(isStartedLabel);
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                attributes.setPreventDefault(true);
                attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP_IMMEDIATE);
            }

        };

        button.setOutputMarkupId(true);
        return button;
    }

}
