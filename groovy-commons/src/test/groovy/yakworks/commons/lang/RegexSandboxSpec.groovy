package yakworks.commons.lang


import spock.lang.Specification

/**
 * regex playground
 * truthy falsey checks
 * https://e.printstacktrace.blog/groovy-regular-expressions-the-definitive-guide/
 */
class RegexSandboxSpec extends Specification {

    void "regex playground"() {
        setup:
        //(?i) at the beginning of the regex makes it case insensitive
        def pattern = /(?i)off|false|no|0/

        expect:
        // ==~ tests if the entire string matches given pattern.
        // =~ means that the expression evaluates to true if any part of the string matches the pattern.
        (VAL ==~ pattern) == RESULT

        where:
        VAL     |  RESULT
        "false" |  true
        "False" |  true
        "FALSE" |  true
        "off" |  true
        "no" |  true
        "0" |  true
        "f" |  false
        "falsey" |  false
        "anything else" |  false
        "true" |  false
        null |  false
    }

}
