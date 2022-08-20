/*
* Copyright 2020 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.extensions

import java.nio.file.Files
import java.nio.file.Path

import groovy.transform.CompileStatic

/**
 * Extensions to the Path so that can use exists on it like file.
 */
@CompileStatic
class PathExtensions {

    static boolean exists(Path self) {
        return Files.exists(self)
    }

}
