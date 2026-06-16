package com.liverpool.commons;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aventstack.extentreports.ExtentTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * Base class for Playwright test framework providing browser management,
 * JSON configuration loading, screenshot capture, and ExtentReports integration.
 * Uses ThreadLocal for thread-safe parallel execution.
 */
public class BaseClass {

	public static Logger log = LogManager.getLogger(BaseClass.class);
	protected static ThreadLocal<Logger> logger = new ThreadLocal<>();
	protected static ThreadLocal<Playwright> playwright = new ThreadLocal<>();
	protected static ThreadLocal<Browser> browser = new ThreadLocal<>();
	protected static ThreadLocal<BrowserContext> context = new ThreadLocal<>();
	protected static ThreadLocal<Page> page = new ThreadLocal<>();
	protected static ThreadLocal<HashMap<String, Object>> jsonMap = new ThreadLocal<>();
	protected static String platformType = "web";
	protected static String browserName;

	static {
		browserName = System.getProperty("browser", "chromium");
	}

	/**
	 * Gets the current thread's Playwright Page instance.
	 * @return Page object for the current thread
	 */
	public static Page getPage() {
		return page.get();
	}

	/**
	 * Gets the JSON configuration map for the current thread.
	 * Contains locators and test data loaded from JSON files.
	 * @return HashMap with JSON configuration data
	 */
	public static HashMap<String, Object> getJsonMap() {
		return jsonMap.get();
	}

	/**
	 * Gets the current ExtentTest instance for reporting.
	 * @return ExtentTest from ExtentListeners ThreadLocal
	 */
	public static ExtentTest getTest() {
		return ExtentListeners.getTest();
	}

	/**
	 * Captures a full-page screenshot and attaches it to the current ExtentReport.
	 * Used for failure evidence in test reports.
	 */
	public static void addScreenCaptureToReport() {
		Page p = page.get();
		if (p != null) {
			try {
				String screenshotPath = captureScreenshot(p);
				getTest().addScreenCaptureFromPath(screenshotPath);
			} catch (Exception e) {
				log.error("Failed to capture screenshot", e);
			}
		}
	}

	/**
	 * Captures a full-page screenshot and saves it to TestReport/screenshots/.
	 * @param p Playwright Page to screenshot
	 * @return File path of the saved screenshot
	 */
	private static String captureScreenshot(Page p) {
		String timeStamp = java.time.LocalDateTime.now()
				.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String screenshotDir = System.getProperty("user.dir") + "/TestReport/screenshots/";
		String screenshotPath = screenshotDir + "screenshot_" + timeStamp + ".png";

		try {
			Files.createDirectories(Paths.get(screenshotDir));
			byte[] screenshotBytes = p.screenshot(
					new Page.ScreenshotOptions().setFullPage(true));
			FileUtils.writeByteArrayToFile(new java.io.File(screenshotPath), screenshotBytes);
		} catch (IOException e) {
			log.error("Failed to save screenshot", e);
		}

		return screenshotPath;
	}

	/**
	 * Initializes Playwright browser, context, and page for the current thread.
	 * Supports chromium, firefox, and webkit browsers. Sets Spanish-Mexico locale
	 * and 1366x768 viewport. Navigates to the provided URL.
	 * @param url Target URL to navigate to
	 */
	public void initDriver(String url) {
		Playwright pw = Playwright.create();
		Browser br;
		switch (browserName.toLowerCase()) {
		case "firefox":
			br = pw.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
			break;
		case "webkit":
			br = pw.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
			break;
		default:
			br = pw.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
			break;
		}

		BrowserContext ctx = br.newContext(
				new Browser.NewContextOptions()
						.setLocale("es-MX")
						.setViewportSize(1366, 768));

		Page pg = ctx.newPage();
		pg.setDefaultTimeout(30000);
		pg.navigate(url);

		playwright.set(pw);
		browser.set(br);
		context.set(ctx);
		page.set(pg);
	}

	/**
	 * Loads a JSON configuration file from resources and flattens it into a HashMap.
	 * Supports nested objects and arrays. Used for locator definitions and test data.
	 * @param jsonPath Path to JSON file in resources (e.g., "locatorsDefinition/HomePage.json")
	 */
	public static void loadJson(String jsonPath) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(
					BaseClass.class.getClassLoader().getResourceAsStream(jsonPath));

			HashMap<String, Object> map = new HashMap<>();
			buildFlatMap(rootNode, map);
			jsonMap.set(map);
		} catch (Exception e) {
			log.error("Failed to load JSON: " + jsonPath, e);
			throw new RuntimeException("Failed to load JSON: " + jsonPath, e);
		}
	}

	/**
	 * Recursively flattens a JSON node into a HashMap.
	 * Handles objects, arrays, and primitive values.
	 * @param node JSON node to process
	 * @param map Target HashMap to populate
	 */
	@SuppressWarnings("unchecked")
	private static void buildFlatMap(JsonNode node, HashMap<String, Object> map) {
		if (node.isObject()) {
			node.fieldNames().forEachRemaining(field -> {
				JsonNode child = node.get(field);
				if (child.isValueNode()) {
					map.put(field, child.asText());
				} else if (child.isArray()) {
					map.put(field, child);
				} else if (child.isObject()) {
					HashMap<String, Object> childMap = new HashMap<>();
					buildFlatMap(child, childMap);
					map.put(field, childMap);
				}
			});
		}
	}

	/**
	 * Tears down Playwright resources for the current thread.
	 * Closes page, context, browser, and Playwright instance in reverse order.
	 * Cleans up ThreadLocal references to prevent memory leaks.
	 */
	public void tearDown() {
		Page pg = page.get();
		if (pg != null)
			pg.close();
		BrowserContext ctx = context.get();
		if (ctx != null)
			ctx.close();
		Browser br = browser.get();
		if (br != null)
			br.close();
		Playwright pw = playwright.get();
		if (pw != null)
			pw.close();

		page.remove();
		context.remove();
		browser.remove();
		playwright.remove();
	}
}
