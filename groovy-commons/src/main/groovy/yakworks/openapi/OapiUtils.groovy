/*
* Copyright 2022 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.openapi

import java.time.LocalDate
import java.time.LocalDateTime

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

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

}
