package com.liverpool.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadProperties {

	private static Properties config;

	static {
		config = new Properties();
		try (InputStream in = ReadProperties.class.getClassLoader()
				.getResourceAsStream("config/configuration.properties")) {
			if (in == null) {
				throw new IllegalStateException("configuration.properties not found on classpath");
			}
			config.load(in);
		} catch (IOException e) {
			throw new RuntimeException("Error loading configuration.properties", e);
		}
	}

	/**
	 * Retrieves a configuration property value by its key.
	 *
	 * @param key The key of the property to retrieve.
	 * @return The string value of the property.
	 * @throws IllegalArgumentException if the property key is not found in the configuration.
	 */
	public static String getConfig(String key) {
		String value = config.getProperty(key);
		if (value == null) {
			throw new IllegalArgumentException("Property '" + key + "' not found in configuration.properties");
		}
		return value;
	}
}
