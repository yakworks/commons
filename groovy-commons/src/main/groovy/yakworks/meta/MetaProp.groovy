/*
* Copyright 2022 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.meta

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

import yakworks.commons.lang.LabelUtils

/**
 * Represents a property on a bean for a MetaMap.
 * Similiar to a MetaBeanProperty. Adds a property for a openapi schema reference.
 * @see groovy.lang.MetaBeanProperty
 */
@EqualsAndHashCode(includes=["name", "classType"], useCanEqual=false) //because its used as cache key
@CompileStatic
class MetaProp implements Serializable {
    private static final long serialVersionUID = 1L
    /** property name */
    String name

    /** title label, will use LabelUtils.getNaturalTitle(name) if not populated.  */
    String title

    /** java type for prop, either this or className should be populated */
    Class classType

    /** java type for prop, the getter will return String classType.name */
    String className

    //--- Optional schema props for display or reporting that can be filled in. this is a subset of whats in openapi schema. ---
    // see https://swagger.io/specification/#schema-object
    // number, integer, boolean, array, object, string (this includes dates and files)
    // String type //basic type.

    /**
     * OpenAPI schema is added if using the schema plugin and the proper oapi.yml is on path.
     * will be an instance of io.swagger.v3.oas.models.media.Schema or one of its types (such as StringSchema, etc)
     */
    //FIXME this is currently not used really.
    transient Object schema

    /** set to true if should be hidden and not shown on reports or exports */
    Boolean hidden

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

    /** gets title , if no title then uses LabelUtils.getNaturalTitle to create one from name */
    String getTitle(){
        if(!this.title) this.title = LabelUtils.getNaturalTitle(name)
        return this.title
    }

    /** check if has a title set */
    boolean hasTitle(){
        return this.title
    }

    @Override
    String toString(){
        return "[name: $name, classType: $classType]"
    }
}
