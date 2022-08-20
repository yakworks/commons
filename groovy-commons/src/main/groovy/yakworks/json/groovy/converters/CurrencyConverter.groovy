/*
* Copyright 2020 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.json.groovy.converters

import groovy.json.JsonGenerator
import groovy.transform.CompileStatic

/**
 * currency converter for json
 */
@CompileStatic
class CurrencyConverter implements JsonGenerator.Converter {

    @Override
    boolean handles(Class<?> type) {
        Currency == type
    }

    @Override
    Object convert(Object value, String key) {
        ((Currency)value).currencyCode
    }
}
