package com.ddlab.rnd.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReaderUtil {
	private static final Properties PROPERTIES = new Properties();

	static {
		try (InputStream is = ConfigReaderUtil.class.getClassLoader().getResourceAsStream("config.properties")) {

			if (is == null) {
				throw new RuntimeException("config.properties not found");
			}
			PROPERTIES.load(is);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private ConfigReaderUtil() {
	}

	public static String getMessage(String key) {
		return PROPERTIES.getProperty(key);
	}

}
