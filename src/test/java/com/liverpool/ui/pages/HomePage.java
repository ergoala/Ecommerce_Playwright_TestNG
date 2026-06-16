package com.liverpool.ui.pages;

import com.liverpool.commons.BasePage;
import com.liverpool.commons.ExtentLogger;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page Object for Liverpool home page.
 * Encapsulates all user interactions and validations on the home page.
 */
public class HomePage extends BasePage {

	/**
	 * Constructor loads the locator definitions from HomePage.json.
	 */
	public HomePage() {
		super("locatorsDefinition/HomePage.json");
	}

	/**
	 * Accepts cookie banner if present on the page.
	 * Uses the base class implementation to find and click the accept button.
	 * @return this HomePage instance for method chaining
	 */
	public HomePage acceptCookiesIfPresent() {
		acceptCookieIfPresent();
		ExtentLogger.pass("Cookie banner accepted if present", false);
		return this;
	}

	/**
	 * Validates that the Liverpool logo is visible on the home page.
	 * Logs pass/fail to ExtentReport with screenshot on failure.
	 * @return this HomePage instance for method chaining
	 */
	public HomePage validateLogoVisible() {
		boolean isVisible = isVisible("Logo");
		if (isVisible) {
			ExtentLogger.pass("Liverpool logo is visible on the home page", true);
		} else {
			ExtentLogger.fail("Liverpool logo is NOT visible on the home page", true);
		}
		return this;
	}

	/**
	 * Searches for a product using the search input field.
	 * Fills the search term, presses Enter, and waits for results page to load.
	 * @param term Search term to enter
	 * @return this HomePage instance for method chaining
	 */
	public HomePage searchProduct(String term) {
		fill("SearchInput", term);
		pressEnter("SearchInput");
		waitForPageLoad();
		ExtentLogger.pass("Searched for product: " + term, true);
		return this;
	}

	/**
	 * Opens the navigation menu (hamburger menu on mobile, categories menu on desktop).
	 * Handles both mobile and desktop layouts by checking which menu is visible.
	 * Uses JavaScript click for hamburger menu to bypass Playwright visibility checks.
	 * @return this HomePage instance for method chaining
	 * @throws RuntimeException if neither menu is found
	 */
	public HomePage openHamburgerMenu() {
		waitForPageLoad();
		try {
			Locator hamburger = getLocatorByKey("HamburgerMenu").first();
			Locator desktopMenu = getLocatorByKey("DesktopCategoriesMenu").first();

			if (desktopMenu.count() > 0 && desktopMenu.first().isVisible()) {
				desktopMenu.first().click();
				ExtentLogger.pass("Desktop categories menu opened", true);
			} else if (hamburger.count() > 0) {
				// Use JavaScript click to bypass visibility checks
				page.evaluate("el => el.click()", hamburger.first().elementHandle());
				ExtentLogger.pass("Hamburger menu opened via JavaScript click", true);
			} else {
				throw new RuntimeException("Neither hamburger nor desktop menu found");
			}
		} catch (Exception e) {
			ExtentLogger.fail("Failed to open menu: " + e.getMessage(), true);
			throw new RuntimeException("Could not open navigation menu", e);
		}
		return this;
	}

	/**
	 * Gets the count of search results displayed on the page.
	 * Counts elements matching the SearchResultsGrid locator.
	 * @return Number of search result items found
	 */
	public int getResultsCount() {
		int count = count("SearchResultsGrid");
		ExtentLogger.info("Number of search results found: " + count);
		return count;
	}

	/**
	 * Validates that the current URL contains the expected fragment.
	 * Useful for verifying navigation to search results or category pages.
	 * Logs pass/fail to ExtentReport with actual URL on failure.
	 * @param fragment Expected URL fragment to verify
	 * @return true if URL contains fragment, false otherwise
	 */
	public boolean isUrlContains(String fragment) {
		boolean contains = urlContains(fragment);
		if (contains) {
			ExtentLogger.pass("URL contains expected fragment: " + fragment, false);
		} else {
			ExtentLogger.fail("URL does not contain fragment: " + fragment + ". Actual URL: " + getUrl(), true);
		}
		return contains;
	}
}
