package io.github.unterstein;

import com.giffing.wicket.spring.boot.starter.app.WicketBootStandardWebApplication;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.convert.converter.DoubleConverter;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Component
public class WicketWebApplication extends WicketBootStandardWebApplication {

    @Override
    protected void init() {
        super.init();
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
    }


}
