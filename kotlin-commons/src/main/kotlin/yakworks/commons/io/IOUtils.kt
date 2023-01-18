package yakworks.commons.io

import org.slf4j.LoggerFactory
import yakworks.util.FileCopyUtils
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.Writer

object IOUtils {
    private val log = LoggerFactory.getLogger(IOUtils::class.java)

    /**
     * flush the writer, ignoring IOException. then use groovy's closeWithWarning
     *
     * @param writer the writer to flush close
     */
    @JvmStatic
    fun flushAndClose(writer: Writer) {
        try {
            writer.flush()
        } catch (e: IOException) {
            // try to continue even in case of error
        }
        tryClose(writer, true)
    }

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * Closes both streams when done.
     *
     * @param ins  the stream to copy from
     * @param outs the stream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    @JvmStatic @Throws(IOException::class)
    fun copy(ins: InputStream?, outs: OutputStream?): Int {
        return FileCopyUtils.copy(ins!!, outs!!)
    }

    /**
     * Attempts to close the closeable returning rather than throwing
     * any Exception that may occur.
     *
     * @param closeable  the thing to close
     * @param logWarning if true will log a warning if an exception occurs
     * @return throwable Exception from the close method, else null
     */
    @JvmStatic
    fun tryClose(closeable: AutoCloseable?, logWarning: Boolean): Throwable? {
        var thrown: Throwable? = null
        if (closeable != null) {
            try {
                closeable.close()
            } catch (e: Exception) {
                thrown = e
                if (logWarning) {
                    log.warn("Caught exception during close(): $e")
                }
            }
        }
        return thrown
    }

}
