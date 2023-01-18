/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.codehaus.groovy.runtime.InvokerHelper
import yakworks.message.MsgServiceRegistry
import yakworks.message.spi.MsgService
import kotlin.reflect.KClass

/** package level func to fire exception */
fun noImpl(){
    throw NotImplementedError()
}

object ResultSupport {

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

    @JvmStatic
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

    fun isBasicType(value: Any, acceptedTypes: List<KClass<out Comparable<*>>>): Boolean {
        return value::class in acceptedTypes
    }

    /**
     * Uses code and args to do look up for message
     * @param msgService the MsgService
     * @param result the result to use for keys
     * @return the message
     */
    @JvmStatic
    fun getMessage( msgService: MsgService, result: Result): String?{
        var message: String? = null
        if(result.msg != null) message = msgService.get(result.msg)

        if(message.isNullOrEmpty() && !result.title.isNullOrEmpty()){
            message = result.title
        }
        return message
    }

    @JvmStatic
    fun resultToStringCommon( p: Result): String {
        val title = if(!p.title.isNullOrEmpty()) "title=${p.title}" else null
        val code = if(!p.code.isNullOrEmpty()) "code=${p.code}" else null
        val payload = p.payload
        val value = if(payload != null && isBasicType(payload, acceptedTypes) ) "payload=${p.payload}" else null
        val status = p.status.code
        return listOf(title, code, value, status)
            .filter { it != null }
            .map{ it.toString() }
            .joinToString()
    }

    @JvmStatic
    fun resultToString( r: Result): String {
        val concat = "ok=${r.ok}, " + resultToStringCommon(r)
        val cname = r::class.simpleName
        return "${cname}(${concat})"
    }

    @JvmStatic
    fun hasProp(obj: Any, name: String): Boolean{
        return InvokerHelper.getMetaClass(obj).hasProperty(obj, name) != null
    }

    @JvmStatic
    fun getProp(obj: Any, name: String): Any? {
        return InvokerHelper.getProperty(obj, name)
    }

    @JvmStatic
    fun addCommonArgs(args: MutableMap<String, Any?>, entity: Any): Map<*, *>? {
        args.putIfAbsent("name", entity.javaClass.getSimpleName())
        if (hasProp(entity, "id")) {
            val id = getProp(entity, "id")
            if(id != null) args.putIfAbsent("id", id)
        }
        if (hasProp(entity, "stamp")) {
            args.putIfAbsent( "stamp", getProp(entity, "stamp"))
        }
        return args
    }



}
