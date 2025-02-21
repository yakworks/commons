/*
* Copyright 2019 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.map


import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import yakworks.commons.beans.PropertyTools
import yakworks.commons.lang.Validate
import yakworks.commons.util.StringUtils
import yakworks.util.ClassUtils

/**
 * Helpful methods for dealing with maps
 * some merge ideas take from https://gist.github.com/robhruska/4612278 and https://e.printstacktrace.blog/how-to-merge-two-maps-in-groovy/
 *
 * @author Joshua Burnett (@basejump)
 */
@Slf4j
@CompileStatic
class Maps {


    /**
     * Return the value of a nested path. Alias to PropertyTools.getProperty.
     *
     * Example Maps.getProperty(source, "x.y.z")
     *
     * @param source - The source object
     * @param property - the property
     * @return value of the specified property or null if any of the intermediate objects are null
     */
    static Object value(Map source, String property) {
        PropertyTools.getProperty(source, property)
    }

    /**
     * puts the deepest nested Map for the path in the Map of Maps
     * or create the path if it doesn't exit and returns the reference.
     *
     * Differs from the PropertyTools.setValue in that it will create a nested Map when it encounters a property
     * that does not exist or is a BasicType. If you want the error use PropertyTools.setValue as its strict.
     *
     * example1: putValue([a: [b: [c: 'bar']]], 'a.b.c', 'foo') == [a: [b: [c: 'foo']]]
     *
     * example2: Will overwrite basic types when it conflicts
     *           putValue([a: [b: "blah"]], 'a.b.c', 'foo') == [a: [b: [c: 'foo']]]
     *
     * example3: Will overwrite basic types when it conflicts
     *           putValue([a: [x: "stay"]], 'a.b.c', 'foo') == [a: [x: "stay"], [b: [c: 'foo'] ] ]
     *
     * @param map       | the target map
     * @param propPath  | the delimited path to the key
     * @param value     | the value to set at the propertyPath
     * @param pathDelimiter [default: '.'] if the path is delimeted by somehting like "_' then can set it here. Useful for csv.
     * @return Map
     */
    static Map putValue(Map map, String propPath, Object value, String pathDelimiter = '.' ) {
        int i = propPath.lastIndexOf(pathDelimiter)
        //get the last prop key,  so for "a.b.c.d", this will get "d"
        String lastKey = propPath.substring(i + 1, propPath.length())
        if (i > -1) {
            //sping through first part, so for a.b.c.d, this will iterate over a.b.c
            propPath.substring(0, i).tokenize(pathDelimiter).each { String k ->
                var m = map.get(k)
                //if its null or its a basic type then overwrite it. so for a.b.c.d example, if map already has a.b.c=foo will overwrite
                if(m == null || ClassUtils.isPrimitiveOrWrapper(m.class) || m instanceof CharSequence) {
                    map = map[k] = [:]
                } else {
                    map = (Map) m
                }
            }
        }
        map[lastKey] = value
        return map
    }

    /**
     * Removes the deeply nested key from map
     *
     * example1: remove([a: [b: [c: 'bar', foo:baz]]], 'a.b.c') == [a: [b: [foo:baz]]]
     */
    static Object remove(Map map, String key) {
        if(key.contains('.')) {
            List<String> keyTokens = key.tokenize('.')
            String keyToRemove = keyTokens.remove(keyTokens.size() - 1)//last key after dot
            String parentKey = keyTokens.join('.')
            Object parent = value(map, parentKey)
            if(parent && parent instanceof Map) {
                return parent.remove(keyToRemove)
            }
            return null
        }
        else {
            return map.remove(key)
        }
    }

    /**
     * Checks if the map contains the key. Supports deeply nested key
     * example1: containsKey([a: [b: [c: 'bar']]], 'a.b.c') == true
     */
    static boolean containsKey(Map map, String key) {
        if(key.contains('.')) {
            List<String> keyTokens = key.tokenize('.')
            String lastKey = keyTokens.remove(keyTokens.size() - 1)//last key after dot
            String parentKey = keyTokens.join('.')
            Object parent = value(map, parentKey)
            return parent && (parent instanceof Map) && parent.containsKey(lastKey)
        }
        else {
            return map.containsKey(key)
        }
    }

