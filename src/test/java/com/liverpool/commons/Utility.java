package com.liverpool.commons;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.Assert;

import com.aventstack.extentreports.Status;
import com.github.javafaker.Faker;

public class Utility {

	public static Map<String, String> dataMap = null;
	public static Map<String, String> testdataMap = null;
	public static HashMap<String, HashMap<String, Object>> uiDataMap = new HashMap<>();
	public static HashMap<String, Long> testCaseExecutionTime = new HashMap<>();
	public static long executionTimeDifference = 0;

	private static final String[] BELOW_TWENTY = { "", "one", "two", "three", "four", "five", "six", "seven", "eight",
			"nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen",
			"nineteen" };
	private static final String[] TENS = { "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty",
			"ninety" };
	private static final String[] THOUSANDS = { "", "thousand", "million", "billion" };

	private Utility() {
	}

	/**
	 * Sets up test data by reading from an Excel file. It populates `testdataMap`
	 * and `dataMap` based on the provided parameters.
	 *
	 * @param excelFilePath The path to the Excel file.
	 * @param masterDataSheetName The name of the master data sheet in the Excel file.
	 * @param testDataSheetName The name of the test data sheet in the Excel file.
	 * @param testCaseName The name of the test case for which to retrieve data.
	 * @param url The URL to be used for master data retrieval.
	 * @param environment The environment to be used for master data retrieval.
	 */
	public static void testDataSetup(String excelFilePath, String masterDataSheetName, String testDataSheetName,
			String testCaseName, String url, String environment) {

		Map<Integer, Map<String, String>> excelFileMaps = ExcelReader.getTestDataFromExcel(excelFilePath,
				testDataSheetName, testCaseName);

		for (Map.Entry<Integer, Map<String, String>> entry : excelFileMaps.entrySet()) {
			testdataMap = entry.getValue();
		}

		String ccNumber = "";
		if (testdataMap != null && testdataMap.get("CreditCard") != null) {
			ccNumber = testdataMap.get("CreditCard");
		}

		Map<Integer, Map<String, String>> excelFileMap = ExcelReader.getMasterDataFromExcel(excelFilePath,
				masterDataSheetName, url, environment, ccNumber);

		for (Map.Entry<Integer, Map<String, String>> entry : excelFileMap.entrySet()) {
			dataMap = entry.getValue();
		}
	}

	/**
	 * Converts a given integer number into its word representation.
	 * For example, 123 would be converted to "one hundred twenty three".
	 *
	 * @param number The integer number to convert.
	 * @return The word representation of the number.
	 */
	public static String convertNumberToWord(int number) {
		if (number == 0)
			return "zero";
		String words = "";
		int index = 0;
		while (number > 0) {
			if (number % 1000 != 0) {
				words = convertNumberToWordHelper(number % 1000) + THOUSANDS[index] + " " + words;
			}
			number /= 1000;
			index++;
		}
		return words.trim();
	}

	/**
	 * Helper method to convert a number less than 1000 into words.
	 *
	 * @param number The number (less than 1000) to convert.
	 * @return The word representation of the number.
	 */
	private static String convertNumberToWordHelper(int number) {
		if (number == 0)
			return "";
		else if (number < 20)
			return BELOW_TWENTY[number] + " ";
		else if (number < 100)
			return TENS[number / 10] + " " + convertNumberToWordHelper(number % 10);
		else
			return BELOW_TWENTY[number / 100] + " hundred " + convertNumberToWordHelper(number % 100);
	}

