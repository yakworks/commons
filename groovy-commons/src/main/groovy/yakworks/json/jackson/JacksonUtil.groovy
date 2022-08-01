/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.json.jackson;

import java.lang.reflect.Type

import groovy.transform.CompileStatic

import com.fasterxml.jackson.databind.JsonNode

/**
 * @author Vlad Mihalcea
 */
@CompileStatic
public class JacksonUtil {

    static String toJson(Object object){
        stringify(object)
    }

    static String stringify(Object object){
        return ObjectMapperWrapper.INSTANCE.toString(object);
    }

    public static <T> T fromString(String string, Class<T> clazz) {
        return ObjectMapperWrapper.INSTANCE.fromString(string, clazz);
    }

    public static <T> T fromString(String string, Type type) {
        return ObjectMapperWrapper.INSTANCE.fromString(string, type);
    }

    public static String toString(Object value) {
        return ObjectMapperWrapper.INSTANCE.toString(value);
    }

    public static JsonNode toJsonNode(String value) {
        return ObjectMapperWrapper.INSTANCE.toJsonNode(value);
    }
}
