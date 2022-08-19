/*
* Copyright 2022 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.json.groovy

import groovy.json.DefaultJsonGenerator

import yakworks.meta.MetaUtils

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
