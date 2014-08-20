package com.cffex.nogc.cson.core.entityInterface.access;

import java.util.Iterator;

import com.cffex.nogc.cson.core.entityInterface.common.IElementValue;

/**
 * Created by sunke on 2014/7/23.
 */
public interface IEntitySequenceAccess {
    public Iterator<IElementValue> iterator() throws Exception;
    public boolean append(Object value);
    public int length();
}
