package yakworks.openapi

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


object OapiUtilsKt {
    @JvmField
    val schemaAttrs: List<String> = listOf(
        "name", "title", "multipleOf",  "maximum", "exclusiveMaximum", "minimum",
        "exclusiveMinimum", "maxLength", "minLength", "pattern", "maxItems", "minItems",
        "uniqueItems", "maxProperties", "minProperties", "required", "type", "not", "description",
        "format", "\$ref", "nullable",
        "readOnly", "writeOnly", "example", "enum"
    )

    /** compile and apply*/
    @JvmStatic
    fun getJsonType(propertyType: Class<*>): Map<String,Any> {
        var typeFormat: MutableMap<String,Any> = mutableMapOf("type" to "string")
        var typeName = ""
        var typeFmt = ""
        when (propertyType) {
            Boolean::class, Byte::class -> typeName = "boolean"
            Integer::class, Short::class -> typeName = "integer"
            Long::class -> {
                typeName = "integer"
                typeFmt = "int64"
            }
            Double::class, Float::class -> typeName = "number"
            BigDecimal::class -> {
                typeName = "number"
                typeFmt = "money"
            }
            LocalDate::class -> {
                typeName = "string"
                typeFmt = "date"
            }
            LocalDateTime::class, Date::class -> {
                typeName = "string"
                typeFmt = "date-time"
            }
            String::class -> typeName = "string"

            else -> typeName="NONE"
        }
        typeFormat["type"] = typeName
        typeFormat["format"] = typeFmt

        if(propertyType.isEnum()){
            typeFormat["type"] = "string"
            typeFormat["format"] = convertEnum(propertyType)
        }

        //TODO what about types like Byte etc..?
        return typeFormat
    }

    fun convertEnum(enumClazz: Class<*>): List<String>  {
        return getNames(enumClazz as Class<out Enum<*>>)
    }

    @JvmStatic
    fun getNames(e: Class<out Enum<*>>): List<String> {
        return e.enumConstants.map { it.name }
    }

}
