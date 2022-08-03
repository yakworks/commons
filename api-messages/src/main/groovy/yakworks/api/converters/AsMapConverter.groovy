/*
* Copyright 2013 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.converters

import groovy.json.JsonGenerator
import groovy.transform.CompileStatic

import yakworks.api.ApiResults
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
