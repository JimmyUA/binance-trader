package io.github.unterstein.botui.pages.home.panels.choice;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import static io.github.unterstein.remoteManagment.ManagementConstants.*;

public class ChoicesPanel extends Panel {


    public ChoicesPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form<Void> form = new Form<>("form");
        add(form);

        Label resistanceLineLabel = new Label("resistanceLineLabel", "Is Resistance line included: ");
        AjaxCheckBox resistanceLine = getResistanceLineChoice();

        Label dayDownTrendLabel = new Label("dayDownTrendLabel", "Trades on down day trend forbidden: ");
        AjaxCheckBox dayDownTrend = getDayDownTrendLabelChoice();

        Label longMACDLabel = new Label("longMACDLabel", "Long MACD included: ");
        AjaxCheckBox longMACD = getLongMACDChoice();

        Label MACDStopLossLabel = new Label("MACDStopLossLabel", "Is MACD Stop Loss allowed: ");
        AjaxCheckBox MACDStopLoss = getMACDStopLossChoice();

        Label negativeMACDLabel = new Label("negativeMACDLabel", "Is negative MACD required: ");
        AjaxCheckBox negativeMACD = getnegativeMACDChoice();

        form.add(resistanceLine, resistanceLineLabel, dayDownTrend, dayDownTrendLabel,
                longMACD, longMACDLabel, MACDStopLoss, MACDStopLossLabel,
                negativeMACD, negativeMACDLabel);

    }

    private AjaxCheckBox getnegativeMACDChoice() {
        return new AjaxCheckBox("negativeMACD", Model.of(isNegativeMACDRequired)) {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                isNegativeMACDRequired = getModelObject();
                ajaxRequestTarget.add(this);
            }
        };
    }

    private AjaxCheckBox getMACDStopLossChoice() {
        return new AjaxCheckBox("MACDStopLoss", Model.of(isMACDStopLossAllowed)) {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                isMACDStopLossAllowed = getModelObject();
                ajaxRequestTarget.add(this);
            }
        };
    }

    private AjaxCheckBox getLongMACDChoice() {
        return new AjaxCheckBox("longMACD", Model.of(isLongMACDIncluded)) {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                isLongMACDIncluded = getModelObject();
                ajaxRequestTarget.add(this);
            }
        };
    }

    private AjaxCheckBox getDayDownTrendLabelChoice() {
        return new AjaxCheckBox("dayDownTrend", Model.of(isTradesOnDownDayTrendForbidden)) {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                isTradesOnDownDayTrendForbidden = getModelObject();
                ajaxRequestTarget.add(this);
            }
        };
    }

    private AjaxCheckBox getResistanceLineChoice() {
        return new AjaxCheckBox("resistanceLine", Model.of(isResistanceLineIncluded)) {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                isResistanceLineIncluded = getModelObject();
                ajaxRequestTarget.add(this);
            }
        };
    }


}
