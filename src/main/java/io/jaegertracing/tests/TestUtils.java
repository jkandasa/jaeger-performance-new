package io.jaegertracing.tests;

import org.apache.commons.lang3.time.DurationFormatUtils;

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

    public static String timeTaken(long durationMillis) {
        return DurationFormatUtils.formatDurationHMS(durationMillis);
    }
}
