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
        Validate.notEmpty([1,2])
    }

    void "notEmpty string array"() {
        when:
        Validate.notEmpty("")
        then:
        thrown(IllegalArgumentException)

        when:
        Validate.notEmpty([])
        then:
        thrown(IllegalArgumentException)
    }

    void "notNull IllegalArg thrown"() {
        setup:
        def x = null
        when:
        Validate.notNull(x)

        then:
        // thrown(IllegalArgumentException)
        def e = thrown(IllegalArgumentException)
        e.message.contains('The validated object must not be null')

        when:
        Validate.notNull(x, "[foo]")

        then:
        e = thrown(IllegalArgumentException)
        e.message.contains('[foo] must not be null')

        when:
        Validate.notNull(x, "custom message")

        then:
        e = thrown(IllegalArgumentException)
        e.message.contains('custom message')
    }

    void "notEmpty IllegalArg thrown"() {
        setup:
        def x = null
        when:
        Validate.notEmpty(x)

        then:
        // thrown(IllegalArgumentException)
        def e = thrown(IllegalArgumentException)
        e.message.contains('Required value was null')
    }

}
