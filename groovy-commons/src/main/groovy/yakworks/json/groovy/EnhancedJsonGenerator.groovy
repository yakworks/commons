/*
* Copyright 2022 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.json.groovy

import groovy.json.DefaultJsonGenerator
import groovy.transform.CompileStatic

import yakworks.meta.MetaUtils

@CompileStatic
class EnhancedJsonGenerator extends DefaultJsonGenerator {

    protected EnhancedJsonGenerator(Options options) {
        super(options)
    }

    /**
     * Overriden to filter out statics
     */
    protected Map<?, ?> getObjectProperties(Object object) {
        return MetaUtils.getProperties(object)
    }
}
