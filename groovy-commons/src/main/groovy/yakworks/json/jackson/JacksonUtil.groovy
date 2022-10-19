/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.json.jackson;

import java.lang.reflect.Type

import groovy.transform.CompileStatic

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Static helpers around Jackson. Many of these are not really needed but serves as what we consider more obvious
 * naming when dealing with Maps. Also serve as reminders or a dingus on how things can be done.
 */
@CompileStatic
public class JacksonUtil {

    static String toJson(Object object){
        stringify(object)
    }

    static String stringify(Object object){
        return ObjectMapperWrapper.INSTANCE.toString(object);
    }

    /**
     * parse string and expect the class type back.
     * usually would call this with parseJson(text, Map) or parseJson(text, List)
     */
    static <T> T parseJson(String text, Class<T> clazz) {
        def parsedObj = ObjectMapperWrapper.INSTANCE.fromString(text, clazz);

        return (T)parsedObj
    }

    public static <T> T fromString(String string, Class<T> clazz) {
        return ObjectMapperWrapper.INSTANCE.fromString(string, clazz);
    }

    public static <T> T fromString(String string, Type type) {
        return ObjectMapperWrapper.INSTANCE.fromString(string, type);
    }

    public static JsonNode toJsonNode(String value) {
        return ObjectMapperWrapper.INSTANCE.toJsonNode(value);
    }

    /** binds the data to new instance of the pased in class */
    public static <T> T bind(Object data, Class<T> toValueType) throws IllegalArgumentException {
        ObjectMapper mapper = ObjectMapperWrapper.INSTANCE.objectMapper
        return mapper.convertValue(data, toValueType);
    }

    /** binds the data to new instance of the pased in class */
    public static <T> T bind(T instance, Object data) {
        ObjectMapper mapper = ObjectMapperWrapper.INSTANCE.objectMapper
        return mapper.updateValue(instance, data);
    }
}
