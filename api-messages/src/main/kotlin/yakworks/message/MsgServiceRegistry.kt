package yakworks.message

import yakworks.message.spi.MsgService
import yakworks.message.spi.MsgServiceProvider
import java.util.*

/**
 * Looks up MsgServiceProvider
 */
object MsgServiceRegistry {
    private var INSTANCE: MsgService? = null
    private var isLoaded = false

    // recomended to use the serviceLoader but can use this to set the static on app startup
    @JvmStatic
    var service: MsgService?
        get() {
            if (INSTANCE == null && !isLoaded) {
                INSTANCE = loadMsgService()
                isLoaded = true
            }
            return INSTANCE
        }
        set(value) {
            INSTANCE = value
        }

    fun loadMsgService(): MsgService? {
        val msp = loadService(MsgServiceProvider::class.java)
        return msp?.get()
    }

    /**
     * Generic implementation of a service lookup, can be refactored out into helper later
     */
    fun <SERVICE> loadService(serviceType: Class<SERVICE>?): SERVICE? {
        val services: Iterator<SERVICE> =
            ServiceLoader.load(serviceType, MsgServiceRegistry::class.java.classLoader).iterator()
        val result = if (services.hasNext()) services.next() else null
        //if only one should be expected then this can show error
        if (result == null) {
            System.err.print("No MsgService Found")
        } else if (services.hasNext()) {
            System.err.printf(
                "Found multiple implementations for the service provider %s. Only one should be setup, Using the first one",
                serviceType
            )
        }
        return result
    }

    // another examlpe but with a default that can be passed in.
    operator fun <SERVICE> get(serviceType: Class<SERVICE>?, defaultValue: SERVICE): SERVICE {
        val services: Iterator<SERVICE> =
            ServiceLoader.load(serviceType, MsgServiceRegistry::class.java.classLoader).iterator()
        var result = if (services.hasNext()) services.next() else defaultValue
        //if only one should be expected then this can show error
        if (services.hasNext()) {
            result = defaultValue
            //System.err.println(
            //    String.format(
            //        "Found multiple implementations for the service provider %s. Using the default: %s",
            //        serviceType, result.javaClass
            //    )
            //)
        }
        return result
    }
}
