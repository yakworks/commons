package yakworks.util

import jakarta.annotation.Nullable
import yakworks.util.Assert.notNull
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*

/**
 * Utility methods for working with the file system.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.5.3
 * @see java.io.File
 *
 * @see java.nio.file.Path
 *
 * @see java.nio.file.Files
 */
object FileSystemUtils {
    /**
     * Delete the supplied [File] - for directories,
     * recursively delete any nested directories or files as well.
     *
     * Note: Like [File.delete], this method does not throw any
     * exception but rather silently returns `false` in case of I/O
     * errors. Consider using [.deleteRecursively] for NIO-style
     * handling of I/O errors, clearly differentiating between non-existence
     * and failure to delete an existing file.
     * @param root the root `File` to delete
     * @return `true` if the `File` was successfully deleted,
     * otherwise `false`
     */
    @JvmStatic
    fun deleteRecursively(root: File?): Boolean {
        return if (root == null) {
            false
        } else try {
            deleteRecursively(root.toPath())
        } catch (ex: IOException) {
            false
        }
    }

    /**
     * Delete the supplied [File]  for directories,
     * recursively delete any nested directories or files as well.
     * @param root the root `File` to delete
     * @return `true` if the `File` existed and was deleted,
     * or `false` if it did not exist
     * @throws IOException in the case of I/O errors
     * @since 5.0
     */
    @JvmStatic @Throws(IOException::class)
    fun deleteRecursively(root: Path?): Boolean {
        if (root == null) {
            return false
        }
        if (!Files.exists(root)) {
            return false
        }
        Files.walkFileTree(root, object : SimpleFileVisitor<Path>() {
            @Throws(IOException::class)
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                Files.delete(file)
                return FileVisitResult.CONTINUE
            }

            @Throws(IOException::class)
            override fun postVisitDirectory(dir: Path, exc: IOException): FileVisitResult {
                Files.delete(dir)
                return FileVisitResult.CONTINUE
            }
        })
        return true
    }

    /**
     * Recursively copy the contents of the `src` file/directory
     * to the `dest` file/directory.
     * @param src the source directory
     * @param dest the destination directory
     * @throws IOException in the case of I/O errors
     */
    @JvmStatic @Throws(IOException::class)
    fun copyRecursively(src: File, dest: File) {
        notNull(src, "Source File must not be null")
        notNull(dest, "Destination File must not be null")
        copyRecursively(src.toPath(), dest.toPath())
    }

    /**
     * Recursively copy the contents of the `src` file/directory
     * to the `dest` file/directory.
     * @param src the source directory
     * @param dest the destination directory
     * @throws IOException in the case of I/O errors
     * @since 5.0
     */
    @JvmStatic @Throws(IOException::class)
    fun copyRecursively(src: Path, dest: Path) {
        notNull(src, "Source Path must not be null")
        notNull(dest, "Destination Path must not be null")
        val srcAttr = Files.readAttributes(src, BasicFileAttributes::class.java)
        if (srcAttr.isDirectory) {
            Files.walkFileTree(
                src,
                EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                Int.MAX_VALUE,
                object : SimpleFileVisitor<Path>() {
                    @Throws(IOException::class)
                    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                        Files.createDirectories(dest.resolve(src.relativize(dir)))
                        return FileVisitResult.CONTINUE
                    }

                    @Throws(IOException::class)
                    override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                        Files.copy(file, dest.resolve(src.relativize(file)), StandardCopyOption.REPLACE_EXISTING)
                        return FileVisitResult.CONTINUE
                    }
                })
        } else if (srcAttr.isRegularFile) {
            Files.copy(src, dest)
        } else {
            throw IllegalArgumentException("Source File must denote a directory or file")
        }
    }
}
