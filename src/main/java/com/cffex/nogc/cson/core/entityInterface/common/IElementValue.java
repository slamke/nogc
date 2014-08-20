package com.cffex.nogc.cson.core.entityInterface.common;

/**
 * Created by sunke on 2014/7/23.
 */
public interface IElementValue {
    public IElementType elementType();
    public Object getValue();
    public void setValue(Object newValue) throws Exception;
}
