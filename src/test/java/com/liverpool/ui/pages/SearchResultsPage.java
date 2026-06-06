package com.liverpool.ui.pages;

import com.liverpool.commons.BasePage;
import com.liverpool.commons.ExtentLogger;

public class SearchResultsPage extends BasePage {

	public SearchResultsPage() {
		super("locatorsDefinition/HomePage.json");
	}

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

	public SearchResultsPage clickOnFirstProduct() {
		getLocatorByKey("SearchResultsGrid").first().click();
		ExtentLogger.pass("Clicked on the first search result", true);
		return this;
	}
}
