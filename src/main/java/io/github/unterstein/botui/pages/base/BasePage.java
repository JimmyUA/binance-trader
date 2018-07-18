package io.github.unterstein.botui.pages.base;


import io.github.unterstein.botui.pages.trades.TradesPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;

public class BasePage extends WebPage {

	/**
	 * 
	 */

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new Link<Void>("trades") {
			@Override
			public void onClick() {
				BasePage.this.setResponsePage(TradesPage.class);
			}
		});
	}



}
