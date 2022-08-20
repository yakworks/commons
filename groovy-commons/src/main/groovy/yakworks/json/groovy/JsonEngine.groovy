/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.json.groovy

import groovy.json.JsonGenerator
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import yakworks.json.groovy.converters.CurrencyConverter
import yakworks.json.groovy.converters.InstantJsonConverter
import yakworks.json.groovy.converters.LocalDateJsonConverter
import yakworks.json.groovy.converters.LocalDateTimeJsonConverter
import yakworks.json.groovy.converters.LocalTimeJsonConverter
import yakworks.json.groovy.converters.OffsetDateTimeJsonConverter
import yakworks.json.groovy.converters.OffsetTimeJsonConverter
import yakworks.json.groovy.converters.PeriodJsonConverter
import yakworks.json.groovy.converters.URIConverter
import yakworks.json.groovy.converters.ZonedDateTimeJsonConverter

/**
 * Wrapper for groovy Json slurper and generator
 *
 * @author Joshua Burnett (@basejump)
 */
@SuppressWarnings('FieldName')
@Builder(builderStrategy= SimpleStrategy, prefix="")
@CompileStatic
class JsonEngine {

    public static JsonEngine INSTANCE

    String dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"

    String timeZone = "GMT"

    String locale = "en/US"

    Boolean escapeUnicode = false

    JsonGenerator jsonGenerator
    JsonSlurper jsonSlurper

    // JsonEngine(){ }

    // default build options
    JsonGenerator.Options buildOptions() {

        JsonGenerator.Options options = new JsonGenerator.Options()

        if (!escapeUnicode) {
            options.disableUnicodeEscaping()
        }
        Locale loc
        String[] localeData = locale.split('/')
        if (localeData.length > 1) {
            loc = new Locale(localeData[0], localeData[1])
        } else {
            loc = new Locale(localeData[0])
        }
        options.dateFormat(dateFormat, loc)
        options.timezone(timeZone)
        options.excludeNulls()

        getConverters().each {
            options.addConverter(it)
        }

        return options
    }

    JsonEngine build() {
        def opts = buildOptions()
        jsonGenerator = new EnhancedJsonGenerator(opts)
        // jsonGenerator = buildOptions().build()

        jsonSlurper = buildSlurper()
        return this
    }

    JsonSlurper buildSlurper(){
        //TODO make this configurable
        return new JsonSlurper() //.setType(JsonParserType.LAX).setLazyChop(false).setChop(true)
    }

    List<JsonGenerator.Converter> getConverters(){
        ServiceLoader<JsonGenerator.Converter> loader = ServiceLoader.load(JsonGenerator.Converter);
        List<JsonGenerator.Converter> converters = []
        for (JsonGenerator.Converter converter : loader) {
            converters.add(converter)
        }
        converters = converters.sort {
            it.hasProperty('order') ? it['order'] : 0
        }

        converters.add(new InstantJsonConverter())
        converters.add(new LocalDateJsonConverter())
        converters.add(new LocalDateTimeJsonConverter())
        converters.add(new LocalTimeJsonConverter())
        converters.add(new OffsetDateTimeJsonConverter())
        converters.add(new OffsetTimeJsonConverter())
        converters.add(new PeriodJsonConverter())
        converters.add(new ZonedDateTimeJsonConverter())
        converters.add(new CurrencyConverter())
        converters.add(new URIConverter())
        // OrderComparator.sort(converters)
        return converters
    }

    static String toJson(Object object){
        stringify(object)
    }

    static String stringify(Object object, Map arguments = [:]){
        getGenerator().toJson(object)
    }

    static JsonGenerator getGenerator(){
        if(!INSTANCE) INSTANCE = new JsonEngine().build()
        INSTANCE.jsonGenerator
    }

    static JsonSlurper getSlurper(){
        if(!INSTANCE) INSTANCE = new JsonEngine().build()
        INSTANCE.jsonSlurper
    }

    /**
     * Parse a JSON data structure from request body input stream.
     * if no content then returns an empty map
     */
    static Object parseJson(String text) {
        return getSlurper().parseText(text)
    }

    /**
     * parse string and expect the class type back.
     * usually would call this with parseJson(text, Map) or parseJson(text, List)
     */
    static <T> T parseJson(String text, Class<T> clazz) {
        Object parsedObj = parseJson(text)

        validateExpectedClass(clazz, parsedObj)

        return (T)parsedObj
    }

    /**
     * throw IllegalArgumentException if clazz is not a super of object
     */
    static void validateExpectedClass(Class clazz, Object parsedObj){
        if(!clazz.isAssignableFrom(parsedObj.class))
            throw new IllegalArgumentException("Json parsing expected a ${clazz.simpleName} but got a ${parsedObj.class.simpleName}")

    }

}
