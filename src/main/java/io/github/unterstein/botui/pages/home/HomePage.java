package io.github.unterstein.botui.pages.home;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import io.github.unterstein.botui.pages.base.BasePage;
import io.github.unterstein.remoteManagment.RemoteManager;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form form = new Form("form");
        Label tradeCurrencyLabel = new Label("tradeCurrency", "Trade Currency: " + remoteManager.getTradeCurrency());
        add(tradeCurrencyLabel);
        Button stopBotButton = getStopBotButton();
        form.add(stopBotButton);
        add(form);

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
