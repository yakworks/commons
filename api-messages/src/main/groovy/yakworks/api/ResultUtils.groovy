/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

import groovy.transform.CompileStatic

import yakworks.message.MsgService

@CompileStatic
class ResultUtils {

    static String resultToStringCommon(final Result p) {
        String title = p.title ? "title=$p.title" : null
        String code = p.code ? "code=$p.code" : null
        String value = isBasicType(p.payload?.class) ? "payload=$p.payload" : null
        String status = p.status.code
        return [title, code, value, status ].findAll{it != null}.join(', ')
    }

    static String resultToString(final Result p) {
        String concat = "ok=${p.ok}, " + resultToStringCommon(p)
        String cname = p.class.simpleName
        return "${cname}(${concat})"
    }


    /**
     * add message arguments for things in entity
     * - add name as class.simpleName
     * - add id if it has one
     * - add stamp if it has one
     * returns null if the msg
     */
    static Map addCommonArgs(Map args, Object entity){
        if(args == null) return args
        args.putIfAbsent('name', entity.class.simpleName)
        if (entity.hasProperty('id') && entity['id'])
            args.putIfAbsent('id', entity['id'])

        if (entity.hasProperty('stamp'))
            args.putIfAbsent('stamp', entity['stamp'])
    }

    // swallow no such message exception and returns empty string
    static String getMessage(MsgService msgService, Result result){
        String message
        if(result.msg) message = msgService.get(result.msg)

        if(!message){
            if(result.title) {
                message = result.title
            } else if(result instanceof ApiResults && result.size() != 0) {
                //use msg form first item
                message = msgService.get(((ApiResults)result)[0].msg)
            }
        }

        return message
    }

    protected static final List BASIC_TYPES = [
        String, Boolean, Byte, Short, Integer, Long, Float, Double, Character
    ] as List<Class>

    /**
     * checks if Class is basic type (String, long/Long, boolean/Boolean, etc...)
     */
    static boolean isBasicType(Class c) {
        if(c == null) return false
        return BASIC_TYPES.contains(c) || c.isPrimitive()
    }

}
