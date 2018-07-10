package io.github.unterstein.botui.pages.home;

import io.github.unterstein.Config;
import io.github.unterstein.TestConfig;
import io.github.unterstein.remoteManagment.ManagementConstants;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class HomePageTest {

    private WicketTester tester;


    @Before
    public void setUp() throws Exception {
        tester = new WicketTester();
        tester.startPage(HomePage.class);
    }

    @Test
    public void shoudRenderPage() {
        tester.assertRenderedPage(HomePage.class);
    }

    @Ignore
    @Test
    public void stopBotButtonShouldChangeShutDownFlag() {
        FormTester formTester = tester.newFormTester("form");
        formTester.submit("stopBotButton");
        assertEquals(true, ManagementConstants.shutDown);
    }
}