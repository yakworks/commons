/*
* Copyright 2019 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.meta

import java.lang.reflect.Modifier

import groovy.transform.CompileStatic

import yakworks.openapi.OapiUtils

/**
 * A bunch of helper and lookup/finder statics for dealing with domain classes and PersistentEntity.
 * Useful methods to find the PersistentEntity and the mapping and meta fields.
 *
 * @author Joshua Burnett (@basejump)
 * @since 6.1
 */
@CompileStatic
class MetaUtils {

    // list of props to exclude in isExcluded used for getProperties
    private static List<String> PROPERTY_EXCLUDES = [
        "class", "declaringClass", "metaClass"
    ]

    /**
     * only returns the instance metaProperties
     * Get the meta properties for and object but filters out statics and props without a getter
     */
    static List<MetaProperty> getMetaProperties(Class<?> entityClass) {
        List<MetaProperty> metaProps = entityClass.metaClass.properties
        List<MetaProperty> filteredProps = metaProps.findAll { MetaProperty mp ->
            !isExcludedProperty(mp)
        }
        return filteredProps
    }

    /**
     * the default Groovy getProperties returns statics.
     * This only returns the instance values
     */
    static Map<String, Object> getProperties(Object instance) {
        Map<String, Object> props = [:]
        for (MetaProperty mp : getMetaProperties(instance.class)) {
            props[mp.name] = mp.getProperty(instance)
        }
        return props
    }

    /**
     * used for getProperties to exclude the utility properties that are on a GormEntity.
     */
    static boolean isExcludedProperty(MetaProperty mp) {
        return Modifier.isStatic(mp.getModifiers()) ||
            PROPERTY_EXCLUDES.contains(mp.getName()) ||
            (mp instanceof MetaBeanProperty) && (((MetaBeanProperty) mp).getGetter()) == null
    }

}
