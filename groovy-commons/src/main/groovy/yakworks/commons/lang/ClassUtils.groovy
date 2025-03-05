/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.lang

import java.lang.reflect.Field
import java.lang.reflect.Modifier

import groovy.transform.CompileStatic

import org.codehaus.groovy.reflection.CachedClass
import org.codehaus.groovy.reflection.ClassInfo
import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.transform.trait.Traits

import jakarta.annotation.Nullable
import yakworks.util.ReflectionUtils

/**
 * There are 2 ClassUtils. This groovy based one and the one in yakworks.utils which is the java/spring one.
 * This is the one to mostly use.
 *
 */
@CompileStatic
@SuppressWarnings("unchecked")
class ClassUtils {

    protected static final List BASIC_TYPES = [
        String, Boolean, Byte, Short, Integer, Long, Float, Double, Character, Void
    ] as List<Class>

    /**
     * Wrapper around InvokerHelper.invokeStaticMethod, here just so we can remember it
     */
    static Object callStaticMethod(Class type, String method, Object arguments) {
        return InvokerHelper.invokeStaticMethod(type, method, arguments)
    }

    /**
     * checks if Class is basic type (String, long/Long, boolean/Boolean, etc...)
     */
    static boolean isBasicType(Class c) {
        BASIC_TYPES.contains(c) || c.isPrimitive()
    }

    static boolean isBasicType(Object o) {
        if(o == null) return false
        return isBasicType(o.class)
    }

    /**
     * simple helper to load the class from the currentThread.classLoader
     * @param clazz the class name
     * @return the loaded class
     * @throws  ClassNotFoundException
     *          If the class was not found
     */
    static Class loadClass(String clazz){
        def classLoader = Thread.currentThread().contextClassLoader
        classLoader.loadClass(clazz)
    }

    /**
     * DELEGATES TO SPRING ClassUtils
     * Replacement for {@code Class.forName()} that also returns Class instances
     * for primitives (e.g. "int") and array class names (e.g. "String[]").
     * Furthermore, it is also capable of resolving nested class names in Java source
     * style (e.g. "java.lang.Thread.State" instead of "java.lang.Thread$State").
     * @param name the name of the Class
     * @param classLoader the class loader to use
     * (may be {@code null}, which indicates the default class loader)
     * @return a class instance for the supplied name
     * @throws ClassNotFoundException if the class was not found
     * @throws LinkageError if the class file could not be loaded
     * @see Class#forName(String, boolean, ClassLoader)
     */
    public static Class<?> forName(String name, @Nullable ClassLoader classLoader)
        throws ClassNotFoundException, LinkageError {
        return yakworks.util.ClassUtils.forName(name, classLoader)
    }
    /**
     * gets the static properties from implemented traits on a class
     * @param mainClass the class to look for traits on.
     * @param name the name of the property
     * @param requiredTyped the type of the property
     * @return the list of values
     */
    public static <T> List<T> getStaticValuesFromTraits(Class mainClass, String name, Class<T> requiredTyped) {
        CachedClass cachedClass = ClassInfo.getClassInfo(mainClass).getCachedClass() //classInfo.getCachedClass()
        Collection<ClassInfo> hierarchy = cachedClass.getHierarchy()
        Class javaClass = cachedClass.getTheClass()
        List<T> values = []
        for (ClassInfo current : hierarchy) {
            def traitClass = current.getTheClass()
            def isTrait = Traits.isTrait(traitClass)
            if(!isTrait) continue
            def traitFieldName = getTraitFieldName(traitClass, name)
            T theval = getStaticPropertyValue(mainClass, traitFieldName, requiredTyped)
            if(theval){
                //println "$traitFieldName found with $theval"
                values.add(theval)
            }
        }
        Collections.reverse(values)
        return values
    }

    /**
     * trait fields get added in the form package_class__field name. this returns that
     */
    static String getTraitFieldName(Class traitClass, String fieldName) {
        return traitClass.getName().replace('.', '_') + "__" + fieldName;
    }


    public static <T> T getStaticPropertyValue(Class clazz, String name, Class<T> requiredType) {
        return returnOnlyIfInstanceOf(getStaticPropertyValue(GroovySystem.getMetaClassRegistry().getMetaClass(clazz), name), requiredType);
    }

    private static <T> T returnOnlyIfInstanceOf(Object value, Class<T> type) {
        if (value != null && (type == Object || AssignUtils.isAssignableFrom(type, value.getClass()))) {
            return (T)value;
        }
        return null;
    }