	/**
	 * Generates a formatted log message based on the validation status and type.
	 *
	 * @param <DataType> The type of the actual and expected values.
	 * @param status The status of the validation (e.g., "passed", "failed").
	 * @param fieldName The name of the field being validated.
	 * @param actualValue The actual value obtained.
	 * @param expectedValue The expected value.
	 * @param validationType The type of validation (e.g., "UI", "UI_UI", "UI_SFDC", "SFDC").
	 * @param replacements Optional replacements for dynamic parts of the message.
	 * @return A formatted HTML string representing the log message.
	 */
	private static <DataType> String generateLogMessage(String status, String fieldName, DataType actualValue,
			DataType expectedValue, String validationType, String... replacements) {
		if (validationType.equalsIgnoreCase("UI")) {
			return "<details><summary><i><font> Validation of " + fieldName + " " + status + " </font></i>"
					+ "</summary>" + "<pre>" + "<b>UI field Value: </b>" + actualValue + "<br /><b>Expected Value: </b>"
					+ expectedValue + "</pre>" + "</details> \n";
		} else if (validationType.equalsIgnoreCase("UI_UI")) {
			return String.format("<details><summary><i><font> Validation of " + fieldName + " " + status
					+ " </font></i>" + "</summary>" + "<pre>" + "<b>%s: </b>" + actualValue + "<br /><b>%s: </b>"
					+ expectedValue + "</pre>" + "</details> \n", replacements[0], replacements[1]);
		} else if (validationType.equalsIgnoreCase("UI_SFDC")) {
			return "<details><summary><i><font> Validation of " + fieldName + " " + status + " </font></i>"
					+ "</summary>" + "<pre>" + "<b>UI field Value: </b>" + actualValue + "<br /><b>SFDC Value: </b>"
					+ expectedValue + "</pre>" + "</details> \n";
		} else if (validationType.equalsIgnoreCase("SFDC")) {
			return "<details><summary><i><font> Validation of " + fieldName + " " + status + " </font></i>"
					+ "</summary>" + "<pre>" + "<b>SFDC field Value: </b>" + actualValue
					+ "<br /><b>Expected Value: </b>" + expectedValue + "</pre>" + "</details> \n";
		} else {
			return "Invalid Validation Type";
		}
	}

	/**
	 * Validates UI data fields based on the specified comparison type and logs the result.
	 * It also asserts the comparison using TestNG assertions.
	 *
	 * @param <DataType> The type of the actual and expected values.
	 * @param methodName The name of the calling method for logging purposes.
	 * @param fieldName The name of the field being validated.
	 * @param actualValue The actual value obtained from the UI.
	 * @param expectedValue The expected value.
	 * @param validationType The type of validation (e.g., "UI", "UI_SFDC").
	 * @param screenShot A boolean indicating whether to capture a screenshot on failure.
	 * @param compareType The type of comparison to perform (e.g., "Equals", "Contains", "NotEquals").
	 * @param replacements Optional replacements for dynamic parts of the log message.
	 */
	public static <DataType> void validateUIDataField(String methodName, String fieldName, DataType actualValue,
			DataType expectedValue, String validationType, Boolean screenShot, String compareType,
			String... replacements) {
		try {
			boolean comparisonStatus;
			if (compareType.equalsIgnoreCase("Equals")) {
				comparisonStatus = String.valueOf(actualValue).equals(String.valueOf(expectedValue).trim());
			} else if (compareType.equalsIgnoreCase("Contains")) {
				comparisonStatus = String.valueOf(actualValue).toLowerCase()
						.contains(String.valueOf(expectedValue).trim().toLowerCase())
						|| String.valueOf(expectedValue).toLowerCase()
								.contains(String.valueOf(actualValue).trim().toLowerCase());
			} else if (compareType.equalsIgnoreCase("NotEquals")) {
				comparisonStatus = !(String.valueOf(actualValue).equals(String.valueOf(expectedValue).trim()));
			} else {
				BaseClass.log.info("Invalid compare type");
				comparisonStatus = false;
			}

			String logMessage = generateLogMessage(comparisonStatus ? "passed" : "failed", fieldName, actualValue,
					expectedValue, validationType, replacements);
			if (comparisonStatus) {
				ExtentLogger.pass(logMessage, screenShot);
			} else {
				ExtentLogger.fail(logMessage, screenShot);
			}

			if (compareType.equalsIgnoreCase("Equals")) {
				Assert.assertEquals(actualValue, expectedValue);
			} else if (compareType.equalsIgnoreCase("Contains")) {
				Assert.assertTrue(String.valueOf(actualValue).toLowerCase()
						.contains(String.valueOf(expectedValue).trim().toLowerCase())
						|| String.valueOf(expectedValue).toLowerCase()
								.contains(String.valueOf(actualValue).trim().toLowerCase()));
			} else if (compareType.equalsIgnoreCase("NotEquals")) {
				Assert.assertNotEquals(actualValue, expectedValue);
			}
		} catch (Exception e) {
			BaseClass.log.info("Exception caught in method: " + methodName + " Error: " + e.getMessage());
		}
	}

