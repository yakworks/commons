package yakworks.util

import jakarta.annotation.Nullable
import yakworks.util.Assert.notNull
import java.io.*
import java.nio.file.Files

/**
 * Simple utility methods for file and stream copying. All copy methods use a block size
 * of 4096 bytes, and close all affected streams when done. A variation of the copy
 * methods from this class that leave streams open can be found in [StreamUtils].
 *
 *
 * Mainly for use within the framework, but also useful for application code.
 *
 * @author Juergen Hoeller
 * @author Hyunjin Choi
 * @since 06.10.2003
 * @see StreamUtils
 *
 * @see FileSystemUtils
 */
object FileCopyUtils {
    /**
     * The default buffer size used when copying bytes.
     */
    const val BUFFER_SIZE = StreamUtils.BUFFER_SIZE
    //---------------------------------------------------------------------
    // Copy methods for java.io.File
    //---------------------------------------------------------------------
    /**
     * Copy the contents of the given input File to the given output File.
     * @param in the file to copy from
     * @param out the file to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    @JvmStatic
    @Throws(IOException::class)
    fun copy(`in`: File, out: File): Int {
        notNull(`in`, "No input File specified")
        notNull(out, "No output File specified")
        return copy(Files.newInputStream(`in`.toPath()), Files.newOutputStream(out.toPath()))
    }

    /**
     * Copy the contents of the given byte array to the given output File.
     * @param in the byte array to copy from
     * @param out the file to copy to
     * @throws IOException in case of I/O errors
     */
    @JvmStatic @Throws(IOException::class)
    fun copy(`in`: ByteArray?, out: File) {
        notNull(`in`, "No input byte array specified")
        notNull(out, "No output File specified")
        copy(ByteArrayInputStream(`in`), Files.newOutputStream(out.toPath()))
    }

    /**
     * Copy the contents of the given input File into a new byte array.
     * @param in the file to copy from
     * @return the new byte array that has been copied to
     * @throws IOException in case of I/O errors
     */
    @JvmStatic @Throws(IOException::class)
    fun copyToByteArray(`in`: File): ByteArray {
        notNull(`in`, "No input File specified")
        return copyToByteArray(Files.newInputStream(`in`.toPath()))
    }
    //---------------------------------------------------------------------
    // Copy methods for java.io.InputStream / java.io.OutputStream
    //---------------------------------------------------------------------
    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * Closes both streams when done.
     * @param in the stream to copy from
     * @param out the stream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    @JvmStatic @Throws(IOException::class)
    fun copy(`in`: InputStream, out: OutputStream): Int {
        notNull(`in`, "No InputStream specified")
        notNull(out, "No OutputStream specified")
        return try {
            StreamUtils.copy(`in`, out)
        } finally {
            close(`in`)
            close(out)
        }
    }

    /**
     * Copy the contents of the given byte array to the given OutputStream.
     * Closes the stream when done.
     * @param in the byte array to copy from
     * @param out the OutputStream to copy to
     * @throws IOException in case of I/O errors
     */
    @JvmStatic @Throws(IOException::class)
    fun copy(`in`: ByteArray?, out: OutputStream) {
        notNull(`in`, "No input byte array specified")
        notNull(out, "No OutputStream specified")
        try {
            out.write(`in`)
        } finally {
            close(out)
        }
    }

    /**
     * Copy the contents of the given InputStream into a new byte array.
     * Closes the stream when done.
     * @param in the stream to copy from (may be `null` or empty)
     * @return the new byte array that has been copied to (possibly empty)
     * @throws IOException in case of I/O errors
     */
    @JvmStatic @Throws(IOException::class)
    fun copyToByteArray(@Nullable `in`: InputStream?): ByteArray {
        if (`in` == null) {
            return ByteArray(0)
        }
        val out = ByteArrayOutputStream(BUFFER_SIZE)
        copy(`in`, out)
        return out.toByteArray()
    }
    //---------------------------------------------------------------------
    // Copy methods for java.io.Reader / java.io.Writer
    //---------------------------------------------------------------------
    /**
     * Copy the contents of the given Reader to the given Writer.
     * Closes both when done.
     * @param in the Reader to copy from
     * @param out the Writer to copy to
     * @return the number of characters copied
     * @throws IOException in case of I/O errors
     */
    @JvmStatic @Throws(IOException::class)
    fun copy(`in`: Reader, out: Writer): Int {
        notNull(`in`, "No Reader specified")
        notNull(out, "No Writer specified")
        return try {
            var charCount = 0
            val buffer = CharArray(BUFFER_SIZE)
            var charsRead: Int
            while (`in`.read(buffer).also { charsRead = it } != -1) {
                out.write(buffer, 0, charsRead)
                charCount += charsRead
            }
            out.flush()
            charCount
        } finally {
            close(`in`)
            close(out)
        }
    }

    /**
     * Copy the contents of the given String to the given Writer.
     * Closes the writer when done.
     * @param in the String to copy from
     * @param out the Writer to copy to
     * @throws IOException in case of I/O errors
     */
    @JvmStatic @Throws(IOException::class)
    fun copy(`in`: String?, out: Writer) {
        notNull(`in`, "No input String specified")
        notNull(out, "No Writer specified")
        try {
            out.write(`in`)
        } finally {
            close(out)
        }
    }

    /**
     * Copy the contents of the given Reader into a String.
     * Closes the reader when done.
     * @param in the reader to copy from (may be `null` or empty)
     * @return the String that has been copied to (possibly empty)
     * @throws IOException in case of I/O errors
     */
    @JvmStatic @Throws(IOException::class)
    fun copyToString(@Nullable `in`: Reader?): String {
        if (`in` == null) {
            return ""
        }
        val out = StringWriter(BUFFER_SIZE)
        copy(`in`, out)
        return out.toString()
    }

    /**
     * Attempt to close the supplied [Closeable], silently swallowing any
     * exceptions.
     * @param closeable the `Closeable` to close
     */
    private fun close(closeable: Closeable) {
        try {
            closeable.close()
        } catch (ex: IOException) {
            // ignore
        }
    }
}
