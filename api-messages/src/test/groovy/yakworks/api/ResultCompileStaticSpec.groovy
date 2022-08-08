/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

import spock.lang.Specification
// import yakworks.commons.map.Maps

class ResultCompileStaticSpec extends Specification {

    void "run statics"(){
        expect:
        ResultCompileStatic.runAll()
    }

}
