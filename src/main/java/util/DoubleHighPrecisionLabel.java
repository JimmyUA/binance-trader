package util;


import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import java.util.Locale;

public class DoubleHighPrecisionLabel extends Label {

    private int precision;

    public DoubleHighPrecisionLabel(String id, IModel<?> model, int precision) {
        super(id, model);
        this.precision = precision;
    }

    @Override
    public <C> IConverter<C> getConverter(Class<C> type) {

        return new IConverter<C>() {
            @Override
            public C convertToObject(String s, Locale locale) throws ConversionException {
                return null;
            }

            @Override
            public String convertToString(C c, Locale locale) {
                String format = "%." + precision +"f";
                return String.format(format, c);
            }
        };
    }
}
