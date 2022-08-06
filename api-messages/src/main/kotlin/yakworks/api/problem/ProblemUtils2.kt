/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.problem

import yakworks.api.Result
import yakworks.api.ResultSupport
import java.util.Objects

internal object ProblemUtils2 {

    @JvmStatic
    fun resultToStringCommon(p: Result): String {
        val title = if (p.title != null) "title=${p.title}" else null
        val code = if (p.code != null) "code=${p.code}" else null
        var value: String? = null
        if(p.payload != null && ResultSupport.isBasicType(p.payload!!::class, ResultSupport.acceptedTypes)){
            value = "payload=$p.payload"
        }
        val status = p.status.code.toString()

        return listOf(title, code, value, status)
            .filter(Objects::nonNull)
            .joinToString{ it as String }
    }

    @JvmStatic
    fun problemToString(p: Problem): String {
        val concat = resultToStringCommon(p)
        //String type = p.type ? "type=$p.type" : null
        //concat = [concat, type, p.detail].findAll{it != null}.join(', ')
        val probName = p::class.simpleName
        return "${probName}(${concat})"
    }

    @JvmStatic
    fun buildMessage(p: Problem): String {
        val code = if (p.code != null) "code=${p.code}" else null
        return listOf(p.title, p.detail, code)
            .filter(Objects::nonNull)
            .joinToString(separator = ": ") { it as String }
    }

}
