package com.liverpool.ui.pages;

import com.liverpool.commons.BasePage;
import com.liverpool.commons.ExtentLogger;

public class HeaderFooterPage extends BasePage {

	public HeaderFooterPage() {
		super("locatorsDefinition/HomePage.json");
	}

	public HeaderFooterPage acceptCookie() {
		acceptCookieIfPresent();
		return this;
	}

	public boolean isLogoVisible() {
		boolean visible = isVisible("Logo");
		if (visible) {
			ExtentLogger.pass("Site logo is visible in the header", false);
		} else {
			ExtentLogger.fail("Site logo is NOT visible in the header", true);
		}
		return visible;
	}

	public String getPageTitle() {
		String title = getTitle();
		ExtentLogger.info("Page title from header: " + title);
		return title;
	}
}
