/*
* Copyright 2020 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.meta

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.codehaus.groovy.util.HashCodeHelper

import yakworks.commons.lang.NameUtils
import yakworks.commons.map.MapFlattener

/**
 * Somewhat similiar to MetaClass.
 * This represents either the root object of the MetaMap or a property that is an object and has its own set of includes.
 * For exammple if Customer is the root object it might have an Address property that will be repesented with this.
 *
 * Customer  -> MetaEntity
 *   name    -> MetaProp
 *   address -> MetaEntity
 *     city  -> MetaProp
 *     etc....
 *
 * @see MetaProp
 */
@Slf4j
@CompileStatic
class MetaEntity extends MetaProp implements Serializable {

    //either a simple MetaProp or a ref to another MetaEntityProps
    Map<String, MetaProp> metaProps = [:] as Map<String, MetaProp>

    Set<String> excludeFields

    //if any special converters then can be set here and the MetaMap will get them
    public static Set<MetaMap.Converter> CONVERTERS = [] as Set<MetaMap.Converter>

    static {
        ServiceLoader<MetaMap.Converter> loader = ServiceLoader.load(MetaMap.Converter)
        for (MetaMap.Converter converter : loader) {
            CONVERTERS.add(converter)
        }

    }

    MetaEntity(){}

    MetaEntity(Class type) {
        super(NameUtils.getShortName(type.name), type)
    }

    MetaEntity(String name, Class type) {
        super(name, type)
    }

    static MetaEntity of(List<String> fields){
        def mmi = new MetaEntity()
        fields.each { mmi.metaProps[it] = new MetaProp(it, null) }
        return mmi
    }

    /**
     * Filters the props to only the ones that are association and have a nested includes
     */
    Map<String, MetaEntity> getNestedIncludes(){
        return metaProps.findAll {  it.value instanceof MetaEntity } as Map<String, MetaEntity>
    }

    /**
     * Filters the props to only the ones that dont have nested includes, basic types.
     */
    Set<String> getBasicIncludes(){
        return metaProps.findAll{ !(it.value instanceof MetaEntity) }.keySet() as Set<String>
    }

    /**
     * gets the class name with out prefix so can lookup the openapi schema
     */
    String getShortClassName(){
        return NameUtils.getShortName(className)
    }

    void addBlacklist(Set<String> excludeFields) {
        this.excludeFields = excludeFields
        this.metaProps.keySet().removeAll(excludeFields)
    }

    /**
     * merges another MetaEntity fields and nested includes
     */
    void merge(MetaEntity toMerge) {
        this.metaProps.putAll(toMerge.metaProps)
        // if(toMerge.nestedIncludes) this.nestedIncludes.putAll(toMerge.nestedIncludes)
    }

    /**
     * convert to map of maps to use for flatting
     */
    Map<String, Object> toMap() {
        Map mmiProps = [:] as Map<String, Object>
        for (String key in metaProps.keySet()) {
            def val = metaProps[key]
            if(val instanceof MetaEntity) {
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
        if (other instanceof MetaEntity) {
            return other.className == className && other.metaProps == metaProps
        }
        return false
    }
    @Override
    int hashCode() {
        int hashCode = HashCodeHelper.initHash()
        if (className) { hashCode = HashCodeHelper.updateHash(hashCode, className) }
        if (metaProps) { hashCode = HashCodeHelper.updateHash(hashCode, metaProps) }
        hashCode
    }
}