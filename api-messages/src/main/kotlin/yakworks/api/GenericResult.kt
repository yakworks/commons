/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

import yakworks.message.MsgKey
import yakworks.message.MsgKeyDecorator

/**
 * The fluent/chainable methods with Generics for a Results
 *
 * @author Joshua Burnett (@basejump)
 */
@Suppress("UNCHECKED_CAST")
interface GenericResult<E: GenericResult<E>?> : Result {
    fun title(v: String?): E    = apply { title = v } as E
    fun status(v: ApiStatus): E = apply { status = v } as E
    fun status(v: Int): E       = apply { status = HttpStatus.valueOf(v) } as E
    fun payload(v: Any?): E     = apply { payload = v } as E
    fun msg(v: MsgKey): E       = apply { msg = v } as E

    /**
     * msg from code
     */
    fun msg(v: String): E {
        if (msg == null) msg = MsgKey.ofCode(v) else msg!!.code = v
        return this as E
    }
    fun msg(v: String, args: Any?): E {
        msg = MsgKey.of(v, args)
        return this as E
    }
}
