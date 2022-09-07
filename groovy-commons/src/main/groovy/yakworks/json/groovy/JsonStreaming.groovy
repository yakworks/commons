/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.json.groovy

import java.nio.file.Path

import groovy.json.JsonGenerator
import groovy.json.StreamingJsonBuilder
import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport

/**
 * Json Parser
 *
 * @author Joshua Burnett (@basejump)
 * @since 7.0.8
 */
@SuppressWarnings('FieldName')
@Builder(builderStrategy= SimpleStrategy, prefix="")
@CompileStatic
class JsonStreaming {

    /**
     * Streams a collection of Objects to file, flushes and closes writer when finished.
     * Its assumed the path passed has directories already, will throw error if not
     *
     * @param payload the object to write to file
     * @param filePath the file as a Path object
     */
    static void streamToFile(Collection<Object> payload, Path path){
        def writer = path.newWriter()
        JsonGenerator generator = JsonEngine.generator
        //def sjb = new StreamingJsonBuilder(writer, JsonEngine.generator)

        if(payload[0] instanceof Number) {
            //if it is a list of ids etc, write down whole collection, because we dont want one number per line in json file.
            writer.write(generator.toJson(payload))
        } else {
            writer.write('[\n')
            int i = 0
            for (Object entry : payload) {
                //use json generator directly as StreamingJsonBuilder will output an array when a single object argument is passed to call()
                writer.write(generator.toJson(entry))
                writer.write(',\n')
                //to avoid OOM error flush every 1000 just in case
                if (i % 1000 == 0) {
                    writer.flush()
                }
                i++
            }
            writer.write(']')
        }
        flushAndClose(writer)
    }

    /**
     * flush the writer, ignoring IOException. then use groovy's closeWithWarning
     * @param writer the writer to flush close
     */
    @SuppressWarnings("EmptyCatchBlock")
    static void flushAndClose(Writer writer){
        try {
            writer.flush()
        } catch (IOException e) {
            // try to continue even in case of error
        }
        DefaultGroovyMethodsSupport.closeWithWarning(writer)
    }

}
