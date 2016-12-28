package ch.unibe.inf.scg_seminar_exceptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Contains accessors to keys and values in configuration.properties.
 */
public class Settings {
	public static final Settings instance = new Settings();
	private Map<String, String> values = new HashMap<>();

	public Settings() {
		values = new HashMap<>();
		Properties properties = new Properties();
		try {
			properties.load(getClass().getClassLoader().getResourceAsStream("configuration.properties"));
			for (String key : properties.stringPropertyNames()) {
				values.put(key, properties.getProperty(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String at(String key) {
		return instance.values.get(key);
	}

	public static String databaseUser() {
		return at("database_user");
	}

	public static String databasePassword() {
		return at("database_password");
	}

	public static String databaseName() {
		return at("database_name");
	}

	public static String databaseConnection() {
		return "jdbc:postgresql://" + databaseHost() + ":" + databasePort() + "/" + databaseName();
	}

	public static String databaseHost() {
		return at("database_host");
	}

	public static String databasePort() {
		return at("database_port");
	}

}
