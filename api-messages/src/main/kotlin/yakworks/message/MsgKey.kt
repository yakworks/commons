package yakworks.message

/**
 * MsgKey contains the lookup code for the message and the argument map for name substitutions.
 * Can also have a defaultMessage stored as a key in the argument map
 *
 * Related to org.springframework.context.MessageSourceResolvable interface but simplified.
 * * This differs in that its simplified and skinnied down
 * - only one code instead of array
 * - message arguments are params and are not an array but based on keys in map
 * - if a list or array is passed then it looks at the first element to see if its a map and uses that
 * - no default message prop but one can be passed into the map with the key 'defaultMessage'
 *
 * @author Joshua Burnett (@basejump)
 * @since 0.3.0
 */
@Suppress("UNUSED_PARAMETER")
interface MsgKey {

    var code: String?
        get() = null
        set(value) { TODO() }

    /**
     * Return the Map of arguments to be used to resolve this message as ICU.
     * A default message can also be in the map params as a 'defaultMessage' key.
     */
    var args: MsgArgs?
        get() = null
        set(value) { TODO() }

    /**
     * if object is passed in then will create this object setter will create  MsgArgs.of()
     */
    fun setArgs(value: Any?) {
        args = if (value is MsgArgs) value else MsgArgs.of(value)
    }

    /**
     * fallbackMessage is the same as defaultMessage for example in spring.
     * Its name as a fallback as thats what it is and should not really be used or leaned on, it means the i18n is not configured correctly
     * If one is set here then return it, if not it looks at args and if its a map then returns the 'fallbackMessage' key if it exists
     */
    var fallbackMessage: String?
        get() = null
        set(value) { TODO() }

}
