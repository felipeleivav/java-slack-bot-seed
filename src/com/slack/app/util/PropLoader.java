package com.slack.app.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropLoader {

    private static Logger logger = Logger.getLogger(PropLoader.class);

    private static Properties properties;

    public static void initialize() {
        try {
            String propName = "slack.bot.properties";
            InputStream propFile = PropLoader.class.getClassLoader().getResourceAsStream(propName);
            properties = new Properties();
            properties.load(propFile);
            logger.info("Loaded config");
        } catch (Exception e) {
            logger.error("Error initializing properties: ", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String placeholder) {
        key = key.replace("?", placeholder);
        return properties.getProperty(key);
    }

    public static boolean getB(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public static boolean getB(String key, String placeholder) {
        key = key.replace("?", placeholder);
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public static int getI(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public static int getI(String key, String placeholder) {
        key = key.replace("?", placeholder);
        return Integer.parseInt(properties.getProperty(key));
    }

    public static long getL(String key) {
        return Long.valueOf(properties.getProperty(key));
    }

    public static long getL(String key, String placeholder) {
        key = key.replace("?", placeholder);
        return Long.valueOf(properties.getProperty(key));
    }

}
