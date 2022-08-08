/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

import yakworks.message.Msg
import yakworks.message.MsgKey
import yakworks.message.MsgKeyDecorator

/**
 * a fully writable Result fluent/chainable methods with Generics for a Results
 *
 * @author Joshua Burnett (@basejump)
 */
@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
interface GenericResult<E: GenericResult<E>?> : Result {

    /** title is really the only required ones to implements */
    override var title: String?
        get() = null
        set(v) { noImpl() }

    override var payload: Any?
        get() = null
        set(v) { noImpl() }

    override var detail: String?
        get() = null
        set(v) { noImpl() }

    override var status: ApiStatus
        get() = HttpStatus.OK
        set(value) { noImpl() }

    fun title(v: String?): E    = apply { title = v } as E
    fun detail(v: String?): E   = apply { detail = v } as E
    fun status(v: ApiStatus): E = apply { status = v } as E
    fun status(v: Int): E       = apply { status = HttpStatus.valueOf(v) } as E
    fun payload(v: Any?): E     = apply { payload = v } as E

    /** optional default code */
    val defaultCode: String?
        get() = null

    // --- message key and code fluent methods ---
    fun msg(v: MsgKey): E       = apply { msg = v } as E

    /**
     * msg from code
     */
    fun msg(v: String): E {
        if (msg == null) msg = Msg.key(v) else msg!!.code = v
        return this as E
    }
    fun msg(v: String, args: Any?): E {
        msg = Msg.key(v, args)
        return this as E
    }
}
