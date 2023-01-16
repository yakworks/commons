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

/**
 * Miscellaneous collection utility methods.
 * Mainly for internal use within the framework.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Arjen Poutsma
 * @since 1.1.3
 */
object CollectionUtils {
    /**
     * Return `true` if the supplied Collection is `null` or empty.
     * Otherwise, return `false`.
     * @param collection the Collection to check
     * @return whether the given Collection is empty
     */
    @JvmStatic
    fun isEmpty(collection: Collection<*>?): Boolean {
        return collection == null || collection.isEmpty()
    }

    /**
     * Return `true` if the supplied Map is `null` or empty.
     * Otherwise, return `false`.
     * @param map the Map to check
     * @return whether the given Map is empty
     */
    @JvmStatic
    fun isEmpty(map: Map<*, *>?): Boolean {
        return map == null || map.isEmpty()
    }
}
