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

public class TC_02_Navigation_Menu extends BaseClass {

	static String excelFilePath = ReadProperties.getConfig("DATASHEET_PATH");
	static String masterDataSheetName = ReadProperties.getConfig("LIVERPOOL_MASTERDATA_SHEETNAME");
	static String testDataSheetName = ReadProperties.getConfig("LIVERPOOL_TESTDATA_SHEETNAME");
	static String testCaseName = "TC_04_Navigation_Menu";
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

	@Test(groups = { "navigation", "smoke" }, description = "TC_02_Navigation_Menu - Verify hamburger menu and logo visibility")
	public void verifyNavigationMenu() throws Exception {

		logger.set(BaseClass.log);
		logger.get().info("<b>***** Liverpool Store - Navigation Menu Test (Playwright) *****</b>");

		HeaderFooterPage headerFooterPage = new HeaderFooterPage();
		headerFooterPage.acceptCookie();
		String landingPageTitle = headerFooterPage.getPageTitle();
		ExtentLogger.pass("Landed on Liverpool home page. Title: " + landingPageTitle, true);

		HomePage homePage = new HomePage();
		homePage.validateLogoVisible();

		logger.get().info("<b>***** Opening hamburger menu *****</b>");
		homePage.openHamburgerMenu();

		logger.get().info("<b>***** Validating logo is still visible after menu open *****</b>");
		homePage.validateLogoVisible();

		Utility.validateUIDataField("verifyNavigationMenu", "Logo Visible After Menu Open",
				"true", "true", "UI", true, "Equals");

		Utility.storeExecutionTime(testCaseName);
		ExtentLogger.pass("Navigation menu test completed successfully", true);
	}

	@AfterClass
	public void tearDown() {
		super.tearDown();
	}
}
