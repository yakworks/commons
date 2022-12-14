/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

/**
 * Base interface for statuses.
 */
interface ApiStatus {
    /**
     * Get the associated status code.
     *
     * @return the status code.
     */
    val code: Int

    /**
     * Get the reason phrase.
     *
     * @return the reason phrase.
     */
    val reason: String?
}