    /**
     * DEEPLY merges into the target by recursively copying the values of each Map in sources,
     * Sources are applied from left to right. Subsequent sources overwrite property assignments of previous sources.
     * so if you call extend(a, b, c) then b overrites a's values and c overwrites b values (when they exist and are not null)
     *
     * NOTE: the target is modified, if you want it merged into a new map then pass in a new map ([:], map1, map2) to target
     * as thats what will be returned.
     *
     * Mimics 'merge()' functions often seen in JavaScript libraries.
     * Any specific Map implementations (e.g. TreeMap, LinkedHashMap)
     * are not guaranteed to be retained. The ordering of the keys in
     * the result Map is not guaranteed. Only nested Maps and Collections will be
     * merged; primitives, objects, and other collection types will be
     * overwritten.
     *
     * The source maps will not be modified, only the target is modified.
     *
     * If no sources passed in then it just returns target without making a copy or modifying
     *
     * @return the new merged map, will be same as the passed in target as its modified
     */
    static Map merge(Map target, Map... sources) {
        if (sources.length == 0) return target

        sources.inject(target) { merged, source ->
            source.each { k, val ->
                def mergedVal = merged[k]
                //we do maps and collections first as most are Cloneable but they only do a shallow clone, we do a deep.
                if (( mergedVal == null || mergedVal instanceof Map ) && val instanceof Map) {
                    if(mergedVal == null) merged[k] = [:]
                    merge(merged[k] as Map, val as Map)
                }
                else if(val instanceof Range){
                    //Groovy Ranges are Lists, we dont try to clone and just set it otherwise they end up as new collection not Range
                    merged[k] = val
                }
                else if ((mergedVal == null || mergedVal instanceof Collection) && val instanceof Collection) {
                    if(mergedVal == null) merged[k] = []
                    merged[k] = (Collection)merged[k] + (Collection)val
                    //The list could be list of maps, so make sure they get copied
                    //XXX should do an add all above to merged[k], then we dont loose it?
                    merged[k] = merged[k].collect{ item ->
                        // ALSO we only clone the map below, it could be a Collection too, which we should clone too.
                        return (item instanceof Map) ? merge([:], item as Map) : item
                    }
                }
                // else if(val instanceof Cloneable){
                //     //If its cloneable, its doesnt merge it, it overrites it. but does try to clone it.
                //     try{
                //         merged[k] = val.clone()
                //     } catch (e){
                //         //on any error then just sets the val
                //         merged[k] = val
                //     }
                // }
                else {
                    merged[k] = val
                }
            }
            return merged
        } as Map

        return target
    }

    static Map merge(Map target, List<Map> sources) {
        return merge( target, sources as Map[])
    }

    /**
     * Does a "deep" clone of the Map by recursively cloning values when possible.
     * It just calls merge([:], source)
     * If a shallow clone is desired use whats built into most al Java Map impls.
     * see merge. it uses merge to do a deep copy of the map into a new Map
     *
     * @return the cloned map
     */
    static Map clone(Map source) {
        if(!source) return [:]
        return merge([:], source)
    }

    /**
     * Does a "deep" clone of the Collection of Maps.
     * See clone
     *
     * @return the cloned map
     */
    static Collection<Map> clone(Collection<Map> listOfMaps) {
        if(!listOfMaps) return []
        listOfMaps.collect{ Maps.clone(it)}
    }

    /**
     * Transforms a string path like "x.y.z" to [x:[y:[z:value]]]
     *
     * @param propertyPath - The path like 'x.y.z'
     * @param value - the value to put in lowest map
     * @return The Map
     */
    static Map<String, Object> pathToMap(String propertyPath, Object value) {
        Validate.notNull(propertyPath, '[source]')
        Map result = propertyPath.tokenize('.').reverse().inject(value) { Object v, Object prop ->
            [(prop):v]
        } as Map

        return result as Map<String, Object>
    }

    /**
     * Deeply remove/prune all nulls and "falsey" empty maps, lists and strings as well
     *
     * @param map the map to prune
     * @param pruneEmpty default:true set to false to keep empty maps, lists and strings and only prune nulls
     * @return the pruned map
     */
    public static <K, V> Map<K, V> prune(Map<K, V> map, boolean pruneEmpty = true) {
        if(!map) return map
        map.collectEntries { k, v ->
            [k, v instanceof Map ? prune(v as Map, pruneEmpty) : v]
        }.findAll { k, v ->
            if(pruneEmpty){
                if (v instanceof List || v instanceof Map || v instanceof String) {
                    return v
                } else {
                    return v != null
                }
            } else {
                return v != null
            }

        } as Map<K, V>
    }

