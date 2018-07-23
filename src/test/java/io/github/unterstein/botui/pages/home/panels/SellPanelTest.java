package io.github.unterstein.botui.pages.home.panels;

import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.TestConfig;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@ActiveProfiles("test")
public class SellPanelTest {

    @Autowired
    private WicketTester tester;

    @Test
    public void shouldRender() throws Exception {
        tester.startComponentInPage(new SellPanel("panel"));
        tester.assertNoErrorMessage();
    }
}