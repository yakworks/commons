/*
* Copyright 2004-2005 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.map

import groovy.transform.CompileStatic

import org.codehaus.groovy.util.HashCodeHelper

/**
 * A redo of the  GrailsParameterMap, primary to remove the need for HttpServletRequest.
 * Allows a flattened map of path keys such that
 *
 * example: [foo.bar.id:1, foo.amount:10]
 * when removePathKeys=false would end up as ["foo.bar.id": 1, "foo.amount": 10, foo: [bar: [id: 1]], amount:10]
 * when removePathKeys=true then would be [foo: [bar: [id: 1]], amount:10]

 * Useful for CSV reading too.
 */
// !!!!!!! PROOF OF CONCEPT, USE LazyPathKeyMap INSTEAD UNLESS PERF SUCKS
@Deprecated
@SuppressWarnings(["ExplicitCallToEqualsMethod"])
@CompileStatic
class PathKeyMap2 implements Map<String, Object>, Cloneable  {

    private Map<String, Object> wrappedMap

    /** the delimiter, defaults to '.' can be changed to somthing like _ so would break apart fields like foo_bar*/
    String pathDelimiter = '.'

    /** defaults to false, whether to remove the orginal path key after its been nested into maps */
    boolean removePathKeys = true

    boolean initialized = false

    /** TODO (MAKE THIS WORK) When set to false will keep the Map flat */
    boolean enabled = true

    /**
     * Populates the PathKeyMap with supplied map.
     *
     * @param values The values to populate with
     */
    PathKeyMap2(Map<String, Object> sourceMap) {
        wrappedMap = sourceMap ?: ([:] as Map<String, Object>)
    }

    static PathKeyMap2 of(Map<String, Object> sourceMap){
        return new PathKeyMap2(sourceMap)
    }

    static PathKeyMap2 of(Map<String, Object> sourceMap, String pathDelimiter ){
        def pkm = new PathKeyMap2(sourceMap)
        pkm.pathDelimiter = pathDelimiter
        return pkm
    }

    static PathKeyMap2 of(Map<String, Object> sourceMap, String pathDelimiter, boolean removePathKeys){
        def pkm = new PathKeyMap2(sourceMap)
        pkm.pathDelimiter = pathDelimiter
        pkm.removePathKeys = removePathKeys
        return pkm
    }

    /**
     * Creates and calls init
     */
    static PathKeyMap2 create(Map<String, Object> sourceMap){
        def pkm = new PathKeyMap2(sourceMap)
        return pkm.init()
    }

    PathKeyMap2 pathDelimiter(String v){
        this.pathDelimiter = v
        return this
    }

    PathKeyMap2 removePathKeys(boolean v){
        this.removePathKeys = v
        return this
    }

    /**
     * Process the nested keys
     */
    PathKeyMap2 init() {
        if(initialized) return this
        //create keySet copy so we can modify as we iterate and dont get concurent modifcation exception
        var keySetCopy = wrappedMap.keySet().collect{it}
        for (String key : keySetCopy) {
            processNestedKeys(wrappedMap, key)
        }
        initialized = true
        return this
    }

    //need this, or else, groovy metaclass would call 'get' method of this class, resulting in StackOverflow error
    //See MetaClassImpl.getProperty
    Map<String, Object> getWrappedMap() {
        return this.wrappedMap //direct field access
    }

    PathKeyMap2 cloneMap() {
        if (wrappedMap.isEmpty()) {
            return PathKeyMap2.of([:], pathDelimiter, removePathKeys)
        } else {
            // Map clonedMap = Maps.clone(wrappedMap)
            Map clonedMap = new LinkedHashMap(wrappedMap)
            // deep clone nested entries
            clonedMap.keySet().each { k ->
                def val = clonedMap[k]
                //clone the nested values that are pathKeyMaps
                if (val instanceof PathKeyMap2) {
                    clonedMap[k] = (val as PathKeyMap2).cloneMap()
                }
                // if its a list of PathKeyMaps then iterate over and clone those too
                else if(val && val instanceof Collection && ((Collection)val)[0] instanceof PathKeyMap2) {
                    clonedMap[k] = val.collect{
                        (it as PathKeyMap2).cloneMap()
                    } as Collection<PathKeyMap2>
                }
            }

            return PathKeyMap2.of(clonedMap, pathDelimiter, removePathKeys)
        }
    }

    @Override
    public Object clone() {
        cloneMap()
    }

    void mergeValuesFrom(PathKeyMap2 otherMap) {
        wrappedMap.putAll((PathKeyMap2)otherMap.clone())
    }

    @Override
    Object get(Object key) {
        Object returnValue = wrappedMap.get(key)
        return returnValue
    }

    @Override
    Object put(String key, Object value) {
        //old code, maybe this is for GStrings?
        if (value instanceof CharSequence) value = value.toString()
        if (key instanceof CharSequence) key = key.toString()
        Object returnValue =  wrappedMap.put(key, value)
        if (key.indexOf(pathDelimiter) > -1) {
            processNestedKeys(this, key)
        }
        return returnValue
    }

