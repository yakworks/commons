/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.openapi

import java.time.LocalDate

import io.swagger.v3.oas.models.media.Schema
import spock.lang.Specification
import yakworks.commons.lang.NameUtils

class OapiUtilsSpec extends Specification {

    void "test schemaProp to map"(){
        when:
        Schema schemaProp = OapiMocks.getStringProp()
        Map schemaMap = OapiUtils.schemaPropToMap(schemaProp)

        then:
        schemaMap == [type: 'string', maxLength: 50, description: 'a desc']
    }

    void "test getJsonType"(){

        expect:
        def tmap = OapiUtils.getJsonType(clazz)
        tmap.type == type
        tmap.format == format

        where:
        clazz      | type      | format
        String     | "string"  | null
        Boolean    | "boolean" | null
        Integer    | "integer" | null
        Long       | "integer" | 'int64'
        BigDecimal | "number"  | 'money'
        LocalDate  | "string"  | 'date'

        // when:
        // Map tmap = OapiUtils.getJsonType(String)
        //
        // then:
        // tmap.type == 'string'
    }

    void "test getJsonType enum"(){
        when:
        Map tmap = OapiUtils.getJsonType(OapiMocks.SomeTypes)

        then:
        tmap.type == 'string'
        tmap.enum == ['string', 'number'] as String []
    }

}
