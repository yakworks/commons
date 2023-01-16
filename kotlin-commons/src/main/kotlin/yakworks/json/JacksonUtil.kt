package yakworks.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.IOException
import java.lang.reflect.Type
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*


object JacksonUtil {
    @JvmField
    val objectMapper: ObjectMapper = ObjectMapper()
        .findAndRegisterModules() //uses ServiceLoader to find "Modules", registered in META-INF.services
        .registerKotlinModule()
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .registerModule(
            SimpleModule().addSerializer(OffsetDateTime::class.java, OffsetDateTimeSerializer())
                .addDeserializer(OffsetDateTime::class.java, OffsetDateTimeDeserializer())
        );

    /** convert object to json string */
    @JvmStatic
    fun toJson(obj: Any?): String {
        return stringify(obj)
    }

    /** convert object to json string */
    @JvmStatic
    fun stringify(obj: Any?): String {
        return objectMapper.writeValueAsString(obj)
    }

    /** json to object */
    @JvmStatic
    fun <T> parseJson( text: String, clazz: Class<T> ): T {
        val parsedObj: T = objectMapper.readValue(text, clazz)
        return parsedObj
    }

    /** json to object, alias to parseJson*/
    @JvmStatic
    fun <T> fromString( text: String, clazz: Class<T> ): T = parseJson(text, clazz)

    /** binds the data to new instance of the pased in class  */
    //@Throws(IllegalArgumentException::class)
    @JvmStatic
    fun <T> bind(data: Any?, toValueType: Class<T>): T {
        return objectMapper.convertValue(data, toValueType)
    }

    /** binds the data to the instance */
    @JvmStatic
    fun <T> bindUpdate( instance: T, data: Any?): T {
        return objectMapper.updateValue(instance, data)
    }

    @Throws(IOException::class)
    @JvmStatic
    fun <T> fromBytes(value: ByteArray?, clazz: Class<T>?): T {
        return objectMapper.readValue(value, clazz)
    }

    @Throws(IOException::class)
    @JvmStatic
    fun <T> fromBytes(value: ByteArray?, type: Type?): T {
        return objectMapper.readValue(value, objectMapper.typeFactory.constructType(type))
    }
}

class OffsetDateTimeSerializer : JsonSerializer<OffsetDateTime>() {
    @Throws(IOException::class)
    override fun serialize(offsetDateTime: OffsetDateTime?, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        if (offsetDateTime == null) {
            jsonGenerator.writeNull()
        } else {
            jsonGenerator.writeString(offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        }
    }

    override fun handledType(): Class<OffsetDateTime> {
        return OffsetDateTime::class.java
    }
}

class OffsetDateTimeDeserializer : JsonDeserializer<OffsetDateTime>() {
    @Throws(IOException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): OffsetDateTime? {
        return if (jsonParser.text != null) {
            try {
                OffsetDateTime.parse(jsonParser.text, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            } catch (e: DateTimeParseException) {
                val date = Date(jsonParser.doubleValue.toLong() * 1000)
                date.toInstant().atOffset(ZoneOffset.UTC)
            }
        } else null
    }

    override fun handledType(): Class<OffsetDateTime> {
        return OffsetDateTime::class.java
    }

}
