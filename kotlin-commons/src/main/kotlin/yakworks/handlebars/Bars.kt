package yakworks.handlebars

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.cache.HighConcurrencyTemplateCache
import com.github.jknack.handlebars.helper.AssignHelper
import com.github.jknack.handlebars.helper.ConditionalHelpers
import com.github.jknack.handlebars.helper.NumberHelper
import com.github.jknack.handlebars.helper.StringHelpers
import yakworks.json.JacksonUtil


object Bars {
    @JvmField
    val handlebars: Handlebars = Handlebars().with(HighConcurrencyTemplateCache())

    init {
        //setting so wrapping args in quotes is optional
        handlebars.stringParams(true)
        //these 2 are built into core handlebars but not registered by default
        StringHelpers.register(handlebars)
        handlebars.registerHelpers(ConditionalHelpers::class.java)
        //register some common helpers https://github.com/jknack/handlebars.java/blob/master/handlebars-helpers/README.md
        handlebars.registerHelper("assign", AssignHelper.INSTANCE)
        NumberHelper.register(handlebars)
    }

    /** compile and apply*/
    @JvmStatic
    fun apply(location: String, model: Any): String {
        return handlebars.compile(location).apply(model)
    }

    /** convert object to json string */
    @JvmStatic
    fun applyInline(template: String, model: Any): String {
        return handlebars.compileInline(template).apply(model)
    }

    //Template compile(final String location) {
    //    return handlebars.compile(location)
    //}
    //
    //Template compileInline(final String input) {
    //    return handlebars.compileInline(input)
    //}

}
