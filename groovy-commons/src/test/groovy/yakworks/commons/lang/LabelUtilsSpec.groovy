package yakworks.commons.lang

import spock.lang.Specification

class LabelUtilsSpec extends Specification  { //implements GrailsWebUnitTest {

    void "getNaturalTitle"() {
        expect:
        LabelUtils.getNaturalTitle("Thing") == 'Thing'
        LabelUtils.getNaturalTitle("thing") == 'Thing'
        LabelUtils.getNaturalTitle("customer.name") == 'Customer'
        LabelUtils.getNaturalTitle("customer.num") == 'Customer Num'

        LabelUtils.getNaturalTitle("customer.org.name") == 'Customer Org'
        LabelUtils.getNaturalTitle("customer.org.id") == 'Org Id'
        LabelUtils.getNaturalTitle("customerOrgNum") == 'Customer Org Num'
        LabelUtils.getNaturalTitle("customerOrgName") == 'Customer Org Name'
        LabelUtils.getNaturalTitle("xx99yy1URLlocX90") == 'Xx99yy1 URL loc X90'
    }

    void "getObjectAndProp"() {
        expect:
        LabelUtils.getObjectAndProp("thing") == 'thing'
        LabelUtils.getObjectAndProp("customer.name") == 'customer.name'
        LabelUtils.getObjectAndProp("customer.org.name") == 'org.name'
    }
}
