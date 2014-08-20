package com.cffex.nogc.cson.core.entityInterface.common;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.cffex.nogc.cson.core.ReadCursor;
import com.cffex.nogc.cson.core.WriteCursor;
import com.cffex.nogc.cson.core.entityInterface.lambda.WriteElementForEleType;
import com.cffex.nogc.cson.core.types.CSONTypes;

/**
 * Created by sunke on 2014/7/23.
 * cson element abstract class
 * 类型操作的抽象类
 */
public abstract  class IElementType {
    protected  byte typeCode = 0;
    /**
     * 获取值的lambda表达式
     */
    public BiFunction<ReadCursor ,Integer,Object> getValue;
    protected CSONTypes csonTypes;

    public ByteBuffer getRawValue(ByteBuffer dataBuffer,Object value,boolean needTypeCode){
        if (needTypeCode) dataBuffer.put(typeCode);
        writeRawFunc.accept(value, dataBuffer);
        return dataBuffer;
    }

    /**
     * 写元素的lambda表达式
     */
    protected WriteElementForEleType writeElement;
    protected BiConsumer<Object,ByteBuffer> writeRawFunc;

    public byte getTypeCode() {
        return typeCode;
    }

    public CSONTypes getCsonTypes() {
        return csonTypes;
    }

    public void writeElement(Object value,WriteCursor cursor,int index) {
        writeElement.writeElement(value,cursor,index);
    }

    public void writeRawFunc(Object t,ByteBuffer byteBuffer) {
        writeRawFunc.accept(t, byteBuffer);
    }

    public Object getValue(ReadCursor cursor ,Integer integer) {
        return getValue.apply(cursor,integer);
    }

    //public void setGetValue(BiFunction<ReadCursor ,Integer,Object> getValue) {
    //   this.getValue = getValue;
    //}
}
