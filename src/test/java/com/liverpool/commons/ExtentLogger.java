package com.liverpool.commons;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class ExtentLogger {

	private ExtentLogger() {
	}

	/**
	 * Logs a passing step to the Extent Report with a green label.
	 * @param message The message to be logged.
	 */
	public static void pass(String message) {
		BaseClass.getTest().log(Status.PASS, MarkupHelper.createLabel(message, ExtentColor.GREEN));
	}

	/**
	 * Logs a passing step to the Extent Report with a green label and optionally adds a screenshot.
	 * @param message The message to be logged.
	 * @param screenshot True to add a screenshot to the report, false otherwise.
	 */
	public static void pass(String message, boolean screenshot) {
		BaseClass.getTest().log(Status.PASS, MarkupHelper.createLabel(message, ExtentColor.GREEN));
		if (screenshot) {
			BaseClass.addScreenCaptureToReport();
		}
	}

	/**
	 * Logs a failing step to the Extent Report with a red label.
	 * @param message The message to be logged.
	 */
	public static void fail(String message) {
		BaseClass.getTest().log(Status.FAIL, MarkupHelper.createLabel(message, ExtentColor.RED));
	}

	/**
	 * Logs a failing step to the Extent Report with a red label and optionally adds a screenshot.
	 * @param message The message to be logged.
	 * @param screenshot True to add a screenshot to the report, false otherwise.
	 */
	public static void fail(String message, boolean screenshot) {
		BaseClass.getTest().log(Status.FAIL, MarkupHelper.createLabel(message, ExtentColor.RED));
		if (screenshot) {
			BaseClass.addScreenCaptureToReport();
		}
	}

	/**
	 * Logs an informational step to the Extent Report.
	 * @param message The message to be logged.
	 */
	public static void info(String message) {
		BaseClass.getTest().log(Status.INFO, message);
	}

	/**
	 * Logs a skipped step to the Extent Report with an orange label.
	 * @param message The message to be logged.
	 */
	public static void skip(String message) {
		BaseClass.getTest().log(Status.SKIP, MarkupHelper.createLabel(message, ExtentColor.ORANGE));
	}
}
