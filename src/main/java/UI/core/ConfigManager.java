package UI.core;

import Common.LogHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = ConfigManager.class
                .getClassLoader()
                .getResourceAsStream("settings.properties")) {
            if (input == null) {
                LogHelper.error("settings.properties not found in resources");
                throw new RuntimeException("settings.properties not found in resources");
            }
            props.load(input);
            LogHelper.info("settings.properties loaded successfully");
        } catch (IOException e) {
            LogHelper.error("Failed to load settings.properties: " + e.getMessage());
            throw new RuntimeException("Failed to load settings.properties", e);
        }
    }

    private ConfigManager() {}

    public static String getProperty(String key) {
        String value = props.getProperty(key);
        LogHelper.debug("Property read: " + key + " = " + value);
        return value;
    }

    public static String baseUrl() {
        return getProperty("BASE_URL");
    }

    public static String user() {
        return getProperty("USER");
    }

    public static String password() {
        return getProperty("PASSWORD");
    }
}
