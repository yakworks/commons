/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

import yakworks.api.ResultSupport.toMap
import yakworks.message.Msg
import yakworks.message.MsgKey

/**
 * Simple OkResult with a Map as the data object
 *
 * @author Joshua Burnett (@basejump)
 */
open class ResultsList : GenericResult<ResultsList> {

    override var ok: Boolean = true
    override val defaultCode: String? = null
    override var title: String? = null
    override var status: ApiStatus = HttpStatus.MULTI_STATUS
    override var payload: Any? = null

    var list: MutableList<Result> = mutableListOf()

    override var msg: MsgKey? = null
        get() {
            if(field == null) field = Msg.key(defaultCode)
            return field
        }

    /**
     * returns the list of successful results
     */
    fun getOkResults(): List<Result> {
        return list.filter{ it.ok }
    }

    /**
     * Alias to getOkResults()
     * @see #getOkResults()
     */
    fun getSuccess(): List<Result> = getOkResults()

    /**
     * returns the problems or results.ok=false as could contain other container apiResults
     * that are not problems but apiResults with problems
     */
     fun getProblems(): List<Result>{
        //only look if this is not ok as it should never have problems if ok=true
        if(ok){
            return listOf()
        } else {
            return list.filter{ !it.ok }
        }
    }

    override fun asMap(): Map<String, Any?> {
        val hmap = toMap(this) as MutableMap<String, Any?>
        if (!ok) {
            hmap.put("problems", getProblems())
        }
        return hmap
    }
    override fun toString(): String {
        return ResultSupport.resultToString(this)
    }
}
