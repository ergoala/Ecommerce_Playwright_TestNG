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

	public static void storeUIData(String testCaseName, Map<String, Object> data) {
		HashMap<String, Object> uiInnerMap = new HashMap<>();
		uiInnerMap.putAll(data);
		uiDataMap.put(testCaseName, uiInnerMap);
		BaseClass.log.info("Updated UI Data Map is: " + uiInnerMap);
	}

	public static void storeExecutionTime(String testCaseName) {
		Long currentTime = System.currentTimeMillis();
		testCaseExecutionTime.put(testCaseName, currentTime);
		BaseClass.log.info("Test Case " + testCaseName + " executed at: " + currentTime);
	}

	public static long retrieveTimeDiffrence(HashMap<String, Long> execTime, String testCaseName) {
		long storedTime = execTime.get(testCaseName);
		long storedTimePlusFifteenMins = storedTime + java.util.concurrent.TimeUnit.MINUTES.toMillis(15);
		long currentTime = System.currentTimeMillis();
		executionTimeDifference = java.util.concurrent.TimeUnit.MILLISECONDS
				.toSeconds(storedTimePlusFifteenMins - currentTime);
		return executionTimeDifference;
	}

	public static HashMap<String, Object> retriveUIData(HashMap<String, HashMap<String, Object>> dataMap,
			String testCaseName) {
		HashMap<String, Object> uiInnerMap = dataMap.get(testCaseName);
		BaseClass.log.info("Retrieved UI Data Map is: " + uiInnerMap);
		return uiInnerMap;
	}

	public static String removeSpecialChars(String input) {
		return input.replaceAll("[^a-zA-Z0-9 ]", "");
	}

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

	public static int generateNumberFromRange(int first, int last) {
		if (first == last)
			return first;
		return ThreadLocalRandom.current().nextInt(first, last);
	}

	public static int generate3Or4DigitNumber(int... count) {
		Random rand = new Random();
		if (count.length > 0 && count[0] == 3)
			return rand.nextInt(900) + 100;
		else if (count.length > 0 && count[0] == 4)
			return rand.nextInt(9000) + 1000;
		else
			return rand.nextBoolean() ? rand.nextInt(900) + 100 : rand.nextInt(9000) + 1000;
	}

	public static String formatTo2DigitNumber(int number) {
		return number < 10 ? "0" + number : String.valueOf(number);
	}

	public static String formatFloatTo2DigitNumber(float number) {
		return String.format("%.2f", number);
	}

	public static String getStingUsingPattern(String inputStr, String patternStr) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patternStr);
		java.util.regex.Matcher matcher = pattern.matcher(inputStr);
		String outputStr = "";
		if (matcher.find()) {
			outputStr = matcher.group().trim();
		}
		return outputStr;
	}

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
