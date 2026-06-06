package com.liverpool.ui.tests;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.liverpool.commons.BaseClass;
import com.liverpool.commons.ExtentLogger;
import com.liverpool.commons.ReadProperties;
import com.liverpool.commons.Utility;
import com.liverpool.ui.pages.HeaderFooterPage;
import com.liverpool.ui.pages.HomePage;
import com.liverpool.ui.pages.SearchResultsPage;

public class TC_01_Search_Product extends BaseClass {

	static String excelFilePath = ReadProperties.getConfig("DATASHEET_PATH");
	static String masterDataSheetName = ReadProperties.getConfig("LIVERPOOL_MASTERDATA_SHEETNAME");
	static String testDataSheetName = ReadProperties.getConfig("LIVERPOOL_TESTDATA_SHEETNAME");
	static String testCaseName = "TC_01_Search_Product";
	static String environment = System.getenv("ENVIRONMENT");
	static String url = ReadProperties.getConfig("baseUrl");

	@BeforeClass
	public void initTest(ITestContext test) throws Exception {
		if (environment == null || environment.isEmpty()) {
			environment = ReadProperties.getConfig("ENVIRONMENT");
		}

		Utility.testDataSetup(excelFilePath, masterDataSheetName, testDataSheetName, testCaseName, url, environment);
		if (Utility.testdataMap != null && "No".equalsIgnoreCase(Utility.testdataMap.get("Execute"))) {
			throw new org.testng.SkipException("Skipping tests as the execute value is \"No\"");
		}
		initDriver(url);
	}

	@Test(groups = { "search", "smoke" }, description = "TC_01_Search_Product - Search for a product on Liverpool store")
	public void searchProductOnLiverpool() throws Exception {

		String searchWord = "iPhone";
		String urlFragment = "iphone";

		if (Utility.testdataMap != null) {
			searchWord = Utility.testdataMap.get("SearchWord") != null ? Utility.testdataMap.get("SearchWord")
					: searchWord;
			urlFragment = Utility.testdataMap.get("UrlFragment") != null ? Utility.testdataMap.get("UrlFragment")
					: urlFragment;
		}

		logger.set(BaseClass.log);
		logger.get().info("<b>***** Liverpool Store - Product Search Test (Playwright) *****</b>");

		HeaderFooterPage headerFooterPage = new HeaderFooterPage();
		headerFooterPage.acceptCookie();
		String landingPageTitle = headerFooterPage.getPageTitle();
		ExtentLogger.pass("Landed on Liverpool home page. Title: " + landingPageTitle, true);

		HomePage homePage = new HomePage();
		homePage.validateLogoVisible();

		logger.get().info("<b>***** Searching for product: " + searchWord + " *****</b>");
		homePage.searchProduct(searchWord);

		homePage.isUrlContains(urlFragment);

		SearchResultsPage searchResultsPage = new SearchResultsPage();
		searchResultsPage.isResultsDisplayed();

		Utility.validateUIDataField("searchProductOnLiverpool", "Search Results Displayed",
				String.valueOf(searchResultsPage.isResultsDisplayed()), "true", "UI", true, "Equals");

		Utility.storeExecutionTime(testCaseName);
		ExtentLogger.pass("Product search test completed successfully", true);
	}

	@AfterClass
	public void tearDown() {
		super.tearDown();
	}
}
