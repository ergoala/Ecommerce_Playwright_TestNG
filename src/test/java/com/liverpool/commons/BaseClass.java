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

	public static Page getPage() {
		return page.get();
	}

	public static HashMap<String, Object> getJsonMap() {
		return jsonMap.get();
	}

	public static ExtentTest getTest() {
		return ExtentListeners.getTest();
	}

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
