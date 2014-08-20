package com.cffex.nogc.cson.core.entityInterface.lambda;

import com.cffex.nogc.cson.core.WriteCursor;

/**
 * Created by sunke on 2014/7/29.
 * 用于lambda表达式的接口，for IElementType writeElement
 */
@FunctionalInterface
public interface WriteElementForEleType {
    public void  writeElement(Object value,WriteCursor cursor,int index);
}
