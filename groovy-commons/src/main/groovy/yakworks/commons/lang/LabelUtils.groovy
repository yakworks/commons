/*
* Copyright 2022 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.lang

import groovy.transform.CompileStatic

/**
 * helper for names and labels.
 * Leans on the the common trait props in the model package.
 */
@CompileStatic
class LabelUtils {

    /**
     * Like NameUtils.getNaturalName but doesn't only use the last part of the "." dot path.
     * And if it matches key fields thens will keep that to append.
     * Some examples
     *   Customer.name -> Customer
     *   Customer.num -> Customer Num
     *
     * @param propName the property name to convert
     * @return the title
     */
    // @CompileDynamic
    static String getNaturalTitle(String propName) {
        propName = propName.endsWith(".name") ? propName[0..<propName.lastIndexOf('.')] : propName

        propName = getObjectAndProp(propName)
        // make foo.bar into fooBar so we can pass it through the getNaturalName
        propName = propName.replaceAll("(\\.)([A-Za-z0-9])") { Object[] it -> it[2].toUpperCase() }
        //text
        return NameUtils.getNaturalName(propName)

    }

    /**
     * Returns just the last dot part
     * so foo.bar.baz -> bar.baz
     */
    static String getObjectAndProp(String text) {
        if(text.count('.') > 1){
            List tokens = text.tokenize('.')
            int toksize = tokens.size()
            String label = tokens[toksize-2] + "." + tokens[toksize-1]
            return label
        } else {
            return text
        }
    }
}
