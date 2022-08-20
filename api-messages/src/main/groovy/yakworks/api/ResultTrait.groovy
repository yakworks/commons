/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.api

import groovy.transform.CompileStatic

import yakworks.message.Msg
import yakworks.message.MsgKey

/**
 * Groovy Trait impl for Result, provides the simple builder methods
 *
 * @author Joshua Burnett (@basejump)
 * @since 1
 */
@CompileStatic
trait ResultTrait<E extends GenericResult> implements GenericResult<E> {
    String defaultCode //= 'result.ok'
    Boolean ok = true
    ApiStatus status = HttpStatus.OK
    /** backing field for the getMsg */
    MsgKey msgKey
    String title
    Object payload

    MsgKey getMsg() {
        if(msgKey == null) msgKey = Msg.key(getDefaultCode())
        return msgKey
    }

    void setMsg(MsgKey v) { msgKey = v }

    @Override
    String toString() {
        return ResultUtils.resultToString(this)
    }

}
