package io.jaegertracing.tests;

public class TestUtils {

    public static Boolean getBooleanEnv(String key, String defaultValue) {
        return Boolean.valueOf(getStringEnv(key, defaultValue));
    }

    public static Float getFloatEnv(String key, String defaultValue) {
        return Float.valueOf(getStringEnv(key, defaultValue));
    }

    public static Integer getIntegerEnv(String key, String defaultValue) {
        return Integer.valueOf(getStringEnv(key, defaultValue));
    }

    public static String getStringEnv(String key, String defaultValue) {
        return System.getenv().getOrDefault(key, defaultValue);
    }

}
