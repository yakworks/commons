/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.problem.data

import groovy.transform.CompileStatic

/**
 * generic problem
 */
@CompileStatic
class DataProblemException extends yakworks.api.problem.ProblemException<DataProblemTrait<?>> {

    DataProblemException() {super()}
    DataProblemException(Throwable cause) {super(cause)}

    Object getEntity(){ problem.getEntity() }
}