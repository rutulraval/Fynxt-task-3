package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoadProp {

    private static final String TEST_DATA_RESOURCE = "TestData/TestData.properties";
    private static final String UI_CONFIG_RESOURCE  = "config.properties";

    /** Reads a key from TestData/TestData.properties (URLs, API keys, test data paths, etc.) */
    public static String getProperty(String key) {
        return loadFromResource(TEST_DATA_RESOURCE, key);
    }

    /** Reads a key from config.properties (UI element locators / selectors). */
    public static String getUIConfig(String key) {
        return loadFromResource(UI_CONFIG_RESOURCE, key);
    }

    private static String loadFromResource(String resourcePath, String key) {
        Properties prop = new Properties();
        try (InputStream input = LoadProp.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("Unable to locate property resource: " + resourcePath);
            }
            prop.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load property resource: " + resourcePath, e);
        }
        String value = prop.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Key '" + key + "' not found in " + resourcePath);
        }
        return value;
    }
}
