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

public class BasePage {

	public static Logger log = LogManager.getLogger(BasePage.class);
	public Page page = BaseClass.getPage();

	private String oldJsonPath = "";

	public BasePage(String jsonPath) {
		if (!oldJsonPath.equals(jsonPath)) {
			BaseClass.loadJson(jsonPath);
			oldJsonPath = jsonPath;
		}
	}

	protected static String getLocator(String locatorKey, String locatorType) {
		Object locatorObject = ((HashMap) ((HashMap) BaseClass.getJsonMap().get(locatorKey))
				.get(BaseClass.platformType)).get(locatorType);
		return locatorObject.toString();
	}

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

	protected static String getXpath(String locatorKey) {
		return getLocator(locatorKey, "xpath");
	}

	protected static String getCss(String locatorKey) {
		return getLocator(locatorKey, "css");
	}

	public Locator getLocatorByKey(String locatorKey) {
		return page.locator(getXpath(locatorKey));
	}

	public void click(String locatorKey) {
		waitForPageLoad();
		getLocatorByKey(locatorKey).first().click();
	}

	public void clickByCss(String locatorKey) {
		waitForPageLoad();
		page.locator(getCss(locatorKey)).first().click();
	}

	public void fill(String locatorKey, String text) {
		waitForPageLoad();
		Locator el = getLocatorByKey(locatorKey).first();
		el.click();
		el.fill(text);
	}

	public void type(String locatorKey, String text) {
		waitForPageLoad();
		Locator el = getLocatorByKey(locatorKey).first();
		el.click();
		el.pressSequentially(text, new Locator.PressSequentiallyOptions().setDelay(30));
	}

	public void pressEnter(String locatorKey) {
		getLocatorByKey(locatorKey).first().press("Enter");
	}

	public String getText(String locatorKey) {
		waitForPageLoad();
		return getLocatorByKey(locatorKey).first().textContent();
	}

	public String getInnerText(String locatorKey) {
		waitForPageLoad();
		return getLocatorByKey(locatorKey).first().innerText();
	}

	public boolean isVisible(String locatorKey) {
		try {
			return getLocatorByKey(locatorKey).first().isVisible();
		} catch (Exception e) {
			return false;
		}
	}

	public int count(String locatorKey) {
		return getLocatorByKey(locatorKey).count();
	}

	public void selectByValue(String locatorKey, String value) {
		getLocatorByKey(locatorKey).first().selectOption(new SelectOption().setValue(value));
	}

	public void selectByVisibleText(String locatorKey, String text) {
		getLocatorByKey(locatorKey).first().selectOption(new SelectOption().setLabel(text));
	}

	public String getTitle() {
		waitForPageLoad();
		return page.title();
	}

	public String getUrl() {
		return page.url();
	}

	public boolean urlContains(String fragment) {
		return page.url().toLowerCase().contains(fragment.toLowerCase());
	}

	public void navigate(String url) {
		page.navigate(url);
		waitForPageLoad();
	}

	public void refresh() {
		page.reload();
		waitForPageLoad();
	}

	public void waitForPageLoad() {
		page.waitForLoadState();
	}

	public void scrollToElement(String locatorKey) {
		getLocatorByKey(locatorKey).first().scrollIntoViewIfNeeded();
	}

	public void mouseHover(String locatorKey) {
		getLocatorByKey(locatorKey).first().hover();
	}

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
