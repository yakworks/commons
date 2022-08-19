/*
* Copyright 2020 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.yaml

import java.nio.file.Files
import java.nio.file.Path

import groovy.transform.CompileStatic

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

/**
 * Helper to dump yml to file
 */
@CompileStatic
class YamlUtils {

    /**
     * Use SnakeYaml to save to yaml path file.
     * @param path the file to write the yaml to
     * @param yml the Map or List to convert to yaml
     */
    static void saveYaml(Path path, Object yml){
        DumperOptions dops = new DumperOptions()
        dops.indent = 2
        dops.prettyFlow = true
        dops.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        //dops.width = 120
        Yaml yaml = new Yaml(dops)
        path.withWriter { writer ->
            yaml.dump(yml, writer)
        }
    }

    /**
     * Use snake to load the yaml file
     * @param path
     * @return the List or Map depending on the the yaml parsed.
     */
    static Object loadYaml(Path path){
        Yaml yaml = new Yaml()
        assert path.exists()
        path.withInputStream { istream ->
            return yaml.load(Files.newInputStream(path))
        }
        // yaml.load(Files.newInputStream(path))
    }

}
