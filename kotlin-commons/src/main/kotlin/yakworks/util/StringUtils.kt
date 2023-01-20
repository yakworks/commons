/*
 * Copyright 2002-2022 the original author or authors.
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
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Miscellaneous [String] utility methods.
 *
 *
 * Mainly for internal use within the framework; consider
 * [Apache's Commons Lang](https://commons.apache.org/proper/commons-lang/)
 * for a more comprehensive suite of `String` utilities.
 *
 *
 * This class delivers some simple functionality that should really be
 * provided by the core Java [String] and [StringBuilder]
 * classes. It also provides easy-to-use methods to convert between
 * delimited strings, such as CSV strings, and collections and arrays.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Keith Donald
 * @author Rob Harrop
 * @author Rick Evans
 * @author Arjen Poutsma
 * @author Sam Brannen
 * @author Brian Clozel
 * @since 16 April 2001
 */
object StringUtils {
    private val EMPTY_STRING_ARRAY = arrayOf<String>()
    private const val FOLDER_SEPARATOR = "/"
    private const val FOLDER_SEPARATOR_CHAR = '/'
    private const val WINDOWS_FOLDER_SEPARATOR = "\\"
    private const val TOP_PATH = ".."
    private const val CURRENT_PATH = "."
    private const val EXTENSION_SEPARATOR = '.'
    //---------------------------------------------------------------------
    // General convenience methods for working with Strings
    //---------------------------------------------------------------------
    /**
     * Check that the given `CharSequence` is neither `null` nor
     * of length 0.
     *
     * Note: this method returns `true` for a `CharSequence`
     * that purely consists of whitespace.
     *
     * <pre class="code">
     * StringUtils.hasLength(null) = false
     * StringUtils.hasLength("") = false
     * StringUtils.hasLength(" ") = true
     * StringUtils.hasLength("Hello") = true
    </pre> *
     * @param str the `CharSequence` to check (may be `null`)
     * @return `true` if the `CharSequence` is not `null` and has length
     * @see .hasLength
     * @see .hasText
     */
    @JvmStatic
    fun hasLength(str: CharSequence?): Boolean {
        return str != null && str.length > 0
    }

    /**
     * Check that the given `String` is neither `null` nor of length 0.
     *
     * Note: this method returns `true` for a `String` that
     * purely consists of whitespace.
     * @param str the `String` to check (may be `null`)
     * @return `true` if the `String` is not `null` and has length
     * @see .hasLength
     * @see .hasText
     */
    @JvmStatic
    fun hasLength(str: String?): Boolean {
        return str != null && !str.isEmpty()
    }

    /**
     * Check whether the given `CharSequence` contains actual *text*.
     *
     * More specifically, this method returns `true` if the
     * `CharSequence` is not `null`, its length is greater than
     * 0, and it contains at least one non-whitespace character.
     *
     * <pre class="code">
     * StringUtils.hasText(null) = false
     * StringUtils.hasText("") = false
     * StringUtils.hasText(" ") = false
     * StringUtils.hasText("12345") = true
     * StringUtils.hasText(" 12345 ") = true
    </pre> *
     * @param str the `CharSequence` to check (may be `null`)
     * @return `true` if the `CharSequence` is not `null`,
     * its length is greater than 0, and it does not contain whitespace only
     * @see .hasText
     * @see .hasLength
     * @see Character.isWhitespace
     */
    @JvmStatic
    fun hasText(str: CharSequence?): Boolean {
        return str != null && str.length > 0 && containsText(str)
    }

    /**
     * Check whether the given `String` contains actual *text*.
     *
     * More specifically, this method returns `true` if the
     * `String` is not `null`, its length is greater than 0,
     * and it contains at least one non-whitespace character.
     * @param str the `String` to check (may be `null`)
     * @return `true` if the `String` is not `null`, its
     * length is greater than 0, and it does not contain whitespace only
     * @see .hasText
     * @see .hasLength
     * @see Character.isWhitespace
     */
    @JvmStatic
    fun hasText(str: String?): Boolean {
        return str != null && !str.isEmpty() && containsText(str)
    }

    private fun containsText(str: CharSequence): Boolean {
        val strLen = str.length
        for (i in 0 until strLen) {
            if (!Character.isWhitespace(str[i])) {
                return true
            }
        }
        return false
    }

    /**
     * Check whether the given `CharSequence` contains any whitespace characters.
     * @param str the `CharSequence` to check (may be `null`)
     * @return `true` if the `CharSequence` is not empty and
     * contains at least 1 whitespace character
     * @see Character.isWhitespace
     */
    fun containsWhitespace(str: CharSequence): Boolean {
        if (str.isNullOrEmpty()) {
            return false
        }
        val strLen = str.length
        for (i in 0 until strLen) {
            if (Character.isWhitespace(str[i])) {
                return true
            }
        }
        return false
    }

    /**
     * Check whether the given `String` contains any whitespace characters.
     * @param str the `String` to check (may be `null`)
     * @return `true` if the `String` is not empty and
     * contains at least 1 whitespace character
     * @see .containsWhitespace
     */
    @JvmStatic
    fun containsWhitespace(str: String?): Boolean {
        if (str.isNullOrEmpty()) {
            return false
        }
        return containsWhitespace(str as CharSequence)
    }

