package yakworks.commons.util

import spock.lang.Specification

class BuildSupportSpec extends Specification {

    // SEE the grade.build for how these get set.
    void "test setup"() {
        setup:
        def rootPath = BuildSupport.rootProjectPath
        def projectPath = BuildSupport.projectPath

        expect:
        projectPath.exists()
        rootPath.exists()
        projectPath.toString().endsWith("groovy-commons")

    }

}
