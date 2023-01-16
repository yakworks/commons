package yakworks.handlebars

import java.time.LocalDate

import spock.lang.Specification

class BarsSpec extends Specification {

    void "test parseTemplate"() {
        when:
        Map tran = [
            tran: [
                id:1,
                refnum: "123",
                tranDate: LocalDate.parse('2022-01-01'),
                customer: [num: 'abc']
            ]
        ]
        String exp = '{{#tran}}82/{{refnum}}/{{customer.num}}/{{dateFormat tranDate "yyyy-M-dd"}}/Scan{{/tran}}'
        String result = Bars.applyInline(exp, tran)

        then:
        result == "82/123/abc/2022-1-01/Scan"

    }

}
