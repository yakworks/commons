/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

import groovy.transform.CompileStatic

import yakworks.message.Msg
import yakworks.message.MsgKey

/**
 * A Parent Result that has a list of Result(s).
 * The data in this case is a List of result/problem instances
 */
@CompileStatic
class ApiResults implements ResultTrait<ApiResults>, Serializable {
    Boolean ok = true
    ApiStatus status = HttpStatus.MULTI_STATUS

    //internal rep
    @Delegate List<Result> results

    /**
     * New result
     * @param isSynchronized defaults to true to create the data list as synchronizedList
     */
    ApiResults(boolean isSynchronized = true){
        results = ( isSynchronized ? Collections.synchronizedList([]) : [] ) as List<Result>
    }

    // ** BUILDERS STATIC OVERRIDES **
    static ApiResults create(boolean isSynchronized = true){ new ApiResults(isSynchronized) }
    static ApiResults OK(){ new ApiResults() }
    static ApiResults of(String code, Object args) {
        return new ApiResults().msg(code, args)
    }
    static ApiResults ofPayload(Object payload) {
        return new ApiResults().payload(payload);
    }

    ApiResults ok(boolean v){
        ok = v
        return this
    }

    /**
     * Overrides to get msg from first item in result if main one is null
     */
    @Override
    MsgKey getMsg(){
        if(getMsgKey() == null){
            if(getDefaultCode()){
                msgKey = Msg.key(getDefaultCode())
            } else if(results.size() != 0){
                msgKey =  results[0].msg
            }
        }
        return msgKey
    }

    @Override //changes default list delegate so we can add ok
    boolean add(Result result){
        if(!result.ok) ok = false
        results << result
    }

    /**
     * if resultToMerge is ApiResults then add all from its resultList
     * else just call add to list
     */
    void merge(Result resultToMerge){
        if(!resultToMerge.ok) ok = false
        if(resultToMerge instanceof ApiResults){
            results.addAll(resultToMerge.results as List<Result>)
        } else {
            results << resultToMerge
        }

    }

    /**
     * returns the problems or results.ok=false as could contain other container apiResults
     * that are not problems but apiResults with problems
     */
    List<Result> getProblems(){
        //only look if this is not ok as it should never have problems if ok=true
        if(this.ok){
            [] as List<Result>
        } else {
            results.findAll{ !it.ok } as List<Result>
        }
    }

    /**
     * returns the successful results
     */
    List<Result> getOkResults(){
        results.findAll{ it.ok } as List<Result>
    }

    //Add these temporarily to be compatible with old Results
    List<Result> getFailed(){
        getProblems()
    }
    List<Result> getSuccess(){
        getOkResults()
    }

    /**
     * converts to Map, helpfull for to json and can be overriden on concrete impls
     */
    @Override
    Map<String, Object> asMap(){
        Map<String, Object> hmap = ResultSupport.toMap(this);
        if(!this.ok){
            hmap.put("problems", getProblems());
        }
        return hmap;
    }

}
