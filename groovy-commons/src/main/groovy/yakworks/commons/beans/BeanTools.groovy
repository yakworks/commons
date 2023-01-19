/*
* Copyright 2019 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.beans

import groovy.transform.CompileStatic

import org.codehaus.groovy.runtime.InvokerHelper

import yakworks.commons.map.Maps
import yakworks.json.JacksonUtils

/**
 * helpers for Plain Old Groovy Objects and Beans
 *
 * @author Joshua Burnett (@basejump)
 */
@CompileStatic
class BeanTools {

    /**
     * shorter and more semanticly correct alias to getProperty
     */
    static Object value(Object source, String property) {
        PropertyTools.getProperty(source, property)
    }


    /**
     * Uses built in Groovy for Simple merging from a map to object, nested or not, onto the pogo.
     * Uses the InvokerHelper.setProperties(values)
     * Works well for most simple cases but does handle binding of a list of generics.
     * Use Jackson for those cases, see the bind method here which does it.
     *
     * @see JacksonJson#bind
     */
    static Object merge( Object pogo, Map values, boolean ignoreNulls = true){
        if(ignoreNulls){
            values = Maps.prune(values)
        }
        return setProps(pogo, values)
    }

    /**
     * sets the properties in taget from the source
     */
    public static <T> T setProps(T target, Object source){
        Map propsToMerge = source instanceof Map ? source : source.properties
        InvokerHelper.setProperties(target, propsToMerge)
        return target
    }

    /**
     * alias to setProps
     */
    public static <T> T copy(T target, Object source){
        return setProps(target, source)
    }

    /**
     * Uses JacksonUtil to binds the data to new instance of the pased in Class clazz
     */
    public static <T> T bind(Object data, Class<T> clazz)  {
        return JacksonUtils.bind(data, clazz)
    }

    /**
     * Uses JacksonUtil to binds the data to new instance of the pased in Class clazz
     * TODO what happens on an update
     */
    public static <T> T bind(T instance, Object data) {
        return JacksonUtils.bindUpdate(instance, data)
    }

}
