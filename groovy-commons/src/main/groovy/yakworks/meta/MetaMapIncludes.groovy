/*
* Copyright 2020 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.meta

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Slf4j

import org.codehaus.groovy.util.HashCodeHelper

import yakworks.commons.lang.NameUtils
import yakworks.commons.map.MapFlattener

/**
 * Includes tree for root entity and nested association or object properties
 */
@Slf4j
@CompileStatic
class MetaMapIncludes extends MetaProp implements Serializable {
    //value will be null if normal prop, if association then will have another nested MetaMapIncludes
    Map<String, MetaProp> propsMap = [:] as Map<String, MetaProp>

    Set<String> excludeFields

    //if any special converters then can be set here and the MetaMap will get them
    public static Set<MetaMap.Converter> CONVERTERS = [] as Set<MetaMap.Converter>

    static {
        ServiceLoader<MetaMap.Converter> loader = ServiceLoader.load(MetaMap.Converter)
        for (MetaMap.Converter converter : loader) {
            CONVERTERS.add(converter)
        }
    }

    MetaMapIncludes(){}

    MetaMapIncludes(Class type) {
        super(NameUtils.getShortName(type.name), type)
    }

    MetaMapIncludes(String name, Class type) {
        super(name, type)
    }

    static MetaMapIncludes of(List<String> fields){
        def mmi = new MetaMapIncludes()
        fields.each { mmi.propsMap[it] = new MetaProp(it, null) }
        return mmi
    }

    /**
     * Filters the props to only the ones that are association and have a nested includes
     */
    Map<String, MetaMapIncludes> getNestedIncludes(){
        return propsMap.findAll {  it.value instanceof MetaMapIncludes } as Map<String, MetaMapIncludes>
    }

    /**
     * Filters the props to only the ones that dont have nested includes, basic types.
     */
    Set<String> getBasicIncludes(){
        return propsMap.findAll{ !(it.value instanceof MetaMapIncludes) }.keySet() as Set<String>
    }

    /**
     * gets the class name with out prefix so can lookup the openapi schema
     */
    String getShortClassName(){
        return NameUtils.getShortName(className)
    }

    void addBlacklist(Set<String> excludeFields) {
        this.excludeFields = excludeFields
        this.propsMap.keySet().removeAll(excludeFields)
    }

    /**
     * merges another MetaMapIncludes fields and nested includes
     */
    void merge(MetaMapIncludes toMerge) {
        this.propsMap.putAll(toMerge.propsMap)
        // if(toMerge.nestedIncludes) this.nestedIncludes.putAll(toMerge.nestedIncludes)
    }

    /**
     * convert to map of maps to use for flatting
     */
    Map<String, Object> toMap() {
        Map mmiProps = [:] as Map<String, Object>
        for (String key in propsMap.keySet()) {
            def val = propsMap[key]
            if(val instanceof MetaMapIncludes) {
                mmiProps[key] = val.toMap()
            } else {
                mmiProps[key] = val
            }
        }
        return mmiProps
    }

    Map<String, MetaProp> flatten() {
        Map bmap = toMap() as Map<String, Object>
        Map flatMap = MapFlattener.of(bmap).convertObjectToString(false).convertEmptyStringsToNull(false).flatten()
        return flatMap as Map<String, MetaProp>
    }

    /**
     * returns a "flattened" list of the properties with dot notation for nested.
     * so mmIncludes with ['id', 'thing':[name:""]] will return ['id', 'thing.name'] etc...
     */
    Set<String> flattenProps() {
        return flatten().keySet()
    }

    @Override
    boolean equals(Object other) {
        if (other == null) return false
        if (this.is(other)) return true
        if (other instanceof MetaMapIncludes) {
            return other.className == className && other.propsMap == propsMap
        }
        return false
    }
    @Override
    int hashCode() {
        int hashCode = HashCodeHelper.initHash()
        if (className) { hashCode = HashCodeHelper.updateHash(hashCode, className) }
        if (propsMap) { hashCode = HashCodeHelper.updateHash(hashCode, propsMap) }
        hashCode
    }
}
