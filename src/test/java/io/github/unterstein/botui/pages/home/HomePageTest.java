package io.github.unterstein.botui.pages.home;

import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.TestConfig;
import io.github.unterstein.remoteManagment.ManagementConstants;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@ActiveProfiles("test")
public class HomePageTest {



    @Autowired
    private WicketTester tester;


    @Before
    public void setUp() throws Exception {
        tester.startPage(HomePage.class);
    }

    @Test
    public void shouldRenderPage() {
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