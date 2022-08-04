/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api;

import groovy.transform.CompileStatic;

import yakworks.message.MsgServiceRegistry;
import yakworks.message.spi.MsgService;

@CompileStatic
class ResultUtils {

    public static MsgService getMsgService(){
        return MsgServiceRegistry.service;
    }

    static String resultToStringCommon(final Result p) {
        String title = p.title ? "title=$p.title" : null;
        String code = p.code ? "code=$p.code" : null;
        String value = isBasicType(p.payload?.class) ? "payload=$p.payload" : null;
        String status = p.status.code
        return [title, code, value, status ].findAll{it != null}.join(', ');
    }

    static String resultToString(final Result p) {
        String concat = "ok=${p.ok}, " + resultToStringCommon(p)
        String cname = p.class.simpleName
        return "${cname}(${concat})"
    }

    /**
     * converts to Map, helpfull for to json and can be overriden on concrete impls
     */
    static Map<String, Object> toMap( Result res ){
        Map<String, Object> hmap = new LinkedHashMap<>();
        hmap.put("ok", res.getOk());
        hmap.put("status", res.getStatus().getCode());
        hmap.put("code", res.getCode());
        // hmap.put("payload", res.getPayload());
        if(getMsgService() != null){
            hmap.put("title", getMessage(getMsgService(), res));
        } else {
            hmap.put("title", res.getTitle());
        }
        // if payload is basic or collection type (not an object) then add it.
        // FIXME need better way to tweak this. maybe look up the "converters" and only do it then?
        Class payloadClass = res.getPayload()?.class
        if(payloadClass && (
            isBasicType(payloadClass) || Map.isAssignableFrom(payloadClass) || Collection.isAssignableFrom(payloadClass)
        ) ){
            hmap.put("payload", res.getPayload());
        }

        return hmap;
    }


    /**
     * add message arguments for things in entity
     * - add name as class.simpleName
     * - add id if it has one
     * - add stamp if it has one
     * returns null if the args is null otherwise returns the updated args for chaining, args is updated in place so its optional to use
     */
    static Map addCommonArgs(Map args, Object entity){
        if(args == null) return args
        args.putIfAbsent('name', entity.class.simpleName)
        if (entity.hasProperty('id') && entity['id'])
            args.putIfAbsent('id', entity['id'])

        if (entity.hasProperty('stamp'))
            args.putIfAbsent('stamp', entity['stamp'])

        return args
    }

    /**
     * Uses code and args to do look up for message
     * @param msgService the MsgService
     * @param result the result to use for keys
     * @return the message
     */
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

    protected static final List COLLECTION_TYPES = [
        Map, Collection
    ] as List<Class>

    /**
     * checks if Class is basic type (String, long/Long, boolean/Boolean, etc...)
     */
    static boolean isBasicType(Class c) {
        if(c == null) return false
        return BASIC_TYPES.contains(c) || c.isPrimitive()
    }

}
