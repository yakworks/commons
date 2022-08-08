package yakworks.message.spi

/**
 * Interface to implment for serviceLoader to find the message service
 */
interface MsgServiceProvider {

    fun get(): MsgService

}
