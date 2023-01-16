/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.json.groovy.converters

import java.time.Period

import groovy.json.JsonGenerator
import groovy.transform.CompileStatic

/**
 * A class to render a {@link Period} as json
 *
 * @author Muhammad Hamza Zaib
 */
@CompileStatic
class PeriodJsonConverter implements JsonGenerator.Converter {

    @Override
    boolean handles(Class<?> type) {
        Period == type
    }

    @Override
    Object convert(Object value, String key) {
        ((Period)value).toString()
    }
}
