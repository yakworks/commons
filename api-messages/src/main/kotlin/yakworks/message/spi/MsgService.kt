package yakworks.message.spi

import yakworks.message.MsgContext
import yakworks.message.MsgKey
import yakworks.message.MsgKey.Companion.ofCode
import yakworks.message.MsgMultiKey

/**
 * Similiar to org.springframework.context.MessageSource but no dependencies
 * so can be used outside spring, micronaut and grails without needing to depend on any framework
 *
 * @author Joshua Burnett (@basejump)
 * @since 0.3.0
 */
interface MsgService {
    /**
     * This is the Main/primary getMessage method that needs to be implements and that the rest flow through.
     */
    fun getMessage(code: String?, msgContext: MsgContext?): String?

    /**
     * shorter alias to getMessage
     */
    operator fun get(code: String?, msgContext: MsgContext?): String? {
        return getMessage(code, msgContext)
    }

    /**
     * gets the message using a MsgKey, will make a context using the args and fallback in the msgKey.
     * Will use default Locale in the LocaleHolder
     */
    operator fun get(code: String?): String? {
        return get(ofCode(code))
    }

    /**
     * gets the message using a MsgKey, will make a context using the args and fallback in the msgKey.
     * Will use default Locale in the LocaleHolder
     */
    operator fun get(msgKey: MsgKey?): String? {
        return getMessage(msgKey!!.code, MsgContext.of(msgKey))
    }

    /**
     * gets the message using MsgContext, which contains args and locale as well
     */
    operator fun get(context: MsgContext): String? {
        return getMessage(context.code, context)
    }

    // support the spring way and allows anything to be passed to args and the MsgArgHolder will try and sort it out
    operator fun get(code: String?, args: Any?, fallbackMessage: String?): String? {
        return getMessage(code, MsgContext.of(args).fallbackMessage(fallbackMessage))
    }

    operator fun get(code: String?, args: Any?): String? {
        return getMessage(code, MsgContext.of(args))
    }

    /**
     * Get first found message for multiKey
     */
    operator fun get(msgMultiKey: MsgMultiKey): String? {
        val codes = msgMultiKey.codes
        if (codes != null) {
            var lastCode: String? = ""
            for (code in codes) {
                lastCode = code
                val message = get(code, MsgContext.of(msgMultiKey).useCodeAsDefaultMessage(false))
                if (message != null) {
                    return message
                }
            }
            // if we got here then nothing found, if spring service has useCodeAsDefaultMessage
            // then run again and if true will return the code, otherwise null.
            return get(lastCode, MsgContext.of(msgMultiKey).useCodeAsDefaultMessage(true))
        }
        return null
    }

    /**
     * Process the string template through the prefered i18n engine..
     *
     * @param template the string template to process with args in MsgContext
     * @param context the msgContext
     * @return the translated message
     */
    fun interpolate(template: String?, context: MsgContext?): String?
}
