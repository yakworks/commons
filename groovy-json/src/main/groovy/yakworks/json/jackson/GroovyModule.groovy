/*
* Copyright 2022 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.json.jackson

import groovy.transform.CompileStatic

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule

/**
 * Module that will serialize GStrings properly with groovy
 */
@CompileStatic
class GroovyModule  extends SimpleModule {

    GroovyModule() {
        addSerializer(GString, new GStringJsonSerializer())
    }

    static class GStringJsonSerializer extends JsonSerializer<GString> {

        @Override
        void serialize(GString value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (value == null) {
                jsonGenerator.writeNull()
            } else {
                jsonGenerator.writeString(value.toString())
            }
        }

        @Override
        Class<GString> handledType() {
            return GString;
        }

    }
}