    @Override
    Object remove(Object key) {
        return wrappedMap.remove(key)
    }

    @Override
    void putAll(Map<? extends String, ? extends Object> map) {
        for (Map.Entry<String, Object> entryObj : map.entrySet()) {
            Entry entry = (Entry)entryObj
            put(entry.getKey() as String, entry.getValue())
        }
    }


    /**
     * @return The identifier in the request
     */
    // Object getIdentifier() {
    //     return get("id")
    // }

    /*
     * Builds up a multi dimensional hash structure from the parameters so that nested keys such as
     * "book.author.name" can be addressed like params['author'].name
     *
     * This also allows data binding to occur for only a subset of the properties in the parameter map.
     */
    private void processNestedKeys(Map parentMap, String key) {
        final int nestedIndex = key.indexOf(pathDelimiter)

        //if no delimiter in the key then check value for a nested PathKeyMap.
        if (nestedIndex == -1) {
            def val = parentMap.get(key)
            //if the val is a PathKeyMap the initialize it
            if(val instanceof PathKeyMap2) val.init()
            //if its a collection and it looks like its a collection of PathKeyMaps then init each one
            if(val && val instanceof Collection && ((Collection)val)[0] instanceof PathKeyMap2) {
                val.each { ((PathKeyMap2)it).init() }
            }
        }
        //else it has at least 1 path key, `a.b` an min or `a.b.c`
        else {

            // We have at least one sub-key, so extract the first element of the nested key as the prfix.
            // In other words, if we have key == "a.b.c", the nestedPrefix is "a".
            String nestedPrefix = key.substring(0, nestedIndex)

            // Let's see if we already have a value in the current map for the prefix.
            Object prefixValue = parentMap.get(nestedPrefix)
            //if nothing then setup a new nested PathKeyMap
            if (prefixValue == null) {
                // No value. So, since there is at least one sub-key,
                // we create a sub-map for this prefix.
                prefixValue = PathKeyMap2.of([:], pathDelimiter, removePathKeys)

                parentMap.put(nestedPrefix, prefixValue)
            }

            // if its not map then blow an error as the keys are conflicting
            if (!(prefixValue instanceof Map)) {
                throw new IllegalStateException("Bad keys, expecting a map for path key ${key}")
            }

            Map nestedMap = (Map) prefixValue

            String remainderOfKey = key.substring(nestedIndex + 1, key.length())
            var parentMapValue = parentMap.get(key)
            var curVal = nestedMap.get(key)
            var oldVal = nestedMap.put(remainderOfKey, parentMapValue)

            boolean hasMorePathsInKey = remainderOfKey.contains(pathDelimiter)
            //if it doesnt have more and the old value it replaced was a map then error
            //if we have something like [a: 'foo', a.b: 'bar'] then its not valid as a both has a value and is a map.
            //at this point if the remainderOfKey has delimeters and it has an oldVal then it should fail
            if (!hasMorePathsInKey && oldVal instanceof Map) {
                throw new IllegalStateException("Bad keys")
            }

            //if its a PathKeyMap then this processNestedKeys will have already been triggered in the put,
            // but if its just a normal Map then we need to trigger it here
            if (!(nestedMap instanceof PathKeyMap2) && hasMorePathsInKey) {
                processNestedKeys((Map) nestedMap, remainderOfKey)
            }

            //remove the orig pathkey with the dot in it
            if (removePathKeys) parentMap.remove(key)
        }
    }

    @Override
    boolean equals(Object that) {
        wrappedMap.equals(that)
    }

    @Override
    int hashCode() {
        int hashCode = HashCodeHelper.initHash();
        for (Map.Entry<String, Object> entry: wrappedMap.entrySet()) {
            hashCode = HashCodeHelper.updateHash(hashCode, entry);
        }
        return hashCode;
    }

    /**
     * Helper method for obtaining a list of values from parameter
     * @param name The name of the parameter
     * @return A list of values
     */
    List getList(String name) {
        Object paramValues = get(name);
        if (paramValues == null) {
            return Collections.emptyList();
        }
        if (paramValues.getClass().isArray()) {
            return Arrays.asList((Object[])paramValues);
        }
        if (paramValues instanceof Collection) {
            return new ArrayList((Collection)paramValues);
        }
        return Collections.singletonList(paramValues);
    }

    List list(String name) {
        return getList(name);
    }

    @Override
    int size() {
        return wrappedMap.size();
    }

    @Override
    boolean isEmpty() {
        return wrappedMap.isEmpty();
    }

    @Override
    boolean containsKey(Object k) {
        return wrappedMap.containsKey(k);
    }

    @Override
    boolean containsValue(Object v) {
        return wrappedMap.containsValue(v);
    }

    @Override
    void clear() {
        wrappedMap.clear();
    }

    @Override
    Set keySet() {
        return wrappedMap.keySet();
    }

    @Override
    Collection values() {
        return wrappedMap.values();
    }

    @Override
    Set entrySet() {
        return wrappedMap.entrySet();
    }

    @Override
    String toString() {
        return this.toMapString()
    }
}
