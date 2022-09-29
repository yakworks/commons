/*
* Copyright 2020 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.extensions

import java.nio.file.Files
import java.nio.file.Path

import groovy.transform.CompileStatic

import yakworks.commons.io.PathTools

/**
 * Extensions to the Path so that can use exists on it like file.
 */
@CompileStatic
class PathExtensions {

    /**
     * Tests whether a file exists.
     * @see Files#exists
     */
    static boolean exists(Path self) {
        return Files.exists(self)
    }

    /**
     * Files.createDirectories to ensure its created, checks if exists first.
     * @see Files#createDirectories
     * @throws IOException if an I/O error occurs
     */
    static Path mkdirs(Path self){
        if(!Files.exists(self)) return Files.createDirectories(self)
        return self
    }

    /**
     * Deletes a file or directory. If the path is a directory, delete it and all sub-directories. <br>
     * The difference between File.delete() and the deleteDir() extension: <br>
     * - A directory to delete does not have to be empty. <br>
     * - You get exceptions when a file or directory cannot be deleted; File.delete() returns a boolean. <br>
     * @return the count of files and dirs deleted
     * @throws IOException if an I/O error occurs
     */
    static int delete(Path self){
        def counter = PathTools.delete(self)
        return counter
    }
}
