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

    void "test mkdirs and delete"() {
        when:
        def path = BuildSupport.rootProjectPath.resolve("build")
        assert path.exists()
        def newPath = path.resolve("testing/paths/here").mkdirs()
        //create 2 files
        newPath.resolve("foo.txt").setText("foo")
        newPath.resolve("bar.txt").setText("bar")

        then:
        newPath.resolve("foo.txt").exists()
        newPath.exists()
        1 == newPath.resolve("foo.txt").delete()
        //3 dirs + 1 file
        4 == path.resolve("testing").delete()

    }

}
