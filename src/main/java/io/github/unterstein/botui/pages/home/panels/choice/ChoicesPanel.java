package io.github.unterstein.botui.pages.home.panels.choice;


import io.github.unterstein.BinanceTrader;
import io.github.unterstein.TradingClient;
import io.github.unterstein.remoteManagment.ManagementConstants;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import static io.github.unterstein.remoteManagment.ManagementConstants.*;

public class ChoicesPanel extends Panel{



    public ChoicesPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new ChoicePanel("dayDownTrend", "Trades on down day trend forbidden: ", isTradesOnDownDayTrendForbidden));
        add(new ChoicePanel("longMACD", "Long MACD included: ", isLongMACDIncluded));
        add(new ChoicePanel("MACDStopLoss", "Is MACD Stop Loss allowed: ", isMACDStopLossAllowed));
        add(new ChoicePanel("resistanceLine", "Is Resistance line included: ", isResistanceLineIncluded));

    }


}
