/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.handlebars

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.cache.HighConcurrencyTemplateCache
import com.github.jknack.handlebars.helper.AssignHelper
import com.github.jknack.handlebars.helper.ConditionalHelpers
import com.github.jknack.handlebars.helper.NumberHelper
import com.github.jknack.handlebars.helper.StringHelpers

/**
 * Wrapper to setup handlebars to use a common instance.
 * see https://github.com/jknack/handlebars.java
 * and https://jknack.github.io/handlebars.java/
 *
 * @author Joshua Burnett (@basejump)
 */
@SuppressWarnings('FieldName')
@Builder(builderStrategy= SimpleStrategy, prefix="")
@CompileStatic
class Bars {

    private static volatile Handlebars handlebarsInstance

    /**
     * Registers our "opinionated" default helpers on handlebars.
     * @param hbars the Handlebars instace to call registerHelpers on
     */
    static void registerDefaultHelpers(Handlebars hbars){
        //setting so wrapping args in quotes is optional
        hbars.stringParams(true)
        //these 2 are built into core handlebars but not registered by default
        StringHelpers.register(hbars)
        hbars.registerHelpers(ConditionalHelpers)
        //register some common helpers https://github.com/jknack/handlebars.java/blob/master/handlebars-helpers/README.md
        hbars.registerHelper("assign", AssignHelper.INSTANCE)
        NumberHelper.register(hbars)
    }

    static Handlebars getHandlebars() {
        //thread safe with double checked locking https://www.baeldung.com/java-singleton-double-checked-locking
        if(!handlebarsInstance) {
            synchronized (Bars.class) {
                if (handlebarsInstance == null) {
                    handlebarsInstance = new Handlebars().with(new HighConcurrencyTemplateCache())
                    registerDefaultHelpers(handlebarsInstance)
                }
            }
        }
        return handlebarsInstance
    }

    static Handlebars setHandlebars(Handlebars val) {
        return handlebarsInstance = val
    }

    Template compile(final String location) {
        return handlebars.compile(location)
    }

    Template compileInline(final String input) {
        return handlebars.compileInline(input)
    }

    static String apply(String location, Object model) {
        return handlebars.compile(location).apply(model)
    }

    static String applyInline(String template, Object model) {
        return handlebars.compileInline(template).apply(model)
    }


}
