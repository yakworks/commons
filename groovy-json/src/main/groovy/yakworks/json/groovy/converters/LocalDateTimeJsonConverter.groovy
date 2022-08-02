/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.json.groovy.converters

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import groovy.json.JsonGenerator
import groovy.transform.CompileStatic

/**
 * A class to render a {@link LocalDateTime} as json
 *
 * @author James Kleeh
 */
@CompileStatic
class LocalDateTimeJsonConverter implements JsonGenerator.Converter {

    @Override
    boolean handles(Class<?> type) {
        LocalDateTime == type
    }

    @Override
    Object convert(Object value, String key) {
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format((LocalDateTime)value)
    }
}