	/**
	 * Generates a log entry based on the provided status, messages, and screenshot preference.
	 *
	 * @param methodName The name of the calling method for logging purposes.
	 * @param status A boolean indicating the success or failure of an operation.
	 * @param passMessage The message to log if the status is true (pass).
	 * @param failMessage The message to log if the status is false (fail).
	 * @param screenShot A boolean indicating whether to capture a screenshot.
	 */
	public static void logGenerator(String methodName, Boolean status, String passMessage, String failMessage,
			Boolean screenShot) {
		try {
			if (status) {
				ExtentLogger.pass(passMessage, screenShot);
			} else {
				ExtentLogger.fail(failMessage, screenShot);
			}
		} catch (Exception e) {
			BaseClass.log.info("Exception caught in method: " + methodName + " Error: " + e.getMessage());
		}
	}

	/**
	 * Stores UI data for a specific test case in the `uiDataMap`.
	 *
	 * @param testCaseName The name of the test case.
	 * @param data A map containing the UI data to be stored.
	 */
	public static void storeUIData(String testCaseName, Map<String, Object> data) {
		HashMap<String, Object> uiInnerMap = new HashMap<>();
		uiInnerMap.putAll(data);
		uiDataMap.put(testCaseName, uiInnerMap);
		BaseClass.log.info("Updated UI Data Map is: " + uiInnerMap);
	}

	/**
	 * Stores the current execution time for a given test case.
	 *
	 * @param testCaseName The name of the test case.
	 */
	public static void storeExecutionTime(String testCaseName) {
		Long currentTime = System.currentTimeMillis();
		testCaseExecutionTime.put(testCaseName, currentTime);
		BaseClass.log.info("Test Case " + testCaseName + " executed at: " + currentTime);
	}

	/**
	 * Retrieves the time difference in seconds between a stored execution time (plus 15 minutes)
	 * and the current time.
	 *
	 * @param execTime A HashMap containing test case names and their execution times.
	 * @param testCaseName The name of the test case for which to retrieve the time difference.
	 * @return The time difference in seconds.
	 */
	public static long retrieveTimeDiffrence(HashMap<String, Long> execTime, String testCaseName) {
		long storedTime = execTime.get(testCaseName);
		long storedTimePlusFifteenMins = storedTime + java.util.concurrent.TimeUnit.MINUTES.toMillis(15);
		long currentTime = System.currentTimeMillis();
		executionTimeDifference = java.util.concurrent.TimeUnit.MILLISECONDS
				.toSeconds(storedTimePlusFifteenMins - currentTime);
		return executionTimeDifference;
	}

	/**
	 * Retrieves UI data for a specific test case from the `uiDataMap`.
	 *
	 * @param dataMap The HashMap containing all UI data.
	 * @param testCaseName The name of the test case for which to retrieve data.
	 * @return A HashMap containing the UI data for the specified test case.
	 */
	public static HashMap<String, Object> retriveUIData(HashMap<String, HashMap<String, Object>> dataMap,
			String testCaseName) {
		HashMap<String, Object> uiInnerMap = dataMap.get(testCaseName);
		BaseClass.log.info("Retrieved UI Data Map is: " + uiInnerMap);
		return uiInnerMap;
	}

