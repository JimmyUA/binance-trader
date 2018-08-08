package io.github.unterstein.botui.pages.home.panels;

import io.github.unterstein.remoteManagment.ManagementConstants;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import util.DoubleHighPrecisionLabel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LabelValuePanel extends Panel{


    private int precision;
    private String text;
    private Model model;
    private String id;
    private List<String> strategies;


    public LabelValuePanel(String id, String text, Model model) {
        super(id);
        this.id = id;
        this.text = text;
        this.model = model;
        this.precision = 0;
        initList();
    }

    public LabelValuePanel(String id, String text, Model model, int precision) {
        super(id);
        this.id = id;
        this.text = text;
        this.model = model;
        this.precision = precision;
        initList();
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("label", text));
        Label value;
        if(precision == 0){
            value = new Label("value", model);
        } else {
            value = new DoubleHighPrecisionLabel("value", model, precision);
        }
        add(value);
    }

    @Override
    public boolean isVisible() {
        return strategies.stream()
                .filter(s -> s.equals(ManagementConstants.strategyName))
                .collect(Collectors.toList()).size() > 0;
    }

    public void setStrategies(String ... strategy){
        strategies = new ArrayList();
        Collections.addAll(strategies, strategy);
    }

    private void initList() {
        strategies = new ArrayList<>();
        Collections.addAll(strategies, "MOMO","ONE");
    }
}
