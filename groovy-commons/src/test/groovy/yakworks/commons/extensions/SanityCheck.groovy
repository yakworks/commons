package yakworks.commons.extensions

import groovy.transform.CompileStatic

@CompileStatic
class SanityCheck {
    //just here so we can decompile and see how the extensions look
    boolean getBool(){
        [foo:false].getBoolean('foo')
    }
}
