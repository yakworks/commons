/*
* Copyright 2022 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.json.jackson

import groovy.transform.CompileStatic

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule

/**
 * Jackson "Module" (normal java Service that gets loaded with ServiceLoader)
 * that will serialize GStrings properly with groovy
 * this is specified in META-INF.services and will get registered on the call to .findAndRegisterModules()
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
