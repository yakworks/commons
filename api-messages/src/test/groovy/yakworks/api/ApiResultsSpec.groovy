/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

import spock.lang.Specification
// import yakworks.commons.map.Maps

class ApiResultsSpec extends Specification {

    void "simple Ok"(){
        when:
        def res = ApiResults.OK()

        then:
        res.ok
        res.status.code == 207
        res.list instanceof Collections.SynchronizedList
    }

    void "test left shift"(){
        when:
        def res = ApiResults.OK()
        res << Result.OK().msg("foo.bar")
        res.msg("buzz")

        then:
        res.size() == 1
        res.code == "buzz"
        res.list[0].code == "foo.bar"
    }

}
