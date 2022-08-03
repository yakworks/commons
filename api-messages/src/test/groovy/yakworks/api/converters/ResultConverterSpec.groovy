/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.converters

import spock.lang.Specification
import yakworks.api.Result
import yakworks.json.groovy.JsonEngine

// import yakworks.commons.map.Maps

class ResultConverterSpec extends Specification {

    void "simple Ok"(){
        when:
        Result okRes = Result.OK().payload([foo:'bar'])
        String res = JsonEngine.toJson(okRes)

        then:
        res == '{"ok":true,"status":200,"payload":{"foo":"bar"}}'
    }

}
