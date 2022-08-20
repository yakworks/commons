package yakworks.openapi

import io.swagger.v3.oas.models.media.Schema


class OapiMocks {

    static Schema getStringProp(){
        return new Schema()
            .type('string')
            .maxLength(50)
            .description("a desc")
    }

    static enum SomeTypes {
        string, number
    }
}
