/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api;

import java.util.Map;

/**
 * helper for an asMap method
 */
public interface AsMap {

    /**
     * converts to Map, helpfull for to json and can be overriden on concrete impls
     */
    Map<String, Object> asMap();

}
