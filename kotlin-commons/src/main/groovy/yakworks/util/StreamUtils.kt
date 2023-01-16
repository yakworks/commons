/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package yakworks.util

import jakarta.annotation.Nullable
import java.io.*
import java.nio.charset.Charset

/**
 * Simple utility methods for dealing with streams. The copy methods of this class are
 * similar to those defined in  FileCopyUtils except that all affected streams are
 * left open when done. All copy methods use a block size of 4096 bytes.
 *
 *
 * Mainly for use within the framework, but also useful for application code.
 *
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @author Brian Clozel
 * @since 3.2.2
 */
object StreamUtils {
    /**
     * The default buffer size used when copying bytes.
     */
    const val BUFFER_SIZE = 4096
    private val EMPTY_CONTENT = ByteArray(0)

    /**
     * Copy the contents of the given InputStream into a new byte array.
     *
     * Leaves the stream open when done.
     * @param in the stream to copy from (may be `null` or empty)
     * @return the new byte array that has been copied to (possibly empty)
     * @throws IOException in case of I/O errors
     */
    @JvmStatic
    @Throws(IOException::class)
    fun copyToByteArray(@Nullable `in`: InputStream?): ByteArray {
        if (`in` == null) {
            return ByteArray(0)
        }
        val out = ByteArrayOutputStream(BUFFER_SIZE)
        copy(`in`, out)
        return out.toByteArray()
    }

    /**
     * Copy the contents of the given InputStream into a String.
     *
     * Leaves the stream open when done.
     * @param in the InputStream to copy from (may be `null` or empty)
     * @param charset the [Charset] to use to decode the bytes
     * @return the String that has been copied to (possibly empty)
     * @throws IOException in case of I/O errors
     */
    @JvmStatic
    @Throws(IOException::class)
    fun copyToString(@Nullable `in`: InputStream?, charset: Charset?): String {
        if (`in` == null) {
            return ""
        }
        val out = StringBuilder(BUFFER_SIZE)
        val reader = InputStreamReader(`in`, charset)
        val buffer = CharArray(BUFFER_SIZE)
        var charsRead: Int
        while (reader.read(buffer).also { charsRead = it } != -1) {
            out.append(buffer, 0, charsRead)
        }
        return out.toString()
    }

    /**
     * Copy the contents of the given [ByteArrayOutputStream] into a [String].
     *
     * This is a more effective equivalent of `new String(baos.toByteArray(), charset)`.
     * @param baos the `ByteArrayOutputStream` to be copied into a String
     * @param charset the [Charset] to use to decode the bytes
     * @return the String that has been copied to (possibly empty)
     * @since 5.2.6
     */
    fun copyToString(baos: ByteArrayOutputStream, charset: Charset?): String {
        Assert.notNull(baos, "No ByteArrayOutputStream specified")
        Assert.notNull(charset, "No Charset specified")
        return try {
            // Can be replaced with toString(Charset) call in Java 10+
            baos.toString(charset!!.name())
        } catch (ex: UnsupportedEncodingException) {
            // Should never happen
            throw IllegalArgumentException("Invalid charset name: $charset", ex)
        }
    }

    /**
     * Copy the contents of the given byte array to the given OutputStream.
     *
     * Leaves the stream open when done.
     * @param in the byte array to copy from
     * @param out the OutputStream to copy to
     * @throws IOException in case of I/O errors
     */
    @JvmStatic
    @Throws(IOException::class)
    fun copy(`in`: ByteArray?, out: OutputStream) {
        Assert.notNull(`in`, "No input byte array specified")
        Assert.notNull(out, "No OutputStream specified")
        out.write(`in`)
        out.flush()
    }

    /**
     * Copy the contents of the given String to the given OutputStream.
     *
     * Leaves the stream open when done.
     * @param in the String to copy from
     * @param charset the Charset
     * @param out the OutputStream to copy to
     * @throws IOException in case of I/O errors
     */
    @JvmStatic
    @Throws(IOException::class)
    fun copy(`in`: String?, charset: Charset?, out: OutputStream?) {
        Assert.notNull(`in`, "No input String specified")
        Assert.notNull(charset, "No Charset specified")
        Assert.notNull(out, "No OutputStream specified")
        val writer: Writer = OutputStreamWriter(out, charset)
        writer.write(`in`)
        writer.flush()
    }

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     *
     * Leaves both streams open when done.
     * @param in the InputStream to copy from
     * @param out the OutputStream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    @JvmStatic
    @Throws(IOException::class)
    fun copy(`in`: InputStream, out: OutputStream): Int {
        Assert.notNull(`in`, "No InputStream specified")
        Assert.notNull(out, "No OutputStream specified")
        var byteCount = 0
        val buffer = ByteArray(BUFFER_SIZE)
        var bytesRead: Int
        while (`in`.read(buffer).also { bytesRead = it } != -1) {
            out.write(buffer, 0, bytesRead)
            byteCount += bytesRead
        }
        out.flush()
        return byteCount
    }

