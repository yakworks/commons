/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.meta

import java.util.concurrent.ConcurrentHashMap

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import yakworks.commons.lang.PropertyTools

/**
 * Builder to create MetaMapIncludes from a sql select like list against an entity
 */
@Slf4j
@CompileStatic
@SuppressWarnings('InvertedIfElse')
class MetaMapIncludesBuilder {
    /**
     * Holds the list of fields that have display:false for a class, meaning they should not be exported
     */
    static final Map<String, Set<String>> BLACKLIST = new ConcurrentHashMap<String, Set<String>>()

    List<String> includes
    List<String> excludes = []
    String className
    Class clazz
    MetaMapIncludes metaMapIncludes

    MetaMapIncludesBuilder(Class clazz){
        this.clazz = clazz
        this.className = clazz.name
        init()
    }

    MetaMapIncludesBuilder(String className, List<String> includes){
        this.className = className
        this.includes = includes ?: ['*'] as List<String>
        init()
    }

    MetaMapIncludesBuilder includes(List<String> includes) {
        this.includes = includes ?: ['*'] as List<String>
        return this
    }

    MetaMapIncludesBuilder excludes(List<String> val) {
        if(val != null) this.excludes = val
        return this
    }

    void init(){
        if(!clazz && className) {
            ClassLoader classLoader = getClass().getClassLoader()
            clazz = classLoader.loadClass(className)
        }

        this.metaMapIncludes = new MetaMapIncludes(clazz)
    }

    static MetaMapIncludesBuilder of(Class entityClass){
        return new MetaMapIncludesBuilder(entityClass)
    }

    static MetaMapIncludes build(Class clazz, List<String> includes){
        return MetaMapIncludesBuilder.of(clazz).includes(includes).build()
    }

    static MetaMapIncludes build(String entityClassName, List<String> includes){
        def mmib = new MetaMapIncludesBuilder(entityClassName, includes)
        return mmib.build()
    }

    /**
     * builds a EntityMapIncludes object from a sql select like list. Used in EntityMap and EntityMapList
     *
     * @param className the class name of the PersistentEntity
     * @param includes the includes list in our custom dot notation
     * @return the EntityMapIncludes object that can be passed to EntityMap
     */
    MetaMapIncludes build() {

        Set<String> rootProps = [] as Set<String>
        Map<String, Object> nestedProps = [:]

        for (String field : includes) {
            field = field.trim()
            Integer nestedIndex = field.indexOf('.')
            //if its has a dot then its an association, so grab the first part
            // for example if this is foo.bar.baz then this sets nestedPropName = 'foo'
            String nestedPropName = nestedIndex > -1 ? field.substring(0, nestedIndex) : null

            // if it is a nestedPropName then if it does NOT exist, continue
            if(nestedPropName && !propertyExists(nestedPropName)){
                continue
            }

            //no dot then its just a property or its the * or $stamp
            if (!nestedPropName) {
                MetaProp propm = getMetaProp(field)
                if(propm){
                    metaMapIncludes.propsMap[field] = propm
                }
            }
            else { // its a nestedProp
                //we are sure its exists at this point as we alread checked above
                // metaMapIncludes.propsMap[nestedPropName] = null

                //set it up if it has not been yet
                if (!nestedProps[nestedPropName]) {
                    MetaBeanProperty mprop = PropertyTools.getMetaBeanProp(clazz, nestedPropName)
                    String nestedClass = mprop.type.name
                    Map<String, Object> initMap = ['className': nestedClass, 'props': [] as Set]
                    nestedProps[nestedPropName] = initMap
                }
                //if prop is foo.bar.baz then this get the bar.baz part
                String propPath = field.substring(nestedIndex + 1)

                def initMap = nestedProps[nestedPropName] as Map<String, Object>
                (initMap['props'] as Set).add(propPath)
            }
        }
        //create the includes class for what we have now along with the the blacklist
        Set blacklist = getBlacklist(null) + (this.excludes as Set)

        //only if it has rootProps
        if (metaMapIncludes.propsMap) {
            if(blacklist) metaMapIncludes.addBlacklist(blacklist)
            //if it has nestedProps then go recursive
            if(nestedProps){
                buildNested(nestedProps)
            }
            return metaMapIncludes
        } else {
            return null
        }
    }

    /** PropMeta from propName depending on whether its a persistentEntity or normal bean */
    MetaProp getMetaProp(String propName){
        def mprop = PropertyTools.getMetaBeanProp(clazz, propName)
        if(mprop) return new MetaProp(mprop)

        return null
    }

    boolean propertyExists(String propName){
        def prop = PropertyTools.getMetaBeanProp(clazz, propName)
        return prop
    }

    //will recursivily call build and add to the metaMapIncludes
    MetaMapIncludes buildNested(Map<String, Object> nestedProps){

        // now we cycle through the nested props and recursively call this again for each associations includes
        Map<String, MetaMapIncludes> nestedIncludesMap = [:]
        for (entry in nestedProps.entrySet()) {
            String prop = entry.key as String //the nested property name
            Map initMap = entry.value as Map
            List incProps = initMap['props'] as List
            String assocClass = initMap['className'] as String
            MetaMapIncludes nestedIncludes

            if(assocClass) {
                nestedIncludes = build(assocClass, incProps)
            }
            // if no class then it wasn't a gorm association or gorm prop didn't have type
            // so try by getting value through meta reflection
            else {
                ClassLoader classLoader = getClass().getClassLoader()
                Class entityClass = classLoader.loadClass(className)
                Class returnType = PropertyTools.getPropertyReturnType(entityClass, prop)
                //if returnType is null at this point then the prop is bad or does not exist.
                //we allow bad props and just continue.
                if(returnType == null) {
                    continue
                }
                // else if its a collection
                else if(Collection.isAssignableFrom(returnType)){
                    String genClass = PropertyTools.findGenericForCollection(entityClass, prop)
                    if(genClass) {
                        nestedIncludes = MetaMapIncludesBuilder.build(genClass, incProps)
                    }
                    //TODO shouldn't we do at leas na object here? should not matter
                } else {
                    nestedIncludes = MetaMapIncludesBuilder.build(returnType.name, incProps)
                }
            }
            //if it got valid nestedIncludes and its not already setup
            if(nestedIncludes && !nestedIncludesMap[prop]) nestedIncludesMap[prop] = nestedIncludes
        }

        if(nestedIncludesMap) {
            metaMapIncludes.propsMap.putAll(nestedIncludesMap)
        }

        return metaMapIncludes
    }

    static Set<String> getBlacklist(Object entity){
        return [] as Set<String>
    }


}
