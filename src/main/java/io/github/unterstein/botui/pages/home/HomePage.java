package io.github.unterstein.botui.pages.home;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import io.github.unterstein.botui.pages.base.BasePage;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("/home")
@WicketHomePage
public class HomePage extends BasePage {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

}
