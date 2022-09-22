/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.util

import java.nio.file.Path
import java.nio.file.Paths

import groovy.transform.CompileStatic

@CompileStatic
class BuildSupport {

    /**
     * setup gradle to assign project.projectDir for the directory of the build to system properties
     * example:
     * subprojects {
     *   plugins.withId('groovy') {
     *     compileGroovy {
     *       groovyOptions.fork = true
     *       groovyOptions.forkOptions.jvmArgs = ['-Dproject.projectDir=' + project.projectDir.absolutePath]
     *     }
     *   }
     * }
     *
     */
    static String getProjectDir(){
        return System.getProperty("project.projectDir")
    }

    static Path getProjectPath(){
        return Paths.get(getProjectDir())
    }

    /**
     * on multiproject builds this returns the project.rootProjectDir that is setup in System properties
     * example in gradle:
     * tasks.withType(Test) { //and with spring would have it under bootRun {  as well
     *   systemProperty "project.rootProjectDir", rootProject.projectDir.absolutePath
     *   systemProperty "project.projectDir", project.projectDir.absolutePath
     * }
     *
     */
    static String getRootProjectDir(){
        return System.getProperty("project.rootProjectDir")
    }

    static Path getRootProjectPath(){
        return Paths.get(getRootProjectDir())
    }
}
