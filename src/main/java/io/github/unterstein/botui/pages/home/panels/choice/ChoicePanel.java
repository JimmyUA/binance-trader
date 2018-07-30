package io.github.unterstein.botui.pages.home.panels.choice;


import io.github.unterstein.BinanceTrader;
import io.github.unterstein.TradingClient;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import static io.github.unterstein.remoteManagment.ManagementConstants.isResistanceLineIncluded;

public class ChoicePanel extends Panel{

    private String labelText;
    private Boolean condition;


    public ChoicePanel(String id, String labelText, Boolean condition) {
        super(id);
        this.labelText = labelText;
        this.condition = condition;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form<Void> form = new Form<>("form");
        add(form);
        Label resistanceLineLabel = new Label("label", labelText);
        AjaxCheckBox resistanceLine = new AjaxCheckBox("choice", Model.of(condition)) {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                condition = getModelObject();
                ajaxRequestTarget.add(this);
            }
        };

        form.add(resistanceLineLabel, resistanceLine);
    }

}
