@file:JvmName("MetaUtilsWork")
package yakworks.meta

/**
 * Use SnakeYaml to save to yaml path file.
 *
 * @param path the file to write the yaml to
 * @param yml  the Map or List to convert to yaml
 */
fun getProps(instance: Any): Map<String, Any?> {
    var props = mutableMapOf<String, Any?>()
    var klazz = instance::class
    //klazz.pro
    //for (MetaProperty mp : getMetaProperties(instance.class)) {
    //    props[mp.name] = mp.getProperty(instance)
    //}
    return props
}
