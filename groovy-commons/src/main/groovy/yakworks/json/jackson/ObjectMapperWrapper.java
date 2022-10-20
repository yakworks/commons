package yakworks.json.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import groovy.lang.GString;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Date;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

/**
 * Wraps a Jackson {@link ObjectMapper} so that you can supply your own {@link ObjectMapper} reference.
 * see Jackson2ObjectMapperBuilder from Spring and the com.vladmihalcea.hibernate.type.util.ObjectMapperWrapper for design
 *
 * calls findAndRegisterModules to
 */
public class ObjectMapperWrapper implements Serializable {

    public static final ObjectMapperWrapper INSTANCE = new ObjectMapperWrapper();

    private final ObjectMapper objectMapper;

    public ObjectMapperWrapper() {
        this(setupDefaultObjectMapper());
    }

    public ObjectMapperWrapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static ObjectMapper setupDefaultObjectMapper() {
        return new ObjectMapper()
            .findAndRegisterModules() //uses ServiceLoader to find "Modules", registered in META-INF.services
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(
                new SimpleModule()
                    // .addSerializer(GString.class, GStringJsonSerializer.INSTANCE)
                    .addSerializer(OffsetDateTime.class, OffsetDateTimeSerializer.INSTANCE)
                    .addDeserializer(OffsetDateTime.class, OffsetDateTimeDeserializer.INSTANCE)
            );
        // these should already be picked up by the findAndRegisterModules
        // .registerModule(new ParameterNamesModule())
        // .registerModule(new Jdk8Module())
        // .registerModule(new JavaTimeModule())
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public <T> T fromString(String string, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(string, clazz);
    }

    public <T> T fromString(String string, Type type) throws JsonProcessingException {
        return objectMapper.readValue(string, objectMapper.getTypeFactory().constructType(type));
    }

    public <T> T fromBytes(byte[] value, Class<T> clazz) throws IOException {
        return objectMapper.readValue(value, clazz);
    }

    public <T> T fromBytes(byte[] value, Type type) throws IOException {
        return objectMapper.readValue(value, objectMapper.getTypeFactory().constructType(type));

    }

    public String toString(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    public byte[] toBytes(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(value);
    }

    public JsonNode toJsonNode(String value) throws JsonProcessingException {
        return objectMapper.readTree(value);
    }

    public static class OffsetDateTimeSerializer extends com.fasterxml.jackson.databind.JsonSerializer<OffsetDateTime> {

        public static final OffsetDateTimeSerializer INSTANCE = new OffsetDateTimeSerializer();

        @Override
        public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (offsetDateTime == null) {
                jsonGenerator.writeNull();
            } else {
                jsonGenerator.writeString(offsetDateTime.format(ISO_OFFSET_DATE_TIME));
            }
        }

        @Override
        public Class<OffsetDateTime> handledType() {
            return OffsetDateTime.class;
        }
    }

    public static class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

        public static final OffsetDateTimeDeserializer INSTANCE = new OffsetDateTimeDeserializer();

        @Override
        public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            if (jsonParser.getText() != null) {
                try {
                    return OffsetDateTime.parse(jsonParser.getText(), ISO_OFFSET_DATE_TIME);
                } catch (DateTimeParseException e) {
                    Date date = new Date((long) jsonParser.getDoubleValue() * 1000);
                    return date.toInstant().atOffset(ZoneOffset.UTC);
                }
            }
            return null;
        }

        @Override
        public Class<OffsetDateTime> handledType() {
            return OffsetDateTime.class;
        }
    }

    public static class GStringJsonSerializer extends com.fasterxml.jackson.databind.JsonSerializer<GString>{
        public static final GStringJsonSerializer INSTANCE = new GStringJsonSerializer();

        @Override
        public void serialize(GString gstring, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (gstring == null) {
                jsonGenerator.writeNull();
            } else {
                jsonGenerator.writeString(gstring.toString());
            }
        }

        @Override
        public Class<GString> handledType() {
            return GString.class;
        }

    }
}