    /**
     * Trim leading and trailing whitespace from the given `String`.
     * @param str the `String` to check
     * @return the trimmed `String`
     * @see java.lang.Character.isWhitespace
     */
    @JvmStatic
    fun trimWhitespace(str: String?): String? {
        if (str.isNullOrEmpty()) {
            return str
        }
        return str.trim()
    }

    /**
     * Trim *all* whitespace from the given `CharSequence`:
     * leading, trailing, and in between characters.
     * @param text the `CharSequence` to check
     * @return the trimmed `CharSequence`
     * @since 5.3.22
     * @see .trimAllWhitespace
     * @see java.lang.Character.isWhitespace
     */
    fun trimAllWhitespace(text: CharSequence): CharSequence {
        if (!hasLength(text)) {
            return text
        }
        val len = text.length
        val sb = StringBuilder(text.length)
        for (i in 0 until len) {
            val c = text[i]
            if (!Character.isWhitespace(c)) {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    /**
     * Trim *all* whitespace from the given `String`:
     * leading, trailing, and in between characters.
     * @param str the `String` to check
     * @return the trimmed `String`
     * @see .trimAllWhitespace
     * @see java.lang.Character.isWhitespace
     */
    @JvmStatic
    fun trimAllWhitespace(str: String?): String? {
        return if (str == null) {
            null
        } else trimAllWhitespace(str as CharSequence).toString()
    }

    /**
     * Trim leading whitespace from the given `String`.
     * @param str the `String` to check
     * @return the trimmed `String`
     * @see java.lang.Character.isWhitespace
     */
    @JvmStatic
    fun trimLeadingWhitespace(str: String?): String? {
        if (str.isNullOrEmpty()) {
            return str
        }
        return str.trimStart()
    }

    /**
     * Trim trailing whitespace from the given `String`.
     * @param str the `String` to check
     * @return the trimmed `String`
     * @see java.lang.Character.isWhitespace
     */
    @JvmStatic
    fun trimTrailingWhitespace(str: String?): String? {
        if (str.isNullOrEmpty()) {
            return str
        }
        return str.trimEnd()
    }

    /**
     * Trim all occurrences of the supplied leading character from the given `String`.
     * @param str the `String` to check
     * @param leadingCharacter the leading character to be trimmed
     * @return the trimmed `String`
     */
    @JvmStatic
    fun trimLeadingCharacter(str: String?, leadingCharacter: Char): String? {
        if (str.isNullOrEmpty()) {
            return str
        }
        var beginIdx = 0
        while (beginIdx < str.length && leadingCharacter == str[beginIdx]) {
            beginIdx++
        }
        return str.substring(beginIdx)
    }

    /**
     * Trim all occurrences of the supplied trailing character from the given `String`.
     * @param str the `String` to check
     * @param trailingCharacter the trailing character to be trimmed
     * @return the trimmed `String`
     */
    @JvmStatic
    fun trimTrailingCharacter(str: String?, trailingCharacter: Char): String? {
        if (str.isNullOrEmpty()) {
            return str
        }
        var endIdx = str.length - 1
        while (endIdx >= 0 && trailingCharacter == str[endIdx]) {
            endIdx--
        }
        return str.substring(0, endIdx + 1)
    }

    /**
     * Test if the given `String` matches the given single character.
     * @param str the `String` to check
     * @param singleCharacter the character to compare to
     * @since 5.2.9
     */
    @JvmStatic
    fun matchesCharacter(str: String?, singleCharacter: Char): Boolean {
        return str != null && str.length == 1 && str[0] == singleCharacter
    }

    /**
     * Test if the given `String` starts with the specified prefix,
     * ignoring upper/lower case.
     * @param str the `String` to check
     * @param prefix the prefix to look for
     * @see java.lang.String.startsWith
     */
    @JvmStatic
    fun startsWithIgnoreCase(str: String?, prefix: String?): Boolean {
        return str != null && prefix != null && str.length >= prefix.length &&
                str.regionMatches(0, prefix, 0, prefix.length, ignoreCase = true)
    }

    /**
     * Test if the given `String` ends with the specified suffix,
     * ignoring upper/lower case.
     * @param str the `String` to check
     * @param suffix the suffix to look for
     * @see java.lang.String.endsWith
     */
    @JvmStatic
    fun endsWithIgnoreCase(str: String?, suffix: String?): Boolean {
        return str != null && suffix != null && str.length >= suffix.length &&
                str.regionMatches(str.length - suffix.length, suffix, 0, suffix.length, ignoreCase = true)
    }

    /**
     * Test whether the given string matches the given substring
     * at the given index.
     * @param str the original string (or StringBuilder)
     * @param index the index in the original string to start matching against
     * @param substring the substring to match at the given index
     */
    @JvmStatic
    fun substringMatch(str: CharSequence, index: Int, substring: CharSequence): Boolean {
        if (index + substring.length > str.length) {
            return false
        }
        for (i in 0 until substring.length) {
            if (str[index + i] != substring[i]) {
                return false
            }
        }
        return true
    }

    /**
     * Count the occurrences of the substring `sub` in string `str`.
     * @param str string to search in
     * @param sub string to search for
     */
    @JvmStatic
    fun countOccurrencesOf(str: String?, sub: String?): Int {
        if (str.isNullOrEmpty() || sub.isNullOrEmpty()) {
            return 0
        }
        var count = 0
        var pos = 0
        var idx: Int
        while (str.indexOf(sub, pos).also { idx = it } != -1) {
            ++count
            pos = idx + sub.length
        }
        return count
    }

    /**
     * Replace all occurrences of a substring within a string with another string.
     * @param inString `String` to examine
     * @param oldPattern `String` to replace
     * @param newPattern `String` to insert
     * @return a `String` with the replacements
     */
    @JvmStatic
    fun replace(inString: String, oldPattern: String, newPattern: String?): String {
        if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
            return inString
        }
        var index = inString.indexOf(oldPattern)
        if (index == -1) {
            // no occurrence -> can return input as-is
            return inString
        }
        var capacity = inString.length
        if (newPattern.length > oldPattern.length) {
            capacity += 16
        }
        val sb = StringBuilder(capacity)
        var pos = 0 // our position in the old string
        val patLen = oldPattern.length
        while (index >= 0) {
            sb.append(inString, pos, index)
            sb.append(newPattern)
            pos = index + patLen
            index = inString.indexOf(oldPattern, pos)
        }

        // append any characters to the right of a match
        sb.append(inString, pos, inString.length)
        return sb.toString()
    }

    /**
     * Delete all occurrences of the given substring.
     * @param inString the original `String`
     * @param pattern the pattern to delete all occurrences of
     * @return the resulting `String`
     */
    @JvmStatic
    fun delete(inString: String, pattern: String): String {
        return replace(inString, pattern, "")
    }

    /**
     * Delete any character in a given `String`.
     * @param inString the original `String`
     * @param charsToDelete a set of characters to delete.
     * E.g. "az\n" will delete 'a's, 'z's and new lines.
     * @return the resulting `String`
     */
    @JvmStatic
    fun deleteAny(inString: String, charsToDelete: String?): String {
        if (!hasLength(inString) || !hasLength(charsToDelete)) {
            return inString
        }
        var lastCharIndex = 0
        val result = CharArray(inString.length)
        for (i in 0 until inString.length) {
            val c = inString[i]
            if (charsToDelete!!.indexOf(c) == -1) {
                result[lastCharIndex++] = c
            }
        }
        return if (lastCharIndex == inString.length) {
            inString
        } else String(result, 0, lastCharIndex)
    }
    //---------------------------------------------------------------------
    // Convenience methods for working with formatted Strings
    //---------------------------------------------------------------------
    /**
     * Quote the given `String` with single quotes.
     * @param str the input `String` (e.g. "myString")
     * @return the quoted `String` (e.g. "'myString'"),
     * or `null` if the input was `null`
     */
    @JvmStatic
    fun quote(str: String?): String? {
        return if (str != null) "'$str'" else null
    }

    /**
     * Turn the given Object into a `String` with single quotes
     * if it is a `String`; keeping the Object as-is else.
     * @param obj the input Object (e.g. "myString")
     * @return the quoted `String` (e.g. "'myString'"),
     * or the input object as-is if not a `String`
     */
    @JvmStatic
    fun quoteIfString(obj: Any?): Any? {
        return if (obj is String) quote(obj as String?) else obj
    }
    /**
     * Unqualify a string qualified by a separator character. For example,
     * "this:name:is:qualified" returns "qualified" if using a ':' separator.
     * @param qualifiedName the qualified name
     * @param separator the separator
     */
    /**
     * Unqualify a string qualified by a '.' dot character. For example,
     * "this.name.is.qualified", returns "qualified".
     * @param qualifiedName the qualified name
     */
    @JvmOverloads
    @JvmStatic
    fun unqualify(qualifiedName: String, separator: Char = '.'): String {
        return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1)
    }

    /**
     * Capitalize a `String`, changing the first letter to
     * upper case as per [Character.toUpperCase].
     * No other letters are changed.
     * @param str the `String` to capitalize
     * @return the capitalized `String`
     */
    @JvmStatic
    fun capitalize(str: String): String {
        return changeFirstCharacterCase(str, true)
    }

    /**
     * Uncapitalize a `String`, changing the first letter to
     * lower case as per [Character.toLowerCase].
     * No other letters are changed.
     * @param str the `String` to uncapitalize
     * @return the uncapitalized `String`
     */
    @JvmStatic
    fun uncapitalize(str: String): String {
        return changeFirstCharacterCase(str, false)
    }

    private fun changeFirstCharacterCase(str: String, capitalize: Boolean): String {
        if (!hasLength(str)) {
            return str
        }
        val baseChar = str[0]
        val updatedChar: Char
        updatedChar = if (capitalize) {
            baseChar.uppercaseChar()
        } else {
            baseChar.lowercaseChar()
        }
        if (baseChar == updatedChar) {
            return str
        }
        val chars = str.toCharArray()
        chars[0] = updatedChar
        return String(chars)
    }

    /**
     * Extract the filename from the given Java resource path,
     * e.g. `"mypath/myfile.txt" &rarr; "myfile.txt"`.
     * @param path the file path (may be `null`)
     * @return the extracted filename, or `null` if none
     */
    @JvmStatic
    fun getFilename(path: String?): String? {
        if (path == null) {
            return null
        }
        val separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR_CHAR)
        return if (separatorIndex != -1) path.substring(separatorIndex + 1) else path
    }

