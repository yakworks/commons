/*
* Copyright 2019 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.beans

import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import yakworks.commons.lang.ClassUtils
import yakworks.commons.lang.NameUtils
import yakworks.commons.lang.Validate

/**
 * PropertyTools contains a set of static helpers, which provides a convenient way
 * for manipulating the object's properties.
 *
 * For example, it allows to retrieve object's properties using filters and place them in a map.
 */
@Slf4j
@CompileStatic
class PropertyTools {

    /**
     * shorter and more semanticly correct alias to getProperty
     */
    static Object value(Object source, String property) {
        getProperty(source, property)
    }

    /**
     * just uses groovy getAt but wraps MissingPropertyException so if it doesnt exist then returns null
     */
    static Object getOrNull(Object source, String property) {
        if (source == null) return null
        Object value
        try {
            value = source[property]
        }
        catch (MissingPropertyException e) {
            // swallow the exceptin basically, if obj is a map then this never happens, but if prop doesn't exist
            // then this get thrown for objects
            value = null
        }
        return value
    }

    /**
     * Return the value of the (probably nested if your using this) property of the specified name, for the specified source object
     *
     * Example getProperty(source, "x.y.z")
     *
     * @param source - The source object
     * @param property - the property
     * @return value of the specified property or null if any of the intermediate objects are null
     */
    static Object getProperty(Object source, String property) {
        Validate.notNull(source, '[source]')
        Validate.notEmpty(property, '[property]')

        Object result

        if(property.contains('.')) {
            result = property.tokenize('.').inject(source) { Object obj, String prop ->
                Object value = getOrNull(obj, prop)
                return value
            }
        }
        else {
            result = getOrNull(source, property)
        }

        return result
    }

    /**
     * Return the property value associated with the given key, or {@code null}
     * if the key cannot be resolved.
     * @param key the property name to resolve
     * @param targetType the expected type of the property value
     */
    static <T> T getProperty(Object source, String path, Class<T> targetType){
        getProperty(source, path) as T
    }

    /**
     * Return the property value associated with the given key, or defaultValue
     * if the key cannot be resolved.
     * @param key the property name to resolve
     * @param targetType the expected type of the property value
     */
    static <T> T getProperty(Object source, String path, Class<T> targetType, T defaultValue) {
        Object value = getProperty(source, path)
        return (value == null ? defaultValue : value ) as T
    }

    /**
     * Returns the deepest nested bean
     */
    static getNestedBean(Object bean, String path) {
        int i = path.lastIndexOf(".")
        if (i > -1) {
            path = path.substring(0, i)
            path.split('\\.').each { String it -> bean = bean[it] }
        }
        return bean
    }

    /**
     * sets the property based on the path. can be a simple "someField" or contain dots for nested path like "foo.bar.someField"
     * @param object the object to navigate and set
     * @param propertyPath the field or path to set
     * @param value the val to set
     */
    static void setProp(Object object, String propertyPath, Object value) {
        def pathElements = propertyPath.tokenize('.')
        String objPath = pathElements[0..-2].join('.')
        Object parent = getProperty(object, objPath)
        if(parent == null) throw new IllegalArgumentException("Result of path to [${objPath}] is null")
        parent[pathElements[-1]] = value
    }

    /**
     * finds the property in an entity class and returns is MetaBeanProperty which is useful for
     * things like getting the return type
     * @param clazz the the class to look in
     * @param prop the property name
     * @return the type Class or null if non found
     */
    static MetaBeanProperty getMetaBeanProp(Class clazz, String prop) {
        return clazz.metaClass.properties.find{ it.name == prop} as MetaBeanProperty
    }

    /**
     * see getMetaBeanProp, this calls that and returns the getter MetaMethod's returnType
     * @param clazz the the class to look in
     * @param prop the property name
     * @return the type Class or null if non found
     */
    static Class getPropertyReturnType(Class clazz, String prop){
        return getMetaBeanProp(clazz, prop)?.getter?.returnType
    }

    /**
     * Trys to find the generic type for a collection property
     * For example if its a List<Foo> the this will return 'x.y.Foo' assuming its in the x.y package
     *
     * @param clazz the class to look on
     * @param prop the class property to check
     * @return the generic class name or implies 'java.lang.Object' if no generic found
     * @see #findGenericTypeForCollection
     */
    static String findGenericForCollection(Class clazz, String prop){
        return findGenericTypeForCollection(clazz, prop).typeName
    }

    static Type findGenericTypeForCollection(Class clazz, String prop){
        Method[] allMethods = clazz.getDeclaredMethods()
        String getterName = NameUtils.getGetterName(prop)

        //defaults to java.lang.Object
        Type type = Object

        Method m = allMethods.find { it.name == getterName}

        if(m){
            def genericReturnType = m.getGenericReturnType()
            if(genericReturnType && genericReturnType instanceof ParameterizedType){
                Type[] actualTypeArguments = genericReturnType.getActualTypeArguments()
                return actualTypeArguments[0]
            }
        }
        return type
    }

    /**
     * @see yakworks.commons.lang.ClassUtils#setFieldValue
     */
    static void setFieldValue(Object instance, String fieldName, Object value){
        ClassUtils.setFieldValue(instance, fieldName, value)
    }
}