    /**
     * recursively removes the flattened spring keys in the form of foo[0] that the config creates for lists
     *
     * @returns a new copied map with the fixes
     */
    static Map removePropertyListKeys(Map<String, Object> cfgMap){
        // for deeply nested array  like `foo: [1,2]`config can transform to [foo[0]: 1, foo[1]: 2], without `foo: [1,2]` in final result
        // so need to find such values and transform back to array
        List<String> array = cfgMap.keySet().findAll{ (it.matches(/.*\[\d*\]/) && !it.contains('.')) && !cfgMap.keySet().contains(it.split('\\[')[0])} as List
        def newCfgMap = cfgMap.findAll {
            !it.key.matches(/.*\[\d*\]/) && !it.key.contains('.')
        } as Map<String, Object>
        if (array) {
            array.reverse().each{
                String key = it.split('\\[')[0]
                if (newCfgMap[key] instanceof List) {
                    (newCfgMap[key] as List).push(cfgMap[it])
                } else {
                    newCfgMap[key] = [cfgMap[it]]
                }
            }
        }
        for (String key : newCfgMap.keySet()) {
            def val = newCfgMap[key]
            if(val instanceof Map){
                newCfgMap[key] = removePropertyListKeys(val as Map)
            }
            if (val instanceof List) {
                newCfgMap[key] = val.collect{it instanceof Map ? removePropertyListKeys(it) : it}
            }
        }
        return newCfgMap
    }

    /**
     * Loosely test 2 maps for equality
     * asserts more or less that every keySet in [a: 1, b: 2] exists in [a: 1, b: 2, c: 3] which is true in this example
     * asserts more or less that subset:[a: 1, b: 2] == full:[a: 1, b: 2, c: 3]
     * mapContains([a: 1, b: 2], [a: 1, c: 3]) returns false
     * mapContains([a: 2, b: 2], [a: 1, b: 2]) also returns false
     * if subset is an empty map or null returns false
     *
     * @param full the map to look in
     * @param subset the subset of values to make sure are in the full
     * @param exclude optional list of keys to exclude from the subset
     * http://csierra.github.io/posts/2013/02/12/loosely-test-for-map-equality-using-groovy/
     */
    static boolean mapContains(Map full, Map subset, List<String> exclude=[]) {
        //println "subset: $subset"
        //println "full: $full"
        if(!subset) return false
        return subset.findAll{
            !exclude.contains(it.key)
        }.every {
            def val = it.value
            if(val instanceof Map){
                return mapContains(full[it.key] as Map, val)
            } else {
                return val == full[it.key]
            }
        }
    }

    /**
     * checks that main map contains all the subset
     */
    static boolean containsAll(Map main, Map subset){
        main.entrySet().containsAll(subset.entrySet())
    }


    /**
     * Retrieves a boolean value from a Map for the given key
     *
     * @param key The key that references the boolean value
     * @param map The map to look in
     * @param defaultReturn if its doesn't have the key or map is null this is the default return value
     * @return A boolean value which will be false if the map is null, the map doesn't contain the key or the value is false
     */
    @SuppressWarnings('EmptyCatchBlock')
    static boolean getBoolean(Map<?, ?> map, Object key, boolean defaultValue = false) {
        if (map == null) return defaultValue

        if (map.containsKey(key)) {
            Object o = map.get(key)
            if (o == null) return false
            if (o instanceof Boolean) {
                return (Boolean)o
            }
            try {
                if (o != null) {
                    return StringUtils.toBoolean(o.toString())
                }
            }
            catch (Exception e) {
                /* swallow exception and will return default */
            }
        }
        return defaultValue
    }

    static boolean 'boolean'(Map<?, ?> map, Object key, boolean defaultValue = false) {
        return getBoolean(map, key, defaultValue)
    }

    @SuppressWarnings(['EmptyCatchBlock'])
    static List getList(Map<?, ?> map, Object key, List defaultValue = []) {
        if (map?.containsKey(key)) {
            Object o = map.get(key)
            if (o == null) return defaultValue
            if (o.getClass().isArray()) {
                return Arrays.asList((Object[])o)
            }
            if (o instanceof Collection) {
                return o as List
            }
            try {
                return StringUtils.split(o.toString())
            }

            catch (Exception e) {
                /* swallow exception and will return default */
            }
        }
        return defaultValue
    }

    /**
     * returns map with specific keys
     */
    public static <K, V> Map<K, V> pick(Map<K, V> map, Collection<K> keys){
        if(!map) return [:]
        map.subMap(keys)
    }

    /**
     * returns map with keys excluded
     */
    public static <K, V> Map<K, V> omit(Map<K, V> map, Collection<K> excludeKeys){
        if(!map) return [:]
        def keys = map.keySet().findAll{ !(it in excludeKeys)}
        map.keySet()
        return map.subMap(keys)
    }

}
