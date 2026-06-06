package com.liverpool.commons;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class ExtentLogger {

	private ExtentLogger() {
	}

	public static void pass(String message) {
		BaseClass.getTest().log(Status.PASS, MarkupHelper.createLabel(message, ExtentColor.GREEN));
	}

	public static void pass(String message, boolean screenshot) {
		BaseClass.getTest().log(Status.PASS, MarkupHelper.createLabel(message, ExtentColor.GREEN));
		if (screenshot) {
			BaseClass.addScreenCaptureToReport();
		}
	}

	public static void fail(String message) {
		BaseClass.getTest().log(Status.FAIL, MarkupHelper.createLabel(message, ExtentColor.RED));
	}

	public static void fail(String message, boolean screenshot) {
		BaseClass.getTest().log(Status.FAIL, MarkupHelper.createLabel(message, ExtentColor.RED));
		if (screenshot) {
			BaseClass.addScreenCaptureToReport();
		}
	}

	public static void info(String message) {
		BaseClass.getTest().log(Status.INFO, message);
	}

	public static void skip(String message) {
		BaseClass.getTest().log(Status.SKIP, MarkupHelper.createLabel(message, ExtentColor.ORANGE));
	}
}
