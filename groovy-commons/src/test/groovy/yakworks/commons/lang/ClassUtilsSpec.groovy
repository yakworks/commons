/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.commons.lang

import spock.lang.Specification

class ClassUtilsSpec extends Specification{

    void "isProxy"() {
        expect:
        ClassUtils.isProxy('yakworks.model.ArTranType$HibernateProxy$HHHn0CoJ')
        ClassUtils.isProxy('yakworks.ArTranType_$$_123')
        !ClassUtils.isProxy('yakworks.ArTranType')
    }

    void "unwrapIfProxy"() {
        expect:
        ClassUtils.unwrapIfProxy('yakworks.model.ArTranType$HibernateProxy$HHHn0CoJ') == 'yakworks.model.ArTranType'
        ClassUtils.unwrapIfProxy('yakworks.model.ArTranType_$$_123') == 'yakworks.model.ArTranType'
        ClassUtils.unwrapIfProxy('yakworks.model.ArTranType') == 'yakworks.model.ArTranType'
    }

}
