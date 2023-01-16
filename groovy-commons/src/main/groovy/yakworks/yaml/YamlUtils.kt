@file:JvmName("YamlUtils")
package yakworks.yaml

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.nio.file.Files
import java.nio.file.Path

fun loadYaml(path: Path?): Any {
    val yaml = Yaml()
    assert(Files.exists(path))
    return Files.newBufferedReader(path).use {
        yaml.load(it)
    }
}

/**
 * Use SnakeYaml to save to yaml path file.
 *
 * @param path the file to write the yaml to
 * @param yml  the Map or List to convert to yaml
 */
fun saveYaml(path: Path?, yml: Any?) {
    val dops = DumperOptions()
    dops.indent = 2
    dops.isPrettyFlow = true
    dops.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
    //dops.width = 120
    val yaml = Yaml(dops)
    Files.newBufferedWriter(path).use {
        yaml.dump(yml, it)
    }
}
