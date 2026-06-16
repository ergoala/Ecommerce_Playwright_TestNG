package com.liverpool.ui.pages;

import com.liverpool.commons.BasePage;
import com.liverpool.commons.ExtentLogger;

/**
 * Page Object for Liverpool header and footer components.
 * Handles common header/footer interactions like cookie acceptance,
 * logo validation, and page title retrieval.
 */
public class HeaderFooterPage extends BasePage {

	/**
	 * Constructor loads the locator definitions from HomePage.json.
	 * Uses shared locators for header/footer elements.
	 */
	public HeaderFooterPage() {
		super("locatorsDefinition/HomePage.json");
	}

	/**
	 * Accepts the cookie banner if present on the page.
	 * Delegates to base class implementation for cookie handling.
	 * @return this HeaderFooterPage instance for method chaining
	 */
	public HeaderFooterPage acceptCookie() {
		acceptCookieIfPresent();
		return this;
	}

	/**
	 * Validates that the site logo is visible in the header.
	 * Logs pass/fail to ExtentReport with screenshot on failure.
	 * @return true if logo is visible, false otherwise
	 */
	public boolean isLogoVisible() {
		boolean visible = isVisible("Logo");
		if (visible) {
			ExtentLogger.pass("Site logo is visible in the header", false);
		} else {
			ExtentLogger.fail("Site logo is NOT visible in the header", true);
		}
		return visible;
	}

	/**
	 * Retrieves the current page title from the browser.
	 * Logs the title to ExtentReport for traceability.
	 * @return Current page title as String
	 */
	public String getPageTitle() {
		String title = getTitle();
		ExtentLogger.info("Page title from header: " + title);
		return title;
	}
}
