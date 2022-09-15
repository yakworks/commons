package yakworks.commons.util

import spock.lang.Specification

class StringUtilsSpec extends Specification {
    def "ToBoolean"() {
        expect:
        StringUtils.toBoolean('on')
        StringUtils.toBoolean('true')
        StringUtils.toBoolean('True')
        StringUtils.toBoolean('yes')
        StringUtils.toBoolean('1')
        !StringUtils.toBoolean('10')
        !StringUtils.toBoolean('y')
        !StringUtils.toBoolean('false')
    }

    def "hasText"() {
        expect:
        StringUtils.hasText('x')
        StringUtils.hasText(' - ')
        !StringUtils.hasText(' ')
        !StringUtils.hasText('  ')
        !StringUtils.hasText('')
        !StringUtils.hasText(null)
    }

    def "capitalize"() {
        expect:
        StringUtils.capitalize('x') == 'X'
    }

    def "parseStringAsGString"() {
        expect:
        StringUtils.parseStringAsGString('has $foo', [foo:'bar']) == 'has bar'
    }

    def "split"() {
        expect:
        StringUtils.split('a, b, c') == ['a', 'b', 'c']
    }

}
