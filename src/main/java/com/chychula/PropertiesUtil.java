package com.chychula;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PropertiesUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private PropertiesUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Properties getLoadedProperties(String fileName) {
        Properties properties = new Properties();
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                logger.warn("Properties file '{}' not found in classpath", fileName);
                return null;
            }
            try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
        } catch (IOException e) {
            logger.error("Error loading properties file: {}", fileName, e);
            return null;
        }
        return properties;
    }
}
