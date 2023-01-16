package yakworks.yaml

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

//junit5 example
internal class YamlUtilsTest {

    val path = Paths.get(System.getProperty("project.rootProjectDir"))

    @Test
    fun test_loadYaml() {
        //val path = BuildSupport.getRootProjectPath()
        assert(Files.exists(path))
        val builYmlPath = path.resolve("build.yml")
        assert(Files.exists(path))
        val yml = loadYaml(builYmlPath) as Map<String,*>
        assert(yml["title"] != null)
        //assert(Files.exists(builYmlPath))
    }

    @Test
    fun test_saveYaml() {
        //val path = BuildSupport.getRootProjectPath()
        assert(Files.exists(path))
        val builYmlPath = path.resolve("build/testing.yml")

        saveYaml(builYmlPath, mapOf("foo" to "bar", "buzz" to "boo"))
        //assert(yml["title"] != null)
        assert(Files.exists(builYmlPath))
    }
}
