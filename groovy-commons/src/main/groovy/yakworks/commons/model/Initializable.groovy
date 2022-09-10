/*
* Copyright 2019 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.model

/**
 * marker for a class that has init() method. spring has afterPropertiesSet, operates in same manner.
 */
interface Initializable {

    void init()

}
