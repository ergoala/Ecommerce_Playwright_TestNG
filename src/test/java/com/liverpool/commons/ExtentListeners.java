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

	/**
	 * Returns a singleton instance of ExtentReports. If the instance does not exist,
	 * it initializes it with a Spark Reporter, configures the report's theme, title,
	 * name, and timestamp format, and sets system information.
	 * The report file name includes a timestamp to ensure uniqueness.
	 *
	 * @return The initialized or existing ExtentReports instance.
	 */
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

	/**
	 * Called when the test suite starts. Initializes the ExtentReports instance.
	 * @param context The test context.
	 */
	@Override
	public synchronized void onStart(ITestContext context) {
		getExtentInstance();
	}

	/**
	 * Called when the test suite finishes. Flushes the ExtentReports instance to write the report.
	 * @param context The test context.
	 */
	@Override
	public synchronized void onFinish(ITestContext context) {
		if (extent != null) {
			extent.flush();
		}
	}

	/**
	 * Called when a test method starts. Creates a new test entry in the Extent Report
	 * and assigns categories based on TestNG groups.
	 * @param result The result of the test method.
	 */
	@Override
	public synchronized void onTestStart(ITestResult result) {
		ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName(),
				result.getMethod().getDescription());
		extentTest.assignCategory(result.getMethod().getGroups());
		test.set(extentTest);
	}

	/**
	 * Called when a test method succeeds. Logs a "pass" status to the Extent Report.
	 * @param result The result of the test method.
	 */
	@Override
	public synchronized void onTestSuccess(ITestResult result) {
		test.get().pass("Test passed");
	}

	/**
	 * Called when a test method fails. Logs a "fail" status and the throwable exception to the Extent Report.
	 * @param result The result of the test method.
	 */
	@Override
	public synchronized void onTestFailure(ITestResult result) {
		test.get().fail(result.getThrowable());
	}

	/**
	 * Called when a test method is skipped. Logs a "skip" status and the throwable exception to the Extent Report.
	 * @param result The result of the test method.
	 */
	@Override
	public synchronized void onTestSkipped(ITestResult result) {
		test.get().skip(result.getThrowable());
	}

	/**
	 * Retrieves the ExtentTest instance for the current thread.
	 * @return The ExtentTest instance associated with the current thread.
	 */
	public static ExtentTest getTest() {
		return test.get();
	}
}
