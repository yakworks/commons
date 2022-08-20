/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.json.groovy.converters

import java.time.OffsetTime
import java.time.format.DateTimeFormatter

import groovy.json.JsonGenerator
import groovy.transform.CompileStatic

/**
 * A class to render a {@link OffsetTime} as json
 *
 * @author James Kleeh
 */
@CompileStatic
class OffsetTimeJsonConverter implements JsonGenerator.Converter {

    @Override
    boolean handles(Class<?> type) {
        OffsetTime == type
    }

    @Override
    Object convert(Object value, String key) {
        DateTimeFormatter.ISO_OFFSET_TIME.format((OffsetTime)value)
    }
}
