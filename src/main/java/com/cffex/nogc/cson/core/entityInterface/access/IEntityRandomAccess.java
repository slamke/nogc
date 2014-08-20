package com.cffex.nogc.cson.core.entityInterface.access;

import com.cffex.nogc.cson.core.entityInterface.common.IElementValue;
import com.cffex.nogc.cson.core.entityInterface.common.IEntitySchema;

/**
 * Created by sunke on 2014/7/23.
 */
public interface IEntityRandomAccess {
    public IEntitySchema getSchema();
    public Object getValue(Object index) throws Exception;
    public IElementValue getElement(Object index) throws Exception;
    public byte[] getRawValue(Object index) throws Exception;
    public byte[] getRawValue(Object index, boolean needTypeCode) throws Exception;
    public void setValue(Object index, Object value) throws Exception;
}
