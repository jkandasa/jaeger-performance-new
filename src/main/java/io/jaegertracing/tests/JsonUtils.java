package io.jaegertracing.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {
    private static final String ENCODING = "UTF-8";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String asString(Object data) {
        MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            logger.error("Exception,", ex);
            return null;
        }
    }

    public static void dumps(Object data, String... names) {
        MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            String stringData = MAPPER.writeValueAsString(data);
            File file = FileUtils.getFile(names);
            FileUtils.write(file, stringData, ENCODING);
            logger.trace("Json saved: {}", file.getAbsolutePath());
        } catch (IOException ex) {
            logger.error("Exception,", ex);
        }
    }

    public static Object loads(Class<?> clazz, File file) {
        try {
            String content = FileUtils.readFileToString(file, ENCODING);
            return MAPPER.readValue(content, clazz);
        } catch (IOException ex) {
            logger.error("Exception when loading {}", file.getAbsolutePath(), ex);
        }
        return null;
    }

    public static Object loads(Class<?> clazz, String... names) {
        return loads(clazz, FileUtils.getFile(names));
    }

    public static Object loadFromString(String content, Class<?> clazz) {
        try {
            return MAPPER.readValue(content, clazz);
        } catch (IOException ex) {
            logger.error("Exception when loading [{}]", content, ex);
        }
        return null;
    }

}