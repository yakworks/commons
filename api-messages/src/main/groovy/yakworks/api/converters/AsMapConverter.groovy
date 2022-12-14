/*
* Copyright 2013 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.api.converters

import groovy.json.JsonGenerator
import groovy.transform.CompileStatic

import yakworks.api.AsMap

/**
 * Renderer for paged list data
 *
 * @author Joshua Burnett (@basejump)
 * @since 7.0.8
 */
@CompileStatic
class AsMapConverter implements JsonGenerator.Converter {

    @Override
    boolean handles(Class<?> type) {
        AsMap.isAssignableFrom(type)
    }

    @Override
    Object convert(Object value, String key) {
        def obj = (AsMap)value
        return obj.asMap()
    }

}
