/*
* Copyright 2020 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.json.groovy

import javax.servlet.http.HttpServletRequest

import groovy.transform.CompileStatic

/**
 * Trait to adds parse methods for using JsonSlurper to parse HttpServletRequest body
 *
 * @author Joshua Burnett
 * @since 7.0.8
 */
@CompileStatic
trait HttpJsonParserTrait extends JsonEngineTrait{

    /**
     * Parse a JSON Map data structure from request body and casts.
     * Clazz should be either list or map
     */
    public <T> T parseJson(HttpServletRequest req, Class<T> clazz) {
        boolean hasContent = req.contentLength > 0
        Object parsedObj
        if(hasContent){
            parsedObj = parseJson(req)
        } else {
            //if its not a map type then assume its list
            parsedObj = Map.isAssignableFrom(clazz) ? Collections.emptyMap() : Collections.emptyList()
        }
        JsonEngine.validateExpectedClass(clazz, parsedObj)
        return (T)parsedObj
    }

    /**
     * Parse a JSON Map data structure from request body input stream.
     * if no content then returns an empty map
     */
    Object parseJson(HttpServletRequest req) {
        boolean hasContent = req.contentLength
        return hasContent ? getJsonSlurper().parse(req.inputStream) : Collections.emptyMap()
    }

}
