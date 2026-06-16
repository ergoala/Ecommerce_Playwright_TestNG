package com.liverpool.commons;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

/**
 * Base Page Object class providing common web interaction methods.
 * Loads locators from JSON files and wraps Playwright actions with logging and waits.
 * All page objects should extend this class.
 */
public class BasePage {

	public static Logger log = LogManager.getLogger(BasePage.class);
	public Page page = BaseClass.getPage();

	private String oldJsonPath = "";

	/**
	 * Constructor loads locator definitions from a JSON file.
	 * Only reloads JSON if the path has changed (caching optimization).
	 * @param jsonPath Path to locator JSON file in resources (e.g., "locatorsDefinition/HomePage.json")
	 */
	public BasePage(String jsonPath) {
		if (!oldJsonPath.equals(jsonPath)) {
			BaseClass.loadJson(jsonPath);
			oldJsonPath = jsonPath;
		}
	}

	/**
	 * Retrieves a locator string (xpath or css) from the loaded JSON configuration.
	 * @param locatorKey Key in the JSON (e.g., "SearchInput")
	 * @param locatorType Type of locator: "xpath" or "css"
	 * @return Locator string for the given key and type
	 */
	protected static String getLocator(String locatorKey, String locatorType) {
		Object locatorObject = ((HashMap) ((HashMap) BaseClass.getJsonMap().get(locatorKey))
				.get(BaseClass.platformType)).get(locatorType);
		return locatorObject.toString();
	}

	/**
	 * Gets all available locator types (xpath, css) for a given locator key.
	 * @param locatorKey Key in the JSON configuration
	 * @return Set of locator type strings
	 * @throws RuntimeException if locator key not found
	 */
	protected static Set getLocatorType(String locatorKey) {
		HashMap locatorObject;
		try {
			locatorObject = (HashMap) ((HashMap) BaseClass.getJsonMap().get(locatorKey))
					.get(BaseClass.platformType);
		} catch (NullPointerException e) {
			BaseClass.log.error("Locator object for key '" + locatorKey + "' not found.");
			throw new RuntimeException("Locator object for key '" + locatorKey + "' not found.", e);
		}
		return locatorObject.keySet();
	}

	/**
	 * Gets the XPath locator for a given key.
	 * @param locatorKey Key in the JSON configuration
	 * @return XPath string
	 */
	protected static String getXpath(String locatorKey) {
		return getLocator(locatorKey, "xpath");
	}

	/**
	 * Gets the CSS locator for a given key.
	 * @param locatorKey Key in the JSON configuration
	 * @return CSS selector string
	 */
	protected static String getCss(String locatorKey) {
		return getLocator(locatorKey, "css");
	}

	/**
	 * Gets a Playwright Locator for the given key using XPath.
	 * @param locatorKey Key in the JSON configuration
	 * @return Playwright Locator object
	 */
	public Locator getLocatorByKey(String locatorKey) {
		return page.locator(getXpath(locatorKey));
	}

	/**
	 * Clicks on an element identified by locator key (using XPath).
	 * Waits for page load before clicking.
	 * @param locatorKey Key in the JSON configuration
	 */
	public void click(String locatorKey) {
		waitForPageLoad();
		getLocatorByKey(locatorKey).first().click();
	}

	/**
	 * Clicks on an element identified by locator key using CSS selector.
	 * Waits for page load before clicking.
	 * @param locatorKey Key in the JSON configuration
	 */
	public void clickByCss(String locatorKey) {
		waitForPageLoad();
		page.locator(getCss(locatorKey)).first().click();
	}

	/**
	 * Fills text into an input field identified by locator key.
	 * Clicks the field first to focus, then fills the text.
	 * Waits for page load before interacting.
	 * @param locatorKey Key in the JSON configuration
	 * @param text Text to fill into the field
	 */
	public void fill(String locatorKey, String text) {
		waitForPageLoad();
		Locator el = getLocatorByKey(locatorKey).first();
		el.click();
		el.fill(text);
	}

	/**
	 * Types text character by character into an input field (simulates human typing).
	 * Clicks the field first to focus, then presses keys sequentially with 30ms delay.
	 * Waits for page load before interacting.
	 * @param locatorKey Key in the JSON configuration
	 * @param text Text to type into the field
	 */
	public void type(String locatorKey, String text) {
		waitForPageLoad();
		Locator el = getLocatorByKey(locatorKey).first();
		el.click();
		el.pressSequentially(text, new Locator.PressSequentiallyOptions().setDelay(30));
	}

	/**
	 * Presses the Enter key on an element identified by locator key.
	 * @param locatorKey Key in the JSON configuration
	 */
	public void pressEnter(String locatorKey) {
		getLocatorByKey(locatorKey).first().press("Enter");
	}

