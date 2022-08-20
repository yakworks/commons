/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.json.groovy.converters

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import groovy.json.JsonGenerator
import groovy.transform.CompileStatic

/**
 * A class to render a {@link ZonedDateTime} as json
 *
 * @author James Kleeh
 */
@CompileStatic
class ZonedDateTimeJsonConverter implements JsonGenerator.Converter {

    @Override
    boolean handles(Class<?> type) {
        ZonedDateTime == type
    }

    @Override
    Object convert(Object value, String key) {
        DateTimeFormatter.ISO_ZONED_DATE_TIME.format((ZonedDateTime)value)
    }
}
