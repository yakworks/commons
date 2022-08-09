/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

import yakworks.message.MsgServiceRegistry
import yakworks.message.spi.MsgService
import java.lang.UnsupportedOperationException
import kotlin.reflect.KClass

/** package level func to fire exception */
fun noImpl(){
    throw NotImplementedError()
}

internal object ResultSupport {

    /**
     * converts to Map, helpfull for to json and can be overriden on concrete impls
     * Title will be the lookup on the code using the MsgServiceRegistry.service.getMessage.
     * If no code or message returns null then it keeps the title that was set.
     */
    @JvmStatic
    fun toMap(res: Result): Map<String, Any?> {
        val hmap: MutableMap<String, Any?> = LinkedHashMap()
        hmap["ok"] = res.ok
        hmap["status"] = res.status.code
        hmap["code"] = res.code
        hmap["title"] = getTitle(res)

        // if payload is basic or collection type (not an object) then add it.
        // FIXME need better way to tweak this. maybe look up the "converters" and only do it then?
        val payload = res.payload
        if(payload != null ){
            val payloadClass = payload::class.java
            if(isBasicType(payload::class, acceptedTypes) || Map::class.java.isAssignableFrom(payloadClass)
                || Collection::class.java.isAssignableFrom(payloadClass) ){

                hmap["payload"] = res.payload
            }
        }

        return hmap
    }

    fun getTitle(result: Result): String?{
        var title: String? = result.title
        if(MsgServiceRegistry.service != null){
            title = getMessage(MsgServiceRegistry.service!!, result)
        }
        return title
    }

    val acceptedTypes: List<KClass<out Comparable<*>>> =
        listOf(Boolean::class, Int::class, String::class,
            Byte::class, Short::class, Long::class,
            Float::class, Double::class, Character::class, Character::class)

    fun isBasicType(value: Any, acceptedTypes: List<KClass<out Comparable<*>>>):
        Boolean = value::class in acceptedTypes

    /**
     * Uses code and args to do look up for message
     * @param msgService the MsgService
     * @param result the result to use for keys
     * @return the message
     */
    fun getMessage( msgService: MsgService, result: Result): String?{
        var message: String? = null
        if(result.msg != null) message = msgService.get(result.msg)

        if(message.isNullOrEmpty() && !result.title.isNullOrEmpty()){
            message = result.title
        }
        return message
    }
    /**
     * Uses code and args to do look up for message
     * @param msgService the MsgService
     * @param result the result to use for keys
     * @return the message
     */
    // static String getMessage(MsgService msgService, Result result){
    //     String message = null;
    //     if(result.getMsg() != null) message = msgService.get(result.getMsg());
    //
    //     if(message != null && message != ""){
    //         if(result.getTitle() != null) {
    //             message = result.getTitle();
    //         } else if(result instanceof ApiResults && ((List<?>)result).size() != 0) {
    //             //use msg form first item
    //             message = msgService.get(((List<?>)result)[0].msg);
    //         }
    //     }
    //
    //     return message
    // }
    // protected static final List BASIC_TYPES = [
    //     String, Boolean, Byte, Short, Integer, Long, Float, Double, Character
    // ] as List<Class>
    //
    // protected static final List COLLECTION_TYPES = [
    //     Map, Collection
    // ] as List<Class>
    /**
     * checks if Class is basic type (String, long/Long, boolean/Boolean, etc...)
     */
    // static boolean isBasicType(Class c) {
    //     if(c == null) return false
    //     return BASIC_TYPES.contains(c) || c.isPrimitive()
    // }
}