	/**
	 * Removes special characters from the input string, leaving only alphanumeric characters and spaces.
	 *
	 * @param input The input string.
	 * @return The string with special characters removed.
	 */
	public static String removeSpecialChars(String input) {
		return input.replaceAll("[^a-zA-Z0-9 ]", "");
	}

	/**
	 * Converts the input string to title case. Each word in the string will start with an uppercase letter,
	 * and the rest of the letters will be lowercase.
	 *
	 * @param input The input string.
	 * @return The string converted to title case.
	 */
	public static String toTitleCase(String input) {
		String[] words = input.split(" ");
		StringBuilder titleCase = new StringBuilder();
		for (String word : words) {
			if (titleCase.length() > 0)
				titleCase.append(" ");
			titleCase.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase());
		}
		return titleCase.toString();
	}

	/**
	 * Generates a random integer within a specified range (inclusive of the first, exclusive of the last).
	 * If first and last are the same, it returns that number.
	 *
	 * @param first The lower bound of the range (inclusive).
	 * @param last The upper bound of the range (exclusive).
	 * @return A random integer within the specified range.
	 */
	public static int generateNumberFromRange(int first, int last) {
		if (first == last)
			return first;
		return ThreadLocalRandom.current().nextInt(first, last);
	}

	/**
	 * Generates a random 3-digit or 4-digit number.
	 * If `count` is provided as 3, it generates a 3-digit number.
	 * If `count` is provided as 4, it generates a 4-digit number.
	 * If no `count` is provided, it randomly generates either a 3-digit or 4-digit number.
	 *
	 * @param count Optional integer array to specify the number of digits (3 or 4).
	 * @return A randomly generated 3-digit or 4-digit number.
	 */
	public static int generate3Or4DigitNumber(int... count) {
		Random rand = new Random();
		if (count.length > 0 && count[0] == 3)
			return rand.nextInt(900) + 100;
		else if (count.length > 0 && count[0] == 4)
			return rand.nextInt(9000) + 1000;
		else
			return rand.nextBoolean() ? rand.nextInt(900) + 100 : rand.nextInt(9000) + 1000;
	}

	/**
	 * Formats an integer number to a two-digit string, prepending a "0" if the number is less than 10.
	 *
	 * @param number The integer number to format.
	 * @return A two-digit string representation of the number.
	 */
	public static String formatTo2DigitNumber(int number) {
		return number < 10 ? "0" + number : String.valueOf(number);
	}

	/**
	 * Formats a float number to a string with two decimal places.
	 *
	 * @param number The float number to format.
	 * @return A string representation of the float number with two decimal places.
	 */
	public static String formatFloatTo2DigitNumber(float number) {
		return String.format("%.2f", number);
	}

	/**
	 * Extracts a substring from the input string that matches the given regular expression pattern.
	 *
	 * @param inputStr The input string from which to extract.
	 * @param patternStr The regular expression pattern to match.
	 * @return The matched substring, or an empty string if no match is found.
	 */
	public static String getStingUsingPattern(String inputStr, String patternStr) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patternStr);
		java.util.regex.Matcher matcher = pattern.matcher(inputStr);
		String outputStr = "";
		if (matcher.find()) {
			outputStr = matcher.group().trim();
		}
		return outputStr;
	}

	/**
	 * Generates fake data based on the specified type using the Faker library.
	 *
	 * @param type The type of fake data to generate (e.g., "FirstName", "LastName", "PhoneNumber", "OrganizationName").
	 * @return A fake string corresponding to the requested type, or an empty string if the type is not recognized.
	 */
	public static String getFakeString(String type) {
		Faker fake = new Faker();
		if (type.equalsIgnoreCase("FirstName"))
			return fake.name().firstName().replace("'", "");
		else if (type.equalsIgnoreCase("LastName"))
			return fake.name().lastName().replace("'", "");
		else if (type.equalsIgnoreCase("PhoneNumber"))
			return fake.number().digits(10);
		else if (type.equalsIgnoreCase("OrganizationName"))
			return fake.company().name().replace("'", "");
		return "";
	}
}