    /**
     * Copy a range of content of the given InputStream to the given OutputStream.
     *
     * If the specified range exceeds the length of the InputStream, this copies
     * up to the end of the stream and returns the actual number of copied bytes.
     *
     * Leaves both streams open when done.
     * @param in the InputStream to copy from
     * @param out the OutputStream to copy to
     * @param start the position to start copying from
     * @param end the position to end copying
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     * @since 4.3
     */
    @JvmStatic
    @Throws(IOException::class)
    fun copyRange(`in`: InputStream, out: OutputStream, start: Long, end: Long): Long {
        Assert.notNull(`in`, "No InputStream specified")
        Assert.notNull(out, "No OutputStream specified")
        val skipped = `in`.skip(start)
        if (skipped < start) {
            throw IOException("Skipped only $skipped bytes out of $start required")
        }
        var bytesToCopy = end - start + 1
        val buffer = ByteArray(Math.min(BUFFER_SIZE.toLong(), bytesToCopy).toInt())
        while (bytesToCopy > 0) {
            val bytesRead = `in`.read(buffer)
            if (bytesRead == -1) {
                break
            } else if (bytesRead <= bytesToCopy) {
                out.write(buffer, 0, bytesRead)
                bytesToCopy -= bytesRead.toLong()
            } else {
                out.write(buffer, 0, bytesToCopy.toInt())
                bytesToCopy = 0
            }
        }
        return end - start + 1 - bytesToCopy
    }

    /**
     * Drain the remaining content of the given InputStream.
     *
     * Leaves the InputStream open when done.
     * @param in the InputStream to drain
     * @return the number of bytes read
     * @throws IOException in case of I/O errors
     * @since 4.3
     */
    @Throws(IOException::class)
    fun drain(`in`: InputStream): Int {
        Assert.notNull(`in`, "No InputStream specified")
        val buffer = ByteArray(BUFFER_SIZE)
        var bytesRead = -1
        var byteCount = 0
        while (`in`.read(buffer).also { bytesRead = it } != -1) {
            byteCount += bytesRead
        }
        return byteCount
    }

    /**
     * Return an efficient empty [InputStream].
     * @return a [ByteArrayInputStream] based on an empty byte array
     * @since 4.2.2
     */
    fun emptyInput(): InputStream {
        return ByteArrayInputStream(EMPTY_CONTENT)
    }

    /**
     * Return a variant of the given [InputStream] where calling
     * [close()][InputStream.close] has no effect.
     * @param in the InputStream to decorate
     * @return a version of the InputStream that ignores calls to close
     */
    @JvmStatic
    fun nonClosing(`in`: InputStream?): InputStream {
        Assert.notNull(`in`, "No InputStream specified")
        return NonClosingInputStream(`in`)
    }

    /**
     * Return a variant of the given [OutputStream] where calling
     * [close()][OutputStream.close] has no effect.
     * @param out the OutputStream to decorate
     * @return a version of the OutputStream that ignores calls to close
     */
    @JvmStatic
    fun nonClosing(out: OutputStream?): OutputStream {
        Assert.notNull(out, "No OutputStream specified")
        return NonClosingOutputStream(out)
    }

    private class NonClosingInputStream(`in`: InputStream?) : FilterInputStream(`in`) {
        @Throws(IOException::class)
        override fun close() {
        }
    }

    private class NonClosingOutputStream(out: OutputStream?) : FilterOutputStream(out) {
        @Throws(IOException::class)
        override fun write(b: ByteArray, off: Int, let: Int) {
            // It is critical that we override this method for performance
            out.write(b, off, let)
        }

        @Throws(IOException::class)
        override fun close() {
        }
    }
}
