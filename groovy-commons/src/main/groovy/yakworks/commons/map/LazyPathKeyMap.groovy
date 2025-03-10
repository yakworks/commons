/*
* Copyright 2023 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.map


import groovy.transform.CompileStatic

/**
 * Loosely based on concepts from org.apache.groovy.json.internal.LazyMap
 * Allows a flattened map of path keys such that
 * foo.bar.id:1, foo.amount:10 would end up as [foo: [bar: [id: 1]], amount:10]
 * Useful for CSV reading too.
 */
@CompileStatic
public class LazyPathKeyMap extends AbstractMap<String, Object> implements Serializable {

    /** the delimiter, defaults to '.' can be changed to somthing like _ so would break apart fields like foo_bar*/
    String pathDelimiter = '.'

    /** defaults to false, whether to remove the orginal path key after its been nested into maps */
    //boolean removePathKeys = true

    /** TODO (MAKE THIS WORK) When set to false will keep the Map flat */
    //boolean enabled = true

    /* Holds the actual backing map that will be lazily created. */
    protected Map<String, Object> map;

    /* Holds the source map with the path keys */
    private Map<String, Object> sourceMap;

    private boolean initialized = false

    /**
     * Populates the PathKeyMap with supplied map.
     *
     * @param values The values to populate with
     */
    LazyPathKeyMap(Map<String, Object> sourceMap) {
        this.sourceMap = sourceMap ?: ([:] as Map<String, Object>)
    }

    static LazyPathKeyMap of(Map<String, Object> sourceMap, String pathDelimiter = '.' ){
        def pkm = new LazyPathKeyMap(sourceMap)
        pkm.pathDelimiter = pathDelimiter
        return pkm
    }

    LazyPathKeyMap pathDelimiter(String v){
        this.pathDelimiter = v
        return this
    }

    public Map<String, Object> getSourceMap(){
        return this.sourceMap
    }

    /** used to make tests easier and to do asserts when we only want to operate on the sourceMap */
    boolean isInitialized(){
        return this.initialized
    }

    @Override
    public Object put(String key, Object value) {
        //if its a map then build it first so it can go into the normal map.put and replace otherwise it ends up getting merged
        if(value instanceof Map) buildIfNeeded()
        if (map == null) {
            return sourceMap.put(key, value)
        } else {
            return map.put(key, value)
        }
    }

    /** call Maps.putValue */
    public void putByPath(String key, Object value) {
        if (map == null) {
            sourceMap.put(key, value)
        } else {
            Maps.putValue(map, key, value, pathDelimiter)
        }
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        buildIfNeeded();
        return map.entrySet();
    }

    @Override
    public int size() {
        if (map == null) {
            return sourceMap.size()
        } else {
            return map.size()
        }
    }

    @Override
    public boolean isEmpty() {
        if (map == null) {
            return sourceMap.isEmpty();
        } else {
            return map.isEmpty();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        buildIfNeeded();
        return map.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        buildIfNeeded();
        return map.containsKey(key);
    }

    @Override
    public Object get(Object key) {
        buildIfNeeded();
        return map.get(key);
    }

    public void buildIfNeeded() {
        if (map == null) {
            map = new LinkedHashMap<String, Object>(sourceMap.size(), 0.01f);
            //create keySet copy so we can modify as we iterate and dont get concurent modifcation exception
            var keySetCopy = sourceMap.keySet().collect() //copy
            for (String key : keySetCopy) {
                var sval = sourceMap[key]
                if(sval instanceof LazyPathKeyMap){
                    sval.buildIfNeeded()
                    putByPath(key, sval)
                }
                else if(sval instanceof Map){
                    // put a dummy ~~MERGE~~ key in to intialize the nested maps.
                    Map mres = Maps.putValue(map, "${key}${pathDelimiter}~~MERGE~~", "MERGE_ME", pathDelimiter)
                    //now remove it
                    mres.remove("~~MERGE~~")
                    //merge in the map now
                    Maps.merge(mres, LazyPathKeyMap.of(sval as Map, pathDelimiter) )
                } else {
                    //key will probably have a dot
                    putByPath(key, sval)
                }
            }
            sourceMap = null
            initialized = true
        }
    }

    @Override
    public Object remove(Object key) {
        buildIfNeeded();
        return map.remove(key);
    }

    @Override
    public void putAll(Map m) {
        buildIfNeeded()
        map.putAll(m)
    }

    @Override
    public void clear() {
        if (map == null) {
            sourceMap.clear()
        } else {
            map.clear()
        }
    }

    @Override
    public Set<String> keySet() {
        buildIfNeeded()
        return map.keySet()
    }

    @Override
    public Collection<Object> values() {
        buildIfNeeded()
        return map.values()
    }

    @Override
    public boolean equals(Object o) {
        buildIfNeeded()
        return map.equals(o)
    }

    @Override
    public int hashCode() {
        buildIfNeeded()
        return map.hashCode()
    }

    @Override
    public String toString() {
        buildIfNeeded()
        return map.toString()
    }

    @Override
    Object clone() throws CloneNotSupportedException {
        cloneMap()
    }

    /** deep clones this */
    Map<String, Object>  cloneMap() {
        if (map == null) {
            Map clonedMap = new LinkedHashMap(sourceMap)
            clonedMap?.keySet().each { k ->
                def val = clonedMap[k]
                //clone the nested values that are LazyPathKeyMaps
                if (val instanceof LazyPathKeyMap) {
                    clonedMap[k] = (val as LazyPathKeyMap).clone()
                }
                // if its a list of PathKeyMaps then iterate over and clone those too
                else if(val && val instanceof Collection && ((Collection)val)[0] instanceof LazyPathKeyMap) {
                    clonedMap[k] = val.collect {
                        (it as LazyPathKeyMap).clone()
                    } as Collection<LazyPathKeyMap>
                }
            }
            return LazyPathKeyMap.of(clonedMap, pathDelimiter)
        } else {
            return Maps.clone(map) as Map<String, Object>
        }
    }

}
