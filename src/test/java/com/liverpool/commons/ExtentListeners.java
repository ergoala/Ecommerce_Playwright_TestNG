package com.liverpool.commons;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentListeners implements ITestListener {

	private static ExtentReports extent;
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

	private static ExtentReports getExtentInstance() {
		if (extent == null) {
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			String reportName = "Test-Report-" + timeStamp + ".html";
			String reportPath = System.getProperty("user.dir") + "/TestReport/" + reportName;

			ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
			sparkReporter.config().setTheme(Theme.DARK);
			sparkReporter.config().setDocumentTitle("Liverpool Store Automation Report - Playwright");
			sparkReporter.config().setReportName("Liverpool Store - Playwright + TestNG");
			sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");

			extent = new ExtentReports();
			extent.attachReporter(sparkReporter);
			extent.setSystemInfo("Application", "Liverpool Store");
			extent.setSystemInfo("Framework", "Playwright + TestNG");
			extent.setSystemInfo("OS", System.getProperty("os.name"));
			extent.setSystemInfo("User Name", System.getProperty("user.name"));
			extent.setSystemInfo("Environment", System.getProperty("env", "QA"));
			extent.setSystemInfo("Java Version", System.getProperty("java.version"));
		}
		return extent;
	}

	@Override
	public synchronized void onStart(ITestContext context) {
		getExtentInstance();
	}

	@Override
	public synchronized void onFinish(ITestContext context) {
		if (extent != null) {
			extent.flush();
		}
	}

	@Override
	public synchronized void onTestStart(ITestResult result) {
		ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName(),
				result.getMethod().getDescription());
		extentTest.assignCategory(result.getMethod().getGroups());
		test.set(extentTest);
	}

	@Override
	public synchronized void onTestSuccess(ITestResult result) {
		test.get().pass("Test passed");
	}

	@Override
	public synchronized void onTestFailure(ITestResult result) {
		test.get().fail(result.getThrowable());
	}

	@Override
	public synchronized void onTestSkipped(ITestResult result) {
		test.get().skip(result.getThrowable());
	}

	public static ExtentTest getTest() {
		return test.get();
	}
}
