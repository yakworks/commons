/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.json.groovy.converters

import java.time.Instant

import groovy.json.JsonGenerator
import groovy.transform.CompileStatic

/**
 * A class to render a {@link java.time.Instant} as json
 *
 * @author James Kleeh
 */
@CompileStatic
class InstantJsonConverter implements JsonGenerator.Converter {

    @Override
    boolean handles(Class<?> type) {
        Instant == type
    }

    @Override
    Object convert(Object value, String key) {
        ((Instant)value).toEpochMilli()
    }
}
