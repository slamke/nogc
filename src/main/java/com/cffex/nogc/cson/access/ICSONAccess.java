package com.cffex.nogc.cson.access;

import java.util.function.Function;

/**
 * Created by sunke on 2014/8/4.
 * The interface to operate cson.
 */
public interface ICSONAccess {
    /**
     * save object to cson
     * @param id the id,  -1 stands for NOT SET and use lambda to compute it;or use this id.
     * @param obj the value
     * @param lambda the lambda expression to compute object id if id is not set.
     * @return the object id.
     */
    public abstract long saveObject(long id,Object obj,Function<Object,Long> lambda);

    /**
     * Get the binary value of the object by id.
     * @param id  id of the object.
     * @return the raw value.
     */
    public abstract byte[] getBinaryById(long id);

    /**
     * Get the object by the id.
     * @param id  id of the object.
     * @return the object instance
     */
    public abstract Object getObjectById(long id);

    /**
     * Get property value of the object by Id and the property index.
     * @param id id of the object.
     * @param propertyIndex index of the property.
     * @return The property value.
     */
    public abstract Object getPropertyByIndex(long id,int propertyIndex);

    /**
     * Get property value of the object by id and the property name.
     * @param id id of the object.
     * @param propertyName name of the property
     * @return The property value.
     */
    public abstract Object getPropertyByName(long id,String propertyName);

    /**
     * Get property raw value of the object by Id and the property name.
     * @param id id of the object.
     * @param propertyName name of the property.
     * @return The property raw value.
     */
    public abstract byte[] getPropertyRawValueByName(long id,String propertyName);

    /**
     * Get property raw value of the object by id and the property index.
     * @param id id of the object.
     * @param propertyIndex index of the property.
     * @return The property raw value.
     */
    public abstract byte[] getPropertyRawValueByIndex(long id,int propertyIndex);

    /**
     * update the whole object by id
     * @param id id of the object.
     * @param obj the new object instance
     * @return update flag.
     */
    public abstract boolean updateCSONObject(long id, Object obj);

    /**
     * Update the property of the object by id.
     * @param id id of the object.
     * @param propertyIndex index of the property.
     * @param value the new value of property.
     * @return update flag.
     */
    public abstract boolean updateCSONByProperty(long id,int propertyIndex,Object value);

    /**
     * Update the property of the object by id.
     * @param id id of the object.
     * @param propertyName name of the property
     * @param value the new value of property.
     * @return update flag.
     */
    public abstract boolean updateCSONByProperty(long id,String propertyName,Object value);

    /**
     * Delete the cson object by id.
     * @param id id of the object.
     * @return delete flag.
     */
    public abstract boolean deleteCSONObject(long id);
}
