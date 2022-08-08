/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.problem

import yakworks.message.Msg
import yakworks.message.MsgKey

/**
 * Violation Implementation
 */
open class ViolationFieldError : Violation {
    override var msg: MsgKey? = null
    override var field: String? = null
    override var message: String? = null

    fun field(v: String?): ViolationFieldError = apply { field = v }

    //STATICS
    companion object {
        @JvmStatic
        fun of(mk: MsgKey?): ViolationFieldError {
            return ViolationFieldError().apply { msg = mk }
        }

        @JvmStatic
        fun of(code: String, msgText: String ): ViolationFieldError {
            return ViolationFieldError().apply {
                msg = Msg.key(code)
                message = msgText
            }
        }
    }
}