    static Object getStaticPropertyValue(Class clazz, String name) {
        return getStaticPropertyValue(clazz.metaClass, name);
    }

    static Object getStaticPropertyValue(MetaClass theMetaClass, String name) {
        MetaProperty metaProperty = theMetaClass.getMetaProperty(name);
        if(metaProperty != null && Modifier.isStatic(metaProperty.getModifiers())) {
            return metaProperty.getProperty(theMetaClass.getTheClass());
        }
        return null;
    }

    static Class<?> getPropertyType(Class<?> cls, String propertyName) {
        MetaProperty metaProperty = GroovySystem.getMetaClassRegistry().getMetaClass(cls).getMetaProperty(propertyName);
        if(metaProperty != null) {
            return metaProperty.getType();
        }
        return null;
    }

    /**
     * trickery to set a private final field
     *
     * @param clazz the class
     * @param instance the instance to set it on
     * @param fieldName the name of the field
     * @param value the value to set
     */
    static void setPrivateFinal(Class clazz, Object instance, String fieldName, Object value){
        //make the constrainedProperties accessible, remove private
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        //remove final modifier
        Field modifiersField = Field.getDeclaredField("modifiers")
        modifiersField.setAccessible(true)
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL)
        //set the value now
        field.set(instance, value)
    }

    /**
     * Cleaner trickery to set a private or final field.
     * Uses yakworks.util.ReflectionUtils to find the field, so it can be in the super class <br>
     * then will make it accesible if it needs to <br>
     * then will set the value on the field.
     *
     * @param instance the instance to set it on
     * @param fieldName the name of the field
     * @param value the value to set
     */
    static void setFieldValue(Object instance, String fieldName, Object value){
        setFieldValue(instance.getClass(), instance, fieldName, value)
    }

    /**
     * see the instance based one for docs.
     * If doesnt works by not specifying the class and letting it find the field
     * or performance is critical and your shaving yaktoseconds then use this one.
     */
    static void setFieldValue(Class<?> clazz, Object instance, String fieldName, Object value){
        //its private so set with reflection
        def field = ReflectionUtils.findField(clazz, fieldName);
        ReflectionUtils.makeAccessible(field)
        field.set(instance, value)
    }

    /**
     * Determine whether the {@link Class} identified by the supplied name is present
     * and can be loaded. Will return {@code false} if either the class or
     * one of its dependencies is not present or cannot be loaded.
     * @param className the name of the class to check
     * @param classLoader the class loader to use
     * (may be {@code null} which indicates the default class loader)
     * @return whether the specified class is present (including all of its
     * superclasses and interfaces)
     * @throws IllegalStateException if the corresponding class is resolvable but
     * there was a readability mismatch in the inheritance hierarchy of the class
     * (typically a missing dependency declaration in a Jigsaw module definition
     * for a superclass or interface implemented by the class to be checked here)
     */
    public static boolean isPresent(String className, @Nullable ClassLoader classLoader) {
        yakworks.util.ClassUtils.isPresent(className, classLoader)
    }

    /**
     * Doesnt require proxyHandler and just looks at the name.
     * - If the name has `_$$_` its java assist
     * - if it matches $HibernateProxy$ then its ByteBuddy
     * method then removes the suffixes then returns just the name.
     * if no match then it just returns the name
     */
    public static String unwrapIfProxy(String name) {
        final int proxyIndicatorJavaAssist = name.indexOf('_$$_')
        final int proxyIndicatorByteBuddy = name.indexOf('$HibernateProxy$')
        if (proxyIndicatorJavaAssist > -1) {
            name = name.substring(0, proxyIndicatorJavaAssist)
        } else if(proxyIndicatorByteBuddy > -1){
            name = name.substring(0, proxyIndicatorByteBuddy)
        }
        return name
    }

    /**
     * Doesnt require proxyHandler and just looks at the name.
     * - If the name has `_$$_` its java assist
     * - if it matches $HibernateProxy$ then its ByteBuddy
     * method then removes the suffixes then returns just the name.
     * if no match then it just returns the name
     */
    public static boolean isProxy(String name) {
        final int proxyIndicatorJavaAssist = name.indexOf('_$$_')
        final int proxyIndicatorByteBuddy = name.indexOf('$HibernateProxy$')
        return (proxyIndicatorJavaAssist > -1 || proxyIndicatorByteBuddy > -1)
    }

}
