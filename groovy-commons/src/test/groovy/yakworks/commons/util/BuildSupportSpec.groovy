package yakworks.commons.util

import spock.lang.Specification

class BuildSupportSpec extends Specification {

    // SEE the grade.build for how these get set.
    void "test setup"() {
        setup:
        def rootPath = BuildSupport.gradleRootProjectPath
        def projectPath = BuildSupport.gradleProjectPath

        expect:
        projectPath.exists()
        rootPath.exists()
        projectPath.toString().endsWith("groovy-commons")
        rootPath.toString().endsWith("commons")
    }

}
