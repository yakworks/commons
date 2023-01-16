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
import com.github.jknack.handlebars.cache.TemplateCache
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

    // see good explanation of thread safe static instance stratgey https://stackoverflow.com/a/16106598/6500859
    @SuppressWarnings('UnusedPrivateField')
    private static class Holder {

        //not final as we need ability to override it and set it,
        // in our spring boot we do just that so we are shaing the same instance across the board.
        private static Handlebars instance = new Handlebars()
            .with(new HighConcurrencyTemplateCache())

        static {
            //setting so wrapping args in quotes is optional
            instance.stringParams(true)
            //these 2 are built into core handlebars but not registered by default
            StringHelpers.register(instance)
            handlebars.registerHelpers(ConditionalHelpers.class);
            //register some common helpers https://github.com/jknack/handlebars.java/blob/master/handlebars-helpers/README.md
            instance.registerHelper("assign", AssignHelper.INSTANCE)
            NumberHelper.register(instance)
        }
    }

    static Handlebars getHandlebars() {
        return Holder.instance
    }

    static Handlebars setHandlebars(Handlebars val) {
        return Holder.instance = val
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
