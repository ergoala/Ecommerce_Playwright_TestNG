package com.liverpool.ui.pages;

import com.liverpool.commons.BasePage;
import com.liverpool.commons.ExtentLogger;

/**
 * Page Object for Liverpool search results page.
 * Handles validation and interactions on the product listing/search results page.
 */
public class SearchResultsPage extends BasePage {

	/**
	 * Constructor loads the locator definitions from HomePage.json.
	 * Reuses the same locator file as HomePage for search result elements.
	 */
	public SearchResultsPage() {
		super("locatorsDefinition/HomePage.json");
	}

	/**
	 * Validates that search results are displayed on the page.
	 * Waits for page load, counts products in the results grid,
	 * and logs pass/fail to ExtentReport with screenshot on failure.
	 * @return true if at least one search result is found, false otherwise
	 */
	public boolean isResultsDisplayed() {
		waitForPageLoad();
		int count = count("SearchResultsGrid");
		boolean hasResults = count > 0;
		if (hasResults) {
			ExtentLogger.pass("Search results are displayed with " + count + " products", true);
		} else {
			ExtentLogger.fail("No search results found", true);
		}
		return hasResults;
	}

	/**
	 * Clicks on the first product in the search results grid.
	 * Navigates to the product detail page (PDP).
	 * @return this SearchResultsPage instance for method chaining
	 */
	public SearchResultsPage clickOnFirstProduct() {
		getLocatorByKey("SearchResultsGrid").first().click();
		ExtentLogger.pass("Clicked on the first search result", true);
		return this;
	}
}
