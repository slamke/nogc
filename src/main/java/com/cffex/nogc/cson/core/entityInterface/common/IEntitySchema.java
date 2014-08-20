package com.cffex.nogc.cson.core.entityInterface.common;

import java.util.function.Function;

import com.cffex.nogc.cson.core.ReadCursor;
import com.cffex.nogc.cson.core.enumeration.EntityType;

/**
 * Created by sunke on 2014/7/23.
 * Meta Data of the pojo.
 */
public abstract class IEntitySchema {

    abstract  public String entityId();
    abstract  public int count();
    abstract  public EntityType schemaType();
    abstract  public boolean containsProperty(String property);
    abstract  public Class objType();
    abstract  public int getID(String propertyName);
    abstract  public String getPropertyName(int ID);
    abstract  public byte getTypeCode(Object property);
    /**
     * Return the element IElementType if this property is an array,
     * return CSONComplexType if this property is a Complex Element.
     */
    abstract  public IElementType getElementType(Object property);
    /**
     * Function reading the CSON structure inside values to decide which real schema it should be.
     * This schema is only the parent class schema. Schema of child class can be get by this function.
     * User must implement a child class schema selecting function and set it's pointer into this schema property
     */
    public Function<ReadCursor,IEntitySchema> getChildSchema;
}
