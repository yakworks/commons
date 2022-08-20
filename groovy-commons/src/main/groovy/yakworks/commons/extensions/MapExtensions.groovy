/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.extensions

import groovy.transform.CompileStatic

/**
 * Extensions to the {@code Map} interface.
 */
@CompileStatic
class MapExtensions {

    /**
     * Very similar to Groovy's {@code Map.get(Object key, Object defaultValue)}
     * method, but allows the default value to be created in a closure.
     * If there is no value for the given key, then the {@code create}
     * closure is executed and its return value is set on the object for the key,
     * and returned to the calling code.
     * <pre>
     *   assert !myObject['someProperty']
     *   def result = map.getOrCreate('someProperty') { ->
     *     return 13
     *   }
     *   assert result == 13
     *   assert myObject['someProperty'] == 13
     * </pre>
     *
     * @return The value stored in the object by {@code key}.
     */
    static <K,V> V getOrCreate(Map<K,V> self, K key, Closure<V> create) {

        def value = self[key]
        if (!value) {
            self[key] = value = create()
        }
        return value
    }
}
