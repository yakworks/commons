/*
* Copyright 2022 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.meta

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

/**
 * Represents a property on a bean for a MetaMap.
 * Similiar to a MetaBeanProperty. Adds a property for a openapi schema reference.
 * @see groovy.lang.MetaBeanProperty
 */
@EqualsAndHashCode(includes=["name", "classType"], useCanEqual=false) //because its used as cache key
@CompileStatic
class MetaProp implements Serializable {
    private static final long serialVersionUID = 1L
    //prop name
    String name
    //java class for prop
    Class classType
    //the base or entity class name for this includes.
    String className
    //--- Optional schema props that can be filled in for display and reporting. this is a subset of whats in openapi schema. ---
    // String title //display title
    // number, integer, boolean, array, object, string (this includes dates and files)
    // String type //basic type.

    //OpenAPI schema is added if using the schema plugin and the proper oapi.yml is on path.
    Object schema

    MetaProp() {}

    MetaProp(Class type) {
        this.classType = type
        this.className = type?.name
    }

    MetaProp(String name, Class type) {
        this.name = name
        this.classType = type
        this.className = type?.name
    }

    MetaProp(MetaBeanProperty metaBeanProperty) {
        //constructs using the return type
        this(metaBeanProperty.name, metaBeanProperty.getter.returnType)
    }

    static MetaProp of(String name, Class type){ new MetaProp(name, type)}

    String getClassName(){
        if(!this.className && this.classType) this.className = classType.name
        return this.className
    }

    @Override
    String toString(){
        return "[name: $name, classType: $classType]"
    }
}
