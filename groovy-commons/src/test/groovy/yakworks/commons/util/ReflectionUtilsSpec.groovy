package yakworks.commons.util

import spock.lang.Specification
import yakworks.util.ReflectionUtils

class ReflectionUtilsSpec extends Specification {

    void "get private field value"() {
        setup:
        TestDomain domain = new TestDomain(100)

        expect:
        domain.@pvtField == 100

        when:
        int value = ReflectionUtils.getPrivateFieldValue(TestDomain, "pvtField", domain)

        then:
        noExceptionThrown()
        value == 100
    }

    void "private field value - inner class"() {

        setup:
        TestDomain domain = new TestDomain(100) {
            //inner class
        }

        when: "when its inner class, private fields can not be accessed using .@ syntax and we must use getPrivateFieldValue"
        int value = domain.@pvtField

        then:
        MissingFieldException ex = thrown()
        ex.message.contains "No such field: pvtField"

        when:
        value = ReflectionUtils.getPrivateFieldValue(TestDomain, "pvtField", domain)

        then:
        noExceptionThrown()
        value == 100
    }

}


class TestDomain {
    private int pvtField

    TestDomain(int val) {
        this.pvtField = val
    }

    int test() {
        return pvtField
    }

}
