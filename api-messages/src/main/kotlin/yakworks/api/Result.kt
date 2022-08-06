/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

import yakworks.message.MsgKey
import yakworks.message.MsgKeyDecorator

/**
 * This is the base result trait for problems and results
 * follows https://datatracker.ietf.org/doc/html/rfc7807 for status and title fields
 *
 * In many cases for parallel processing and batch processing we are spinning through chunks of data.
 * Especially when doing gpars and concurrent processing.
 * Java of course does not allow multi-value returns.
 * On errors and exceptions we don't want to stop or halt the processing. So many methods
 * can catch an exception and return this to contain basic status and a message of what went wrong so
 * we can be report on it, log it, etc and move on to try the next item.
 *
 * @author Joshua Burnett (@basejump)
 * @since 7.0.8
 */
//@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
@Suppress("UNUSED_PARAMETER")
interface Result : MsgKeyDecorator, AsMap {
    /** success or fail? if ok is true then it still may mean that there are warnings and needs to be looked into */
    val ok: Boolean?
        get() = true

    /** optional default code */
    val defaultCode: String?
        get() = null

    /**
     * A short, human-readable summary of the result type. It SHOULD NOT change from occurrence to occurrence of the
     * result, except for purposes of localization (e.g., using proactive content negotiation; see [RFC7231], Section 3.4).
     * in which case code can be used for lookup and the localization with message.properties
     */
    var title: String?
        get() = null
        set(value) { noImpl() }

    /**
     * status code, normally an HttpStatus.value()
     */
    var status: ApiStatus
        get() = HttpStatus.OK
        set(value) { noImpl() }

    /**
     * the response object value or result of the method/function or process
     * Implementations might choose to ignore this in favor of concrete, typed fields.
     * Or this is generated from the target
     */
    var payload: Any?
        get() = null
        set(value) { noImpl() }

    /**
     * get the value of the payload, keeps api similiar to Optional.
     */
    fun get(): Any? {
        return payload
    }

    /**
     * converts to Map, helpfull for to json and can be overriden on concrete impls
     */
    override fun asMap(): Map<String, Any?> {
        return ResultSupport.toMap(this)
        //return ResultUtils.toMap(this)
        //return mapOf("Vanilla" to 24, "Chocolate" to 14, "Rocky Road" to 7)
    }

    companion object {
        //STATIC HELPERS
        @JvmStatic
        fun OK(): OkResult = OkResult()

        @JvmStatic
        fun ofCode(code: String): OkResult = OkResult().msg(MsgKey.of(code, null))

        @JvmStatic
        fun of(code: String, args: Any?): OkResult = OkResult().msg(MsgKey.of(code, args))

        @JvmStatic
        fun ofMsgKey(mk: MsgKey): OkResult = OkResult().msg(mk)

    }
}
