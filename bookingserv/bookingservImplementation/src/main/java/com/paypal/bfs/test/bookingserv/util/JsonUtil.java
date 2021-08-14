package com.paypal.bfs.test.bookingserv.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Created By Shivanshu Goyal on 14/08/21 Aug, 2021
 */
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    }

    public static String getJsonStringFromObject(Object obj) throws IOException {
        StringWriter writer = new StringWriter();
        objectMapper.writeValue(writer, obj);
        return writer.toString();
    }

    public static <T> T getObjectFromJsonString(Class<T> clazz, String requestJson) throws IOException {
        return objectMapper.readValue(new ByteArrayInputStream(requestJson.getBytes(StandardCharsets.UTF_8)), clazz);
    }

    public static <T> T getObjectFromAnotherObject(Object object, Class<T> clazz) {

        return objectMapper.convertValue(object, clazz);
    }

    public static <T> Object getObjectFromFile(String fileName, Class<T> className)
            throws IOException {
        File file = new File(JsonUtil.class.getClassLoader().getResource(fileName).getFile());
        return objectMapper.readValue(file, className);
    }
}