	/**
	 * Gets the text content of an element (textContent property).
	 * Waits for page load before retrieving.
	 * @param locatorKey Key in the JSON configuration
	 * @return Text content of the element
	 */
	public String getText(String locatorKey) {
		waitForPageLoad();
		return getLocatorByKey(locatorKey).first().textContent();
	}

	/**
	 * Gets the inner text of an element (innerText property, visible text only).
	 * Waits for page load before retrieving.
	 * @param locatorKey Key in the JSON configuration
	 * @return Inner text of the element
	 */
	public String getInnerText(String locatorKey) {
		waitForPageLoad();
		return getLocatorByKey(locatorKey).first().innerText();
	}

	/**
	 * Checks if an element is visible on the page.
	 * Returns false if element is not found or not visible (no exception thrown).
	 * @param locatorKey Key in the JSON configuration
	 * @return true if element is visible, false otherwise
	 */
	public boolean isVisible(String locatorKey) {
		try {
			return getLocatorByKey(locatorKey).first().isVisible();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Counts the number of elements matching the locator key.
	 * @param locatorKey Key in the JSON configuration
	 * @return Count of matching elements
	 */
	public int count(String locatorKey) {
		return getLocatorByKey(locatorKey).count();
	}

	/**
	 * Selects an option from a dropdown by its value attribute.
	 * @param locatorKey Key in the JSON configuration (should be a select element)
	 * @param value Value attribute of the option to select
	 */
	public void selectByValue(String locatorKey, String value) {
		getLocatorByKey(locatorKey).first().selectOption(new SelectOption().setValue(value));
	}

	/**
	 * Selects an option from a dropdown by its visible text label.
	 * @param locatorKey Key in the JSON configuration (should be a select element)
	 * @param text Visible text of the option to select
	 */
	public void selectByVisibleText(String locatorKey, String text) {
		getLocatorByKey(locatorKey).first().selectOption(new SelectOption().setLabel(text));
	}

	/**
	 * Gets the current page title.
	 * Waits for page load before retrieving.
	 * @return Page title string
	 */
	public String getTitle() {
		waitForPageLoad();
		return page.title();
	}

	/**
	 * Gets the current page URL.
	 * @return Current page URL string
	 */
	public String getUrl() {
		return page.url();
	}

	/**
	 * Checks if the current URL contains a specific fragment (case-insensitive).
	 * @param fragment URL fragment to search for
	 * @return true if URL contains fragment, false otherwise
	 */
	public boolean urlContains(String fragment) {
		return page.url().toLowerCase().contains(fragment.toLowerCase());
	}

	/**
	 * Navigates to a specific URL and waits for page load.
	 * @param url Target URL to navigate to
	 */
	public void navigate(String url) {
		page.navigate(url);
		waitForPageLoad();
	}

	/**
	 * Refreshes the current page and waits for load.
	 */
	public void refresh() {
		page.reload();
		waitForPageLoad();
	}

	/**
	 * Waits for the page to reach load state (network idle).
	 */
	public void waitForPageLoad() {
		page.waitForLoadState();
	}

	/**
	 * Scrolls the page until the element is in view.
	 * @param locatorKey Key in the JSON configuration
	 */
	public void scrollToElement(String locatorKey) {
		getLocatorByKey(locatorKey).first().scrollIntoViewIfNeeded();
	}

	/**
	 * Hovers the mouse over an element identified by locator key.
	 * @param locatorKey Key in the JSON configuration
	 */
	public void mouseHover(String locatorKey) {
		getLocatorByKey(locatorKey).first().hover();
	}

	/**
	 * Accepts cookie banner if present on the page.
	 * Looks for CookieAcceptButton locator, clicks if visible with 3s timeout.
	 * Silently ignores if banner is not present or already accepted.
	 */
	public void acceptCookieIfPresent() {
		try {
			String cookieXpath = getXpath("CookieAcceptButton");
			Locator cookieBtn = page.locator(cookieXpath).first();
			if (cookieBtn.isVisible()) {
				cookieBtn.click(new Locator.ClickOptions().setTimeout(3000));
				BaseClass.log.info("Cookie banner accepted");
			}
		} catch (Exception e) {
			BaseClass.log.info("Cookie banner not present or already accepted");
		}
	}

	/**
	 * Gathers field values from the current page object instance using reflection.
	 * Used for collecting test data from page object fields.
	 * @param columnNames Variable list of field names to gather
	 * @return Map of field names to their string values
	 * @throws RuntimeException if field access fails
	 */
	public Map<String, String> gatherData(String... columnNames) {
		Map<String, String> columnValueMap = new HashMap<>();
		for (String columnName : columnNames) {
			try {
				java.lang.reflect.Field field = this.getClass().getDeclaredField(columnName);
				field.setAccessible(true);
				Object value = field.get(this);
				if (value != null) {
					columnValueMap.put(columnName, value.toString());
				}
			} catch (Exception e) {
				throw new RuntimeException("Error accessing field: " + columnName, e);
			}
		}
		return columnValueMap;
	}
}
