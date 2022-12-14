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
@CompileStatic //JacksonJson.
class JacksonJson {

    static ObjectMapper getObjectMapper() {
        ObjectMapperWrapper.instance.objectMapper
    }

    /** convert object to json string */
    static String toJson(Object object){
        stringify(object)
    }

    /** convert object to json string */
    static String stringify(Object value){
        return objectMapper.writeValueAsString(value)
    }

    /**
     * parse string and expect the class type back.
     * usually would call this with parseJson(text, Map) or parseJson(text, List)
     */
    public static <T> T parseJson(String text, Class<T> clazz) {
        def parsedObj = objectMapper.readValue(text, clazz)
        return (T)parsedObj
    }

    public static <T> T fromString(String string, Class<T> clazz) {
        return objectMapper.readValue(string, clazz)
    }

    public static <T> T fromString(String string, Type type) {
        return objectMapper.readValue(string, objectMapper.getTypeFactory().constructType(type))
    }

    static JsonNode toJsonNode(String value) {
        return objectMapper.readTree(value)
    }

    /** binds the data to new instance of the pased in class */
    public static <T> T bind(Object data, Class<T> toValueType) throws IllegalArgumentException {
        return objectMapper.convertValue(data, toValueType);
    }

    /** binds the data to new instance of the pased in class */
    public static <T> T bind(T instance, Object data) {
        return objectMapper.updateValue(instance, data);
    }

    public static <T> T fromBytes(byte[] value, Class<T> clazz) throws IOException {
        return objectMapper.readValue(value, clazz);
    }

    public static <T> T fromBytes(byte[] value, Type type) throws IOException {
        return objectMapper.readValue(value, objectMapper.getTypeFactory().constructType(type));

    }
}
