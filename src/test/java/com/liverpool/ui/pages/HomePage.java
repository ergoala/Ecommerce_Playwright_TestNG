package com.liverpool.ui.pages;

import com.liverpool.commons.BasePage;
import com.liverpool.commons.ExtentLogger;

public class HomePage extends BasePage {

	public HomePage() {
		super("locatorsDefinition/HomePage.json");
	}

	public HomePage acceptCookiesIfPresent() {
		acceptCookieIfPresent();
		ExtentLogger.pass("Cookie banner accepted if present", false);
		return this;
	}

	public HomePage validateLogoVisible() {
		boolean isVisible = isVisible("Logo");
		if (isVisible) {
			ExtentLogger.pass("Liverpool logo is visible on the home page", true);
		} else {
			ExtentLogger.fail("Liverpool logo is NOT visible on the home page", true);
		}
		return this;
	}

	public HomePage searchProduct(String term) {
		fill("SearchInput", term);
		pressEnter("SearchInput");
		waitForPageLoad();
		ExtentLogger.pass("Searched for product: " + term, true);
		return this;
	}

	public HomePage openHamburgerMenu() {
		if (isVisible("HamburgerMenu")) {
			click("HamburgerMenu");
			ExtentLogger.pass("Hamburger menu opened successfully", true);
		} else {
			click("DesktopCategoriesMenu");
			ExtentLogger.pass("Desktop categories menu opened", true);
		}
		return this;
	}

	public int getResultsCount() {
		int count = count("SearchResultsGrid");
		ExtentLogger.info("Number of search results found: " + count);
		return count;
	}

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
