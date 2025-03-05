/*
* Copyright 2020 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.meta

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

import yakworks.commons.map.Maps
import yakworks.commons.model.Hydratable
import yakworks.commons.model.TotalCount
import yakworks.util.ClassUtils

/**
 * A list wrapper that will wrap object in MetaMap on a get()
 *
 * @author Joshua Burnett (@basejump)
 * @since 6.1.12
 */
@SuppressWarnings(["ExplicitCallToEqualsMethod", "UnusedPrivateMethod"])
@CompileStatic
class MetaMapList extends AbstractList<MetaMap> implements TotalCount, Serializable, Hydratable {

    protected transient List resultList
    protected List<MetaMap> metaMapList = []
    protected int totalCount = Integer.MIN_VALUE;

    MetaEntity metaEntity

    MetaMapList(List resultList) {
        this.resultList = resultList
    }

    MetaMapList(List resultList, MetaEntity metaEntity) {
        this.resultList = resultList
        this.metaEntity = metaEntity
    }

    @Override
    @CompileDynamic //not a performance hit
    int getTotalCount() {
        if (totalCount == Integer.MIN_VALUE) {
            boolean hasGormPagedResultList = ClassUtils.isPresent('grails.gorm.PagedResultList', MetaMapList.classLoader)
            var resListToUse = getListToUse()
            if(hasGormPagedResultList && (
                resListToUse.class.name == 'grails.gorm.PagedResultList' ||
                resListToUse.class.name == 'org.grails.orm.hibernate.query.PagedResultList' ||
                resListToUse.class.name == 'grails.orm.PagedResultList'
            )) {
                totalCount = resListToUse.totalCount
            }
            else if(resListToUse instanceof TotalCount) {
                totalCount = resListToUse.totalCount
            }
            else {
                totalCount = resListToUse.size()
            }
        }
        return totalCount;
    }

    /**
     * wraps the item in a MetaMap before returning it
     */
    @Override
    MetaMap get(int i) {
        if(resultList) {
            def origObj = resultList.get(i)
            def eb = new MetaMap(origObj, metaEntity)
            return eb
        } else {
            return metaMapList.get(i)
        }
    }

    @Override
    int size() {
        return getListToUse().size()
    }

    @Override
    boolean equals(Object o) {
        return getListToUse().equals(o)
    }

    @Override
    int hashCode() {
        return getListToUse().hashCode()
    }

    @Override
    Object clone() {
        return Maps.clone(this as Collection<Map>)
    }

    List getListToUse(){
        return resultList ?: metaMapList
    }

    @Override
    MetaMapList hydrate() {
        if (metaMapList) return this //already done

        for(MetaMap val : this){
            metaMapList.add(val.hydrate())
        }
        resultList = null

        return this
    }


    /*
     * Method called on serialize.
     * We call hydrate on everything in the list to move from entity to the MetaMap
     */
    // private void writeObject(ObjectOutputStream oos) throws Exception {
    //     hydrate()
    //     // to perform default serialization of Account object.
    //     oos.defaultWriteObject();
    // }
    private void writeObject(ObjectOutputStream out) throws IOException {
        // find the total count if it hasn't been done yet so there when this is deserialized
        getTotalCount();
        hydrate()

        out.defaultWriteObject();
    }
}
