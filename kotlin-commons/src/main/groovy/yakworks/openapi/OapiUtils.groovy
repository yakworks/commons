/*
* Copyright 2022 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.openapi

import java.time.LocalDate
import java.time.LocalDateTime

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

import yakworks.meta.MetaEntity

@CompileStatic
class OapiUtils {

    /**
     * The main schema attrs to cylce through for schema
     * see https://swagger.io/specification/#schema-object
     */
    public static final List schemaAttrs = [
        'name', 'title', 'multipleOf',  'maximum', 'exclusiveMaximum', 'minimum',
        'exclusiveMinimum', 'maxLength', 'minLength', 'pattern', 'maxItems', 'minItems', 'uniqueItems',
        'maxProperties', 'minProperties', 'required', 'type', 'not', 'description', 'format', '$ref', 'nullable',
        'readOnly', 'writeOnly', 'example', 'enum'
    ]

    /* see http://epoberezkin.github.io/ajv/#formats */
    /* We are adding 'money' and 'date' as formats too
     * big decimal defaults to money
     */
    static Map<String,Object> getJsonType(Class propertyType) {
        Map typeFormat = [type: 'string'] as Map<String,Object>
        switch (propertyType) {
            case [Boolean, Byte]:
                typeFormat.type = 'boolean'
                break
            case [Integer, Short]:
                typeFormat.type = 'integer'
                break
            case [Long]:
                typeFormat.type = 'integer'
                typeFormat.format = 'int64'
                break
            case [Double, Float]:
                typeFormat.type = 'number'
                break
            case [BigDecimal]:
                typeFormat.type = 'number'
                //defaults to money
                typeFormat.format = 'money'
                break
            case [LocalDate]:
                typeFormat.type = 'string'
                //date. verified to be a date of the format YYYY-MM-DD
                typeFormat.format = 'date'
                break
            case [Date, LocalDateTime]:
                //date-time. verified to be a valid date and time in the format YYYY-MM-DDThh:mm:ssZ
                typeFormat.type = 'string'
                typeFormat.format = 'date-time'
                break
            case [String]:
                typeFormat.type = 'string'
                break
        }

        if(propertyType.isEnum()){
            typeFormat.type = 'string'
            typeFormat.enum = convertEnum(propertyType) as String[]
            // propertyType.values()*.name() as String[]
        }

        //TODO what about types like Byte etc..?
        return typeFormat
    }

    @CompileDynamic
    static String[] convertEnum(Class enumClazz){
        return enumClazz.values()*.name() as String[]
    }


    /**
     * convert to openApi schema in map form, ready to be dumped to json parser.
     *
     * Example:
     *    MetaEntity src                    Schema yml
     * -----------------------------------------------
     * name: "Org"                      ->    name: Org
     * title: "Org"                     ->    title: Organizations
     * classType: yak.rally.Org         ->    type: object
     * metaProps: [                     ->    properties:
     *  [num: [                         ->      num:
     *    classType: java.lang.String   ->        type: string
     *    schema.maxLength: 50          ->        maxLength: 50
     *  ], [name: etc..                 ->      name: ...
     *
     */
    static Map<String, Object> toSchemaMap(MetaEntity ment, boolean isChild = false) {
        Map schemap = [:] as Map<String, Object>
        if(!isChild) schemap.name = ment.name
        schemap.title = ment.title
        // schemap.type = 'object' //object is the default

        Map props = [:]
        for (String key in ment.metaProps.keySet()) {
            def val = ment.metaProps[key]
            if(val instanceof MetaEntity) {
                props[key] = toSchemaMap(val, true)
            } else {
                props[key] = OapiUtils.schemaPropToMap(val.schema)
            }
        }
        schemap['properties'] = props
        return schemap
    }

    /**
     * converts a schema property to map.
     * example would properteis schema under the num prop below.
     * properties: [
     *   amount: [
     *     type: "number",
     *     format: "money"
     *     title: "amount due"
     *     etc..
     *   ] <- this is the map it returns
     *   ...
     * ]
     */
    static Map<String, Object> schemaPropToMap(Object schemaProp) {
        if(!schemaProp) return [:]
        Map schemaMap = [:] as Map<String, Object>
        for(String attr: OapiUtils.schemaAttrs){
            if(schemaProp[attr] != null){
                schemaMap[attr] = schemaProp[attr]
            }
        }
        return schemaMap
    }


}
