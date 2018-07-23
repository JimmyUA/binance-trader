package io.github.unterstein.botui.pages.amplitude;

import io.github.unterstein.botlogic.services.AmplitudeService;
import io.github.unterstein.botui.pages.base.BasePage;
import io.github.unterstein.persistent.entity.Amplitude;
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
import util.DoubleHighPrecisionLabel;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@MountPath("/amplitudes")
public class AmplitudesPage extends BasePage{

    @SpringBean
    protected AmplitudeService amplitudeService;

    private List<Amplitude> amplitudes;


    public AmplitudesPage(){}


    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new Label("label", "Amplitudes statistic"));
        initAmplitudes();

        DataView<Amplitude> amplitudesView = new DataView<Amplitude>("view", new AmplitudesDataProvider()) {
            @Override
            protected void populateItem(Item<Amplitude> item) {
                Amplitude currentAmplitude = item.getModelObject();
                item.add(new Label("id", currentAmplitude.getId()));
                item.add(new DoubleHighPrecisionLabel("max", new Model<>(currentAmplitude.getMax()), 10));
                item.add(new DoubleHighPrecisionLabel("min", new Model<>(currentAmplitude.getMin()), 10));
                item.add(new DoubleHighPrecisionLabel("maxPercent", new Model<>(currentAmplitude.getMaxPercent()), 2));
                item.add(new DoubleHighPrecisionLabel("minPercent", new Model<>(currentAmplitude.getMinPercent()), 2));
            }

        };


        amplitudesView.setItemsPerPage(10);

        add(amplitudesView);

        double maxMax = amplitudeService.getMaxMax();
        add(new DoubleHighPrecisionLabel("maxMax", new Model<>(maxMax), 10));

        double minMin = amplitudeService.getMinMin();
        add(new DoubleHighPrecisionLabel("minMin", new Model<>(minMin), 10));

        double averageMax = amplitudeService.getMaxAverage();
        add(new DoubleHighPrecisionLabel("averageMax", new Model<>(averageMax), 10));

        double averageMin = amplitudeService.getMinAverage();
        add(new DoubleHighPrecisionLabel("averageMin", new Model<>(averageMin),10));


        Form<Void> form = new Form<Void>("form");

        PagingNavigator pager = new PagingNavigator("pager", amplitudesView);
        form.add(pager);
        add(form);
    }

    private void initAmplitudes() {
        amplitudes = amplitudeService.getAmplitudes();
    }


    class AmplitudesDataProvider implements IDataProvider<Amplitude>{

        @Override
        public Iterator<? extends Amplitude> iterator(long l, long l1) {
            List<Amplitude> selected = amplitudes.stream().skip(l).limit(l1).collect(Collectors.toList());
            return selected.iterator();
        }

        @Override
        public long size() {
            return amplitudes.size();
        }

        @Override
        public IModel<Amplitude> model(Amplitude amplitude) {
            return new Model<>(amplitude);
        }
    }
}
