/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.yaml

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import spock.lang.Specification

class YamlUtilsSpec extends Specification {

    void "test loadYaml"(){
        when:
        Path path =  Paths.get(System.getProperty("project.rootProjectDir"))
        assert(Files.exists(path))
        Path builYmlPath = path.resolve("build.yml")
        assert Files.exists(path)
        def yml = YamlUtils.loadYaml(builYmlPath) as Map

        then:
        yml["title"] != null
    }

}
