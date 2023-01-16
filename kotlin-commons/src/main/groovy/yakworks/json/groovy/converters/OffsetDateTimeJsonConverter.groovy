/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.json.groovy.converters

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import groovy.json.JsonGenerator
import groovy.transform.CompileStatic

/**
 * A class to render a {@link OffsetDateTime} as json
 *
 * @author James Kleeh
 */
@CompileStatic
class OffsetDateTimeJsonConverter implements JsonGenerator.Converter {

    @Override
    boolean handles(Class<?> type) {
        OffsetDateTime == type
    }

    @Override
    Object convert(Object value, String key) {
        DateTimeFormatter.ISO_OFFSET_DATE_TIME.format((OffsetDateTime)value)
    }
}
