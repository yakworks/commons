/*
* Copyright 2023 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.extensions

import java.time.ZonedDateTime

import groovy.transform.CompileStatic

@CompileStatic
class ZonedTimeStaticExt {

    /**
     * Uses the app default zone to get the current date.
     * The system zone should be set to UTC. The App can have a default time zone.
     *
     * So for example when I want today and its 9:00pm Eastern, its tomorrow at 1am in UTC so using the default
     * ZonedDateTime.now() give a date for tomorrow. This will give now for today in the Eastern time zone.
     *
     * @return the LocalDate in the default time zone.
     */
    static ZonedDateTime nowAppZone(final ZonedDateTime type) {
        ZonedDateTime.now(AppTimeZone.zoneId)
    }

}
