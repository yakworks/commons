/*
* Copyright 2019 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.meta

import java.util.concurrent.ConcurrentHashMap

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import yakworks.commons.lang.PropertyTools

/**
 * Builder to create MetaEntity from a sql select like list against an entity
 */
@Slf4j
@CompileStatic
@SuppressWarnings('InvertedIfElse')
class BasicMetaEntityBuilder {
    /**
     * Holds the list of fields that have display:false for a class, meaning they should not be exported
     */
    static final Map<String, Set<String>> BLACKLIST = new ConcurrentHashMap<String, Set<String>>()

    List<String> includes
    List<String> excludes = []
    String className
    Class clazz
    MetaEntity metaEntity

    BasicMetaEntityBuilder(Class clazz){
        this.clazz = clazz
        this.className = clazz.name
        init()
    }

    BasicMetaEntityBuilder(String className, List<String> includes){
        this.className = className
        this.includes = includes ?: ['*'] as List<String>
        init()
    }

    BasicMetaEntityBuilder includes(List<String> includes) {
        this.includes = includes ?: ['*'] as List<String>
        return this
    }

    BasicMetaEntityBuilder excludes(List<String> val) {
        if(val != null) this.excludes = val
        return this
    }

    void init(){
        if(!clazz && className) {
            ClassLoader classLoader = getClass().getClassLoader()
            clazz = classLoader.loadClass(className)
        }

        this.metaEntity = new MetaEntity(clazz)
    }

    static BasicMetaEntityBuilder of(Class entityClass){
        return new BasicMetaEntityBuilder(entityClass)
    }

    static MetaEntity build(Class clazz, List<String> includes){
        return BasicMetaEntityBuilder.of(clazz).includes(includes).build()
    }

    static MetaEntity build(String entityClassName, List<String> includes){
        def mmib = new BasicMetaEntityBuilder(entityClassName, includes)
        return mmib.build()
    }

    /**
     * builds a EntityMapIncludes object from a sql select like list. Used in EntityMap and EntityMapList
     *
     * @param className the class name of the PersistentEntity
     * @param includes the includes list in our custom dot notation
     * @return the EntityMapIncludes object that can be passed to EntityMap
     */
    MetaEntity build() {

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
                    metaEntity.metaProps[field] = propm
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
        if (metaEntity.metaProps) {
            if(blacklist) metaEntity.addBlacklist(blacklist)
            if(includes) metaEntity.includes = includes as Set<String>
            //if it has nestedProps then go recursive
            if(nestedProps){
                buildNested(nestedProps)
            }
            return metaEntity
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
    MetaEntity buildNested(Map<String, Object> nestedProps){

        // now we cycle through the nested props and recursively call this again for each associations includes
        Map<String, MetaEntity> nestedMetaEntityMap = [:]
        for (entry in nestedProps.entrySet()) {
            String prop = entry.key as String //the nested property name
            Map initMap = entry.value as Map
            List incProps = initMap['props'] as List
            String assocClass = initMap['className'] as String
            MetaEntity nestedMetaEntity

            if(assocClass) {
                nestedMetaEntity = build(assocClass, incProps)
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
                        nestedMetaEntity = BasicMetaEntityBuilder.build(genClass, incProps)
                    }
                    //TODO shouldn't we do at leas na object here? should not matter
                } else {
                    nestedMetaEntity = BasicMetaEntityBuilder.build(returnType.name, incProps)
                }
            }
            //if it got valid nestedIncludes and its not already setup
            if(nestedMetaEntity && !nestedMetaEntityMap[prop]) nestedMetaEntityMap[prop] = nestedMetaEntity
        }

        if(nestedMetaEntityMap) {
            metaEntity.metaProps.putAll(nestedMetaEntityMap)
        }

        return metaEntity
    }

    static Set<String> getBlacklist(Object entity){
        return [] as Set<String>
    }


}