    /**
     * Extract the filename extension from the given Java resource path,
     * e.g. "mypath/myfile.txt"  "txt".
     * @param path the file path (may be `null`)
     * @return the extracted filename extension, or `null` if none
     */
    @JvmStatic
    fun getFilenameExtension(path: String?): String? {
        if (path == null) {
            return null
        }
        val extIndex = path.lastIndexOf(EXTENSION_SEPARATOR)
        if (extIndex == -1) {
            return null
        }
        val folderIndex = path.lastIndexOf(FOLDER_SEPARATOR_CHAR)
        return if (folderIndex > extIndex) {
            null
        } else path.substring(extIndex + 1)
    }

    /**
     * Strip the filename extension from the given Java resource path,
     * e.g. "mypath/myfile.txt"  "mypath/myfile".
     * @param path the file path
     * @return the path with stripped filename extension
     */
    @JvmStatic
    fun stripFilenameExtension(path: String): String {
        val extIndex = path.lastIndexOf(EXTENSION_SEPARATOR)
        if (extIndex == -1) {
            return path
        }
        val folderIndex = path.lastIndexOf(FOLDER_SEPARATOR_CHAR)
        return if (folderIndex > extIndex) {
            path
        } else path.substring(0, extIndex)
    }

    /**
     * Apply the given relative path to the given Java resource path,
     * assuming standard Java folder separation (i.e. "/" separators).
     * @param path the path to start from (usually a full file path)
     * @param relativePath the relative path to apply
     * (relative to the full file path above)
     * @return the full file path that results from applying the relative path
     */
    fun applyRelativePath(path: String, relativePath: String): String {
        val separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR_CHAR)
        return if (separatorIndex != -1) {
            var newPath = path.substring(0, separatorIndex)
            if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
                newPath += FOLDER_SEPARATOR_CHAR
            }
            newPath + relativePath
        } else {
            relativePath
        }
    }

    /**
     * Normalize the path by suppressing sequences like "path/.." and
     * inner simple dots.
     *
     * The result is convenient for path comparison. For other uses,
     * notice that Windows separators ("\") are replaced by simple slashes.
     *
     * **NOTE** that `cleanPath` should not be depended
     * upon in a security context. Other mechanisms should be used to prevent
     * path-traversal issues.
     * @param path the original path
     * @return the normalized path
     */
    @JvmStatic
    fun cleanPath(path: String): String {
        if (!hasLength(path)) {
            return path
        }
        val normalizedPath = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR)
        var pathToUse = normalizedPath

        // Shortcut if there is no work to do
        if (pathToUse.indexOf('.') == -1) {
            return pathToUse
        }

        // Strip prefix from path to analyze, to not treat it as part of the
        // first path element. This is necessary to correctly parse paths like
        // "file:core/../core/io/Resource.class", where the ".." should just
        // strip the first "core" directory while keeping the "file:" prefix.
        val prefixIndex = pathToUse.indexOf(':')
        var prefix = ""
        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1)
            if (prefix.contains(FOLDER_SEPARATOR)) {
                prefix = ""
            } else {
                pathToUse = pathToUse.substring(prefixIndex + 1)
            }
        }
        if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
            prefix = prefix + FOLDER_SEPARATOR
            pathToUse = pathToUse.substring(1)
        }
        val pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR)
        // we never require more elements than pathArray and in the common case the same number
        val pathElements: Deque<String> = ArrayDeque(pathArray.size)
        var tops = 0
        for (i in pathArray.indices.reversed()) {
            val element = pathArray[i]
            if (CURRENT_PATH == element) {
                // Points to current directory - drop it.
            } else if (TOP_PATH == element) {
                // Registering top path found.
                tops++
            } else {
                if (tops > 0) {
                    // Merging path element with element corresponding to top path.
                    tops--
                } else {
                    // Normal path element found.
                    pathElements.addFirst(element)
                }
            }
        }

        // All path elements stayed the same - shortcut
        if (pathArray.size == pathElements.size) {
            return normalizedPath
        }
        // Remaining top paths need to be retained.
        for (i in 0 until tops) {
            pathElements.addFirst(TOP_PATH)
        }
        // If nothing else left, at least explicitly point to current path.
        if (pathElements.size == 1 && pathElements.last.isEmpty() && !prefix.endsWith(FOLDER_SEPARATOR)) {
            pathElements.addFirst(CURRENT_PATH)
        }
        val joined = collectionToDelimitedString(pathElements, FOLDER_SEPARATOR)
        // avoid string concatenation with empty prefix
        return if (prefix.isEmpty()) joined else prefix + joined
    }

    /**
     * Compare two paths after normalization of them.
     * @param path1 first path for comparison
     * @param path2 second path for comparison
     * @return whether the two paths are equivalent after normalization
     */
    @JvmStatic
    fun pathEquals(path1: String, path2: String): Boolean {
        return cleanPath(path1) == cleanPath(path2)
    }

    /**
     * Decode the given encoded URI component value. Based on the following rules:
     *
     *  * Alphanumeric characters `"a"` through `"z"`, `"A"` through `"Z"`,
     * and `"0"` through `"9"` stay the same.
     *  * Special characters `"-"`, `"_"`, `"."`, and `"*"` stay the same.
     *  * A sequence "`%<i>xy</i>`" is interpreted as a hexadecimal representation of the character.
     *
     * @param source the encoded String
     * @param charset the character set
     * @return the decoded value
     * @throws IllegalArgumentException when the given source contains invalid encoded sequences
     * @since 5.0
     * @see java.net.URLDecoder.decode
     */
    fun uriDecode(source: String, charset: Charset = StandardCharsets.UTF_8): String {
        val length = source.length
        if (length == 0) {
            return source
        }
        Assert.notNull(charset, "Charset must not be null")
        val baos = ByteArrayOutputStream(length)
        var changed = false
        var i = 0
        while (i < length) {
            val ch = source[i].code
            if (ch == '%'.code) {
                if (i + 2 < length) {
                    val hex1 = source[i + 1]
                    val hex2 = source[i + 2]
                    val u = hex1.digitToIntOrNull(16) ?: -1
                    val l = hex2.digitToIntOrNull(16) ?: -1
                    require(!(u == -1 || l == -1)) { "Invalid encoded sequence \"" + source.substring(i) + "\"" }
                    baos.write(((u shl 4) + l).toChar().code)
                    i += 2
                    changed = true
                } else {
                    throw IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"")
                }
            } else {
                baos.write(ch)
            }
            i++
        }
        return if (changed) StreamUtils.copyToString(baos, charset) else source
    }

    /**
     * Parse the given `String` value into a [Locale], accepting
     * the [Locale.toString] format as well as BCP 47 language tags as
     * specified by [Locale.forLanguageTag].
     * @param localeValue the locale value: following either `Locale's`
     * `toString()` format ("en", "en_UK", etc), also accepting spaces as
     * separators (as an alternative to underscores), or BCP 47 (e.g. "en-UK")
     * @return a corresponding `Locale` instance, or `null` if none
     * @throws IllegalArgumentException in case of an invalid locale specification
     * @since 5.0.4
     * @see .parseLocaleString
     *
     * @see Locale.forLanguageTag
     */
    @JvmStatic
    fun parseLocale(localeValue: String): Locale? {
        val tokens = tokenizeLocaleSource(localeValue)
        if (tokens.size == 1) {
            validateLocalePart(localeValue)
            val resolved = Locale.forLanguageTag(localeValue)
            if (resolved.language.length > 0) {
                return resolved
            }
        }
        return parseLocaleTokens(localeValue, tokens)
    }

    /**
     * Parse the given `String` representation into a [Locale].
     *
     * For many parsing scenarios, this is an inverse operation of
     * [Locale&#39;s toString][Locale.toString], in a lenient sense.
     * This method does not aim for strict `Locale` design compliance;
     * it is rather specifically tailored for typical Spring parsing needs.
     *
     * **Note: This delegate does not accept the BCP 47 language tag format.
     * Please use [.parseLocale] for lenient parsing of both formats.**
     * @param localeString the locale `String`: following `Locale's`
     * `toString()` format ("en", "en_UK", etc), also accepting spaces as
     * separators (as an alternative to underscores)
     * @return a corresponding `Locale` instance, or `null` if none
     * @throws IllegalArgumentException in case of an invalid locale specification
     */
    @JvmStatic
    fun parseLocaleString(localeString: String): Locale? {
        return parseLocaleTokens(localeString, tokenizeLocaleSource(localeString))
    }

    private fun tokenizeLocaleSource(localeSource: String): Array<String> {
        return tokenizeToStringArray(localeSource, "_ ", false, false)
    }

    private fun parseLocaleTokens(localeString: String, tokens: Array<String>): Locale? {
        val language = if (tokens.size > 0) tokens[0] else ""
        var country = if (tokens.size > 1) tokens[1] else ""
        validateLocalePart(language)
        validateLocalePart(country)
        var variant = ""
        if (tokens.size > 2) {
            // There is definitely a variant, and it is everything after the country
            // code sans the separator between the country code and the variant.
            val endIndexOfCountryCode = localeString.indexOf(country, language.length) + country.length
            // Strip off any leading '_' and whitespace, what's left is the variant.
            variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode))!!
            if (variant.startsWith("_")) {
                variant = trimLeadingCharacter(variant, '_')!!
            }
        }
        if (variant.isEmpty() && country.startsWith("#")) {
            variant = country
            country = ""
        }
        return if (language.length > 0) Locale(language, country, variant) else null
    }

    private fun validateLocalePart(localePart: String) {
        for (i in 0 until localePart.length) {
            val ch = localePart[i]
            require(!(ch != ' ' && ch != '_' && ch != '-' && ch != '#' && !Character.isLetterOrDigit(ch))) { "Locale part \"$localePart\" contains invalid characters" }
        }
    }

    /**
     * Parse the given `timeZoneString` value into a [TimeZone].
     * @param timeZoneString the time zone `String`, following [TimeZone.getTimeZone]
     * but throwing [IllegalArgumentException] in case of an invalid time zone specification
     * @return a corresponding [TimeZone] instance
     * @throws IllegalArgumentException in case of an invalid time zone specification
     */
    fun parseTimeZoneString(timeZoneString: String): TimeZone {
        val timeZone = TimeZone.getTimeZone(timeZoneString)
        require(!("GMT" == timeZone.id && !timeZoneString.startsWith("GMT"))) {
            // We don't want that GMT fallback...
            "Invalid time zone specification '$timeZoneString'"
        }
        return timeZone
    }
    //---------------------------------------------------------------------
    // Convenience methods for working with String arrays
    //---------------------------------------------------------------------
    /**
     * Copy the given [Collection] into a `String` array.
     *
     * The `Collection` must contain `String` elements only.
     * @param collection the `Collection` to copy
     * (potentially `null` or empty)
     * @return the resulting `String` array
     */
    fun toStringArray(collection: Collection<String?>): Array<String> {
        return if (!collection.isEmpty()) {
            collection.stream().toArray { arrayOfNulls<String>(it) }
        } else {
            EMPTY_STRING_ARRAY
        }
    }

    /**
     * Copy the given [Enumeration] into a `String` array.
     *
     * The `Enumeration` must contain `String` elements only.
     * @param enumeration the `Enumeration` to copy
     * (potentially `null` or empty)
     * @return the resulting `String` array
     */
    @JvmStatic
    fun toStringArray(enumeration: Enumeration<String?>?): Array<String> {
        return if (enumeration != null) toStringArray(Collections.list(enumeration)) else EMPTY_STRING_ARRAY
    }

    /**
     * Append the given `String` to the given `String` array,
     * returning a new array consisting of the input array contents plus
     * the given `String`.
     * @param array the array to append to (can be `null`)
     * @param str the `String` to append
     * @return the new array (never `null`)
     */
    @JvmStatic
    fun addStringToArray(array: Array<String?>, str: String?): Array<String?> {
        if (ObjectUtils.isEmpty(array)) {
            return arrayOf(str)
        }
        val newArr = arrayOfNulls<String>(array.size + 1)
        System.arraycopy(array, 0, newArr, 0, array.size)
        newArr[array.size] = str
        return newArr
    }

    /**
     * Concatenate the given `String` arrays into one,
     * with overlapping array elements included twice.
     *
     * The order of elements in the original arrays is preserved.
     * @param array1 the first array (can be `null`)
     * @param array2 the second array (can be `null`)
     * @return the new array (`null` if both given arrays were `null`)
     */
    @JvmStatic
    fun concatenateStringArrays(array1: Array<String?>?, array2: Array<String?>?): Array<String?>? {
        if (array1.isNullOrEmpty()) {
            return array2
        }
        if (array2.isNullOrEmpty()) {
            return array1
        }
        val newArr = arrayOfNulls<String>(array1.size + array2.size)
        System.arraycopy(array1, 0, newArr, 0, array1.size)
        System.arraycopy(array2, 0, newArr, array1.size, array2.size)
        return newArr
    }

    /**
     * Sort the given `String` array if necessary.
     * @param array the original array (potentially empty)
     * @return the array in sorted form (never `null`)
     */
    @JvmStatic
    fun sortStringArray(array: Array<String>): Array<String> {
        if (ObjectUtils.isEmpty(array)) {
            return array
        }
        Arrays.sort(array)
        return array
    }

    /**
     * Trim the elements of the given `String` array, calling
     * `String.trim()` on each non-null element.
     * @param array the original `String` array (potentially empty)
     * @return the resulting array (of the same size) with trimmed elements
     */
    fun trimArrayElements(array: Array<String?>): Array<String?> {
        if (ObjectUtils.isEmpty(array)) {
            return array
        }
        val result = arrayOfNulls<String>(array.size)
        for (i in array.indices) {
            val element = array[i]
            result[i] = element?.trim { it <= ' ' }
        }
        return result
    }

    /**
     * Remove duplicate strings from the given array.
     *
     * As of 4.2, it preserves the original order, as it uses a [LinkedHashSet].
     * @param array the `String` array (potentially empty)
     * @return an array without duplicates, in natural sort order
     */
    @JvmStatic
    fun removeDuplicateStrings(array: Array<String>): Array<String> {
        if (ObjectUtils.isEmpty(array)) {
            return array
        }
        val set: Set<String?> = LinkedHashSet(Arrays.asList(*array))
        return toStringArray(set)
    }

    /**
     * Split a `String` at the first occurrence of the delimiter.
     * Does not include the delimiter in the result.
     * @param toSplit the string to split (potentially `null` or empty)
     * @param delimiter to split the string up with (potentially `null` or empty)
     * @return a two element array with index 0 being before the delimiter, and
     * index 1 being after the delimiter (neither element includes the delimiter);
     * or `null` if the delimiter wasn't found in the given input `String`
     */
    @JvmStatic
    fun split(toSplit: String?, delimiter: String?): Array<String>? {
        if (toSplit.isNullOrEmpty() || delimiter.isNullOrEmpty()) {
            return null
        }
        val offset = toSplit.indexOf(delimiter)
        if (offset < 0) {
            return null
        }
        val beforeDelimiter = toSplit.substring(0, offset)
        val afterDelimiter = toSplit.substring(offset + delimiter.length)
        return arrayOf(beforeDelimiter, afterDelimiter)
    }

    /**
     * Take an array of strings and split each element based on the given delimiter.
     * A `Properties` instance is then generated, with the left of the delimiter
     * providing the key, and the right of the delimiter providing the value.
     *
     * Will trim both the key and value before adding them to the `Properties`.
     * @param array the array to process
     * @param delimiter to split each element using (typically the equals symbol)
     * @return a `Properties` instance representing the array contents,
     * or `null` if the array to process was `null` or empty
     */
    @JvmStatic
    fun splitArrayElementsIntoProperties(array: Array<String>, delimiter: String): Properties? {
        return splitArrayElementsIntoProperties(array, delimiter, null)
    }

    /**
     * Take an array of strings and split each element based on the given delimiter.
     * A `Properties` instance is then generated, with the left of the
     * delimiter providing the key, and the right of the delimiter providing the value.
     *
     * Will trim both the key and value before adding them to the
     * `Properties` instance.
     * @param array the array to process
     * @param delimiter to split each element using (typically the equals symbol)
     * @param charsToDelete one or more characters to remove from each element
     * prior to attempting the split operation (typically the quotation mark
     * symbol), or `null` if no removal should occur
     * @return a `Properties` instance representing the array contents,
     * or `null` if the array to process was `null` or empty
     */
    @JvmStatic
    fun splitArrayElementsIntoProperties(
        array: Array<String>, delimiter: String, charsToDelete: String?
    ): Properties? {
        if (ObjectUtils.isEmpty(array)) {
            return null
        }
        val result = Properties()
        for (element in array) {
            var el = element
            if (charsToDelete != null) {
                el = deleteAny(element, charsToDelete)
            }
            val splittedElement = split(el, delimiter) ?: continue
            result.setProperty(splittedElement[0].trim { it <= ' ' }, splittedElement[1].trim { it <= ' ' })
        }
        return result
    }
    /**
     * Tokenize the given `String` into a `String` array via a
     * [StringTokenizer].
     *
     * The given `delimiters` string can consist of any number of
     * delimiter characters. Each of those characters can be used to separate
     * tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using [.delimitedListToStringArray].
     * @param str the `String` to tokenize (potentially `null` or empty)
     * @param delimiters the delimiter characters, assembled as a `String`
     * (each of the characters is individually considered as a delimiter)
     * @param trimTokens trim the tokens via [String.trim]
     * @param ignoreEmptyTokens omit empty tokens from the result array
     * (only applies to tokens that are empty after trimming; StringTokenizer
     * will not consider subsequent delimiters as token in the first place).
     * @return an array of the tokens
     * @see java.util.StringTokenizer
     *
     * @see String.trim
     * @see .delimitedListToStringArray
     */
    /**
     * Tokenize the given `String` into a `String` array via a
     * [StringTokenizer].
     *
     * Trims tokens and omits empty tokens.
     *
     * The given `delimiters` string can consist of any number of
     * delimiter characters. Each of those characters can be used to separate
     * tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using [.delimitedListToStringArray].
     * @param str the `String` to tokenize (potentially `null` or empty)
     * @param delimiters the delimiter characters, assembled as a `String`
     * (each of the characters is individually considered as a delimiter)
     * @return an array of the tokens
     * @see java.util.StringTokenizer
     *
     * @see String.trim
     * @see .delimitedListToStringArray
     */
    @JvmStatic
    @JvmOverloads
    fun tokenizeToStringArray(
        str: String?, delimiters: String?, trimTokens: Boolean = true, ignoreEmptyTokens: Boolean = true
    ): Array<String> {
        if (str == null) {
            return EMPTY_STRING_ARRAY
        }
        val st = StringTokenizer(str, delimiters)
        val tokens: MutableList<String?> = ArrayList()
        while (st.hasMoreTokens()) {
            var token = st.nextToken()
            if (trimTokens) {
                token = token.trim { it <= ' ' }
            }
            if (!ignoreEmptyTokens || token.length > 0) {
                tokens.add(token)
            }
        }
        return toStringArray(tokens)
    }
    /**
     * Take a `String` that is a delimited list and convert it into
     * a `String` array.
     *
     * A single `delimiter` may consist of more than one character,
     * but it will still be considered as a single delimiter string, rather
     * than as a bunch of potential delimiter characters, in contrast to
     * [.tokenizeToStringArray].
     * @param str the input `String` (potentially `null` or empty)
     * @param delimiter the delimiter between elements (this is a single delimiter,
     * rather than a bunch individual delimiter characters)
     * @param charsToDelete a set of characters to delete; useful for deleting unwanted
     * line breaks: e.g. "\r\n\f" will delete all new lines and line feeds in a `String`
     * @return an array of the tokens in the list
     * @see .tokenizeToStringArray
     */
    /**
     * Take a `String` that is a delimited list and convert it into a
     * `String` array.
     *
     * A single `delimiter` may consist of more than one character,
     * but it will still be considered as a single delimiter string, rather
     * than as a bunch of potential delimiter characters, in contrast to
     * [.tokenizeToStringArray].
     * @param str the input `String` (potentially `null` or empty)
     * @param delimiter the delimiter between elements (this is a single delimiter,
     * rather than a bunch individual delimiter characters)
     * @return an array of the tokens in the list
     * @see .tokenizeToStringArray
     */
    @JvmOverloads @JvmStatic
    fun delimitedListToStringArray(
        str: String?, delimiter: String?, charsToDelete: String? = null
    ): Array<String> {
        if (str == null) {
            return EMPTY_STRING_ARRAY
        }
        if (delimiter == null) {
            return arrayOf(str)
        }
        val result: MutableList<String?> = ArrayList()
        if (delimiter.isEmpty()) {
            for (i in 0 until str.length) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete))
            }
        } else {
            var pos = 0
            var delPos: Int
            while (str.indexOf(delimiter, pos).also { delPos = it } != -1) {
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete))
                pos = delPos + delimiter.length
            }
            if (str.length > 0 && pos <= str.length) {
                // Add rest of String, but not in case of empty input.
                result.add(deleteAny(str.substring(pos), charsToDelete))
            }
        }
        return toStringArray(result)
    }

    /**
     * Convert a comma delimited list (e.g., a row from a CSV file) into an
     * array of strings.
     * @param str the input `String` (potentially `null` or empty)
     * @return an array of strings, or the empty array in case of empty input
     */
    @JvmStatic
    fun commaDelimitedListToStringArray(str: String?): Array<String> {
        return delimitedListToStringArray(str, ",")
    }

    /**
     * Convert a comma delimited list (e.g., a row from a CSV file) into a set.
     *
     * Note that this will suppress duplicates, and as of 4.2, the elements in
     * the returned set will preserve the original order in a [LinkedHashSet].
     * @param str the input `String` (potentially `null` or empty)
     * @return a set of `String` entries in the list
     * @see .removeDuplicateStrings
     */
    fun commaDelimitedListToSet(str: String?): Set<String> {
        val tokens = commaDelimitedListToStringArray(str)
        return LinkedHashSet(Arrays.asList(*tokens))
    }
    /**
     * Convert a [Collection] to a delimited `String` (e.g. CSV).
     *
     * Useful for `toString()` implementations.
     * @param coll the `Collection` to convert (potentially `null` or empty)
     * @param delim the delimiter to use (typically a ",")
     * @param prefix the `String` to start each element with
     * @param suffix the `String` to end each element with
     * @return the delimited `String`
     */
    /**
     * Convert a `Collection` into a delimited `String` (e.g. CSV).
     *
     * Useful for `toString()` implementations.
     * @param coll the `Collection` to convert (potentially `null` or empty)
     * @param delim the delimiter to use (typically a ",")
     * @return the delimited `String`
     */
    @JvmOverloads
    fun collectionToDelimitedString(coll: Collection<*>, delim: String, prefix: String = "", suffix: String = ""): String {
        if (coll.isEmpty()) {
            return ""
        }
        var totalLength = coll.size * (prefix.length + suffix.length) + (coll.size - 1) * delim.length
        for (element in coll) {
            totalLength += element.toString().length
        }
        val sb = StringBuilder(totalLength)
        val it = coll.iterator()
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix)
            if (it.hasNext()) {
                sb.append(delim)
            }
        }
        return sb.toString()
    }

    /**
     * Convert a `Collection` into a delimited `String` (e.g., CSV).
     *
     * Useful for `toString()` implementations.
     * @param coll the `Collection` to convert (potentially `null` or empty)
     * @return the delimited `String`
     */
    @JvmStatic
    fun collectionToCommaDelimitedString(coll: Collection<*>): String {
        return collectionToDelimitedString(coll, ",")
    }

    /**
     * Convert a `String` array into a delimited `String` (e.g. CSV).
     *
     * Useful for `toString()` implementations.
     * @param arr the array to display (potentially `null` or empty)
     * @param delim the delimiter to use (typically a ",")
     * @return the delimited `String`
     */
    fun arrayToDelimitedString(arr: Array<Any>, delim: String?): String {
        if (ObjectUtils.isEmpty(arr)) {
            return ""
        }
        if (arr.size == 1) {
            return ObjectUtils.nullSafeToString(arr[0])
        }
        val sj = StringJoiner(delim)
        for (elem in arr) {
            sj.add(elem.toString())
        }
        return sj.toString()
    }

    /**
     * Convert a `String` array into a comma delimited `String`
     * (i.e., CSV).
     *
     * Useful for `toString()` implementations.
     * @param arr the array to display (potentially `null` or empty)
     * @return the delimited `String`
     */
    @JvmStatic
    fun arrayToCommaDelimitedString(arr: Array<Any>): String {
        return arrayToDelimitedString(arr, ",")
    }
}
