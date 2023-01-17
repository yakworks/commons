package yakworks.commons.lang

import spock.lang.Specification

class ValidateSpec extends Specification {

    void "isTrue"() {
        when:
        String v1=""
        String v2=" "
        then:
        Validate.isTrue(true)

    }

    void "notEmpty"() {
        expect:
        Validate.notEmpty("x")
        !Validate.notEmpty("")
        Validate.notEmpty([1,2])
        !Validate.notEmpty([])
    }

}
