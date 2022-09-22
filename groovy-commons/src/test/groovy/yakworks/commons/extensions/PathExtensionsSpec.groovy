package yakworks.commons.extensions

import spock.lang.Specification
import yakworks.commons.util.BuildSupport

class PathExtensionsSpec extends Specification {

    void "test exists"() {
        expect:
        def path = BuildSupport.rootProjectPath
        path.exists()
        !path.resolve("nothing").exists()

    }

}
