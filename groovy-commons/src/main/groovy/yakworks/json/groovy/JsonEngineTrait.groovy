/*
* Copyright 2020 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.json.groovy

import groovy.json.JsonGenerator
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

/**
 * simple trait to add getters for jsonSlurper and jsonGenerator
 *
 * @author Joshua Burnett (@basejump)
 * @since 7.0.8
 */
@CompileStatic
trait JsonEngineTrait {


    JsonSlurper getJsonSlurper(){
        return JsonEngine.slurper
    }

    JsonGenerator getJsonGenerator(){
        return JsonEngine.generator
    }

}
