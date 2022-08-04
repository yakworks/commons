/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

import groovy.transform.CompileStatic
import yakworks.message.MsgKey

/**
 * Simple OkResult with a Map as the data object
 *
 * @author Joshua Burnett (@basejump)
 */
class OkResult : Result.Fluent<OkResult> {

    override val ok: Boolean = true
    override val defaultCode: String? = null
    override var title: String? = null
    override var status: ApiStatus = HttpStatus.OK
    override var payload: Any? = null

    override var msg: MsgKey? = null
        get() {
            if(field == null) field = MsgKey.ofCode(defaultCode)
            return field
        }

    constructor() {}

    constructor(mk: MsgKey?) {
        msg = mk
    }

    companion object {
        fun ofMsg(mk: MsgKey?): OkResult {
            return OkResult(mk)
        }
    }



}
