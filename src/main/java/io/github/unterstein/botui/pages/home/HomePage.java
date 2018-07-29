package io.github.unterstein.botui.pages.home;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import io.github.unterstein.BinanceTrader;
import io.github.unterstein.botui.pages.base.BasePage;
import io.github.unterstein.botui.pages.home.panels.buy.BuyPanel;
import io.github.unterstein.botui.pages.home.panels.sell.SellPanel;
import io.github.unterstein.botui.pages.home.panels.statistic.StatisticPanel;
import io.github.unterstein.remoteManagment.RemoteManager;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import static io.github.unterstein.remoteManagment.ManagementConstants.isLongMACDIncluded;
import static io.github.unterstein.remoteManagment.ManagementConstants.isMACDStopLossAllowed;
import static io.github.unterstein.remoteManagment.ManagementConstants.isTradesOnDownDayTrendForbidden;

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


    @Override
    protected void onInitialize() {
        super.onInitialize();

        form = new Form("form");
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
        form.add(saveButton);

        addDayDownTrendChoice();
        addLongMACDChoice();
        addMACDStopLossChoice();
        initPanels();

    }

    private void addMACDStopLossChoice() {
        Label MACDStopLossLabel = new Label("MACDStopLossLabel", "Is MACD Stop Loss allowed: ");
        AjaxCheckBox MACDStopLoss = new AjaxCheckBox("MACDStopLoss", Model.of(isMACDStopLossAllowed)) {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                isMACDStopLossAllowed = getModelObject();
                ajaxRequestTarget.add(this);
            }
        };

        form.add(MACDStopLossLabel, MACDStopLoss);
    }

    private void addLongMACDChoice() {
        Label longMACDChoiceLabel = new Label("longMACDChoiceLabel", "Long MACD included: ");
        AjaxCheckBox longMACDChoice = new AjaxCheckBox("longMACDChoice", Model.of(isLongMACDIncluded)) {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                isLongMACDIncluded = getModelObject();
                ajaxRequestTarget.add(this);
            }
        };

        form.add(longMACDChoice, longMACDChoiceLabel);
    }

    private void addDayDownTrendChoice() {
        Label tradesOnDownTrendLabel = new Label("tradesOnDownTrendLabel", "Trades on down day trend forbidden: ");
        AjaxCheckBox tradesOnDownTrend = new AjaxCheckBox("tradesOnDownTrend", Model.of(isTradesOnDownDayTrendForbidden)) {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                isTradesOnDownDayTrendForbidden = getModelObject();
                ajaxRequestTarget.add(this);
            }
        };

        form.add(tradesOnDownTrend, tradesOnDownTrendLabel);
    }

    private void initPanels() {
        SellPanel sellPanel = new SellPanel("sellPanel");
        BuyPanel buyPanel = new BuyPanel("buyPanel");
        statisticPanel = new StatisticPanel("statisticPanel");
        statisticPanel.setOutputMarkupId(true);
        add(sellPanel, buyPanel, statisticPanel);
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
