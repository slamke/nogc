package com.cffex.nogc.cson.core.elements;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.LinkedList;

import com.cffex.nogc.cson.core.ReadCursor;
import com.cffex.nogc.cson.core.entityInterface.access.IEntityRandomAccess;
import com.cffex.nogc.cson.core.entityInterface.access.IEntitySequenceAccess;
import com.cffex.nogc.cson.core.entityInterface.common.IElementType;
import com.cffex.nogc.cson.core.entityInterface.common.IElementValue;
import com.cffex.nogc.cson.core.entityInterface.common.IEntitySchema;
import com.cffex.nogc.cson.core.types.CSONArrayType;
import com.cffex.nogc.cson.core.types.CSONTypes;
import com.cffex.nogc.cson.core.types.CSONTypesArray;

/**
 * Created by sunke on 2014/7/29.
 */

/**
 * CSON array. The input can be schema or CSONSimpleType of array element(If this array just contain element like int, Array[Byte]
 */
public class CSONArrayElement implements IEntityRandomAccess, IEntitySequenceAccess, IElementValue{
    /**
     * 构造函数
     * @param elementSchemaOrType
     * @param rawData
     */
    public CSONArrayElement(Object elementSchemaOrType, ReadCursor rawData) {
        elementTypeCode = CSONElement.getElementTypeCode(elementSchemaOrType);
        thisElementType = CSONElement.getArrayType(elementSchemaOrType);
        arrayItems = new LinkedList<IElementValue>();
        count = 0;
        if (rawData != null){
            int ArrayCount = rawData.getReader().readCount(rawData);
            for(int i =0;i<ArrayCount;i++){
                try {
                    append(readElement(rawData, i));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 元素的typecode
     */
    private byte elementTypeCode;
    /**
     * 元素的类型
     */
    private CSONArrayType thisElementType;
    /**
     * 存储数组对象值的链表
     */
    private LinkedList<IElementValue> arrayItems;
    /**
     * 数组的length
     */
    private int count;


    /**
     * 读取第index个元素
     * @param cur
     * @param index
     * @return 该元素对应的IElementValue
     * @throws Exception
     */
    private IElementValue readElement(ReadCursor cur,int index)throws Exception{
        byte elementTypeCode = cur.getReader().readElementType(cur, index);
        CSONTypes types = CSONTypesArray.CSONElementTypes[elementTypeCode].getCsonTypes();
        switch (types){
            case Array: //不予许嵌套数组
                throw new Exception("CSON array inside another array was read. It's illegal format.");
            case EmbeddedDocument:
                return new CSONComplexElement(thisElementType.getElementSchema(),cur.getReader().getChildElementCursor(cur, index));
            default:
                return CSONElement.getElement(cur, index);
        }
    }

    /**
     * 获取第index个元素
     * @param index
     * @return 该元素对应的IElementValue
     */
    public IElementValue apply(Object index){
        return arrayItems.get(CSONElement.getIndex(index));
    }

    @Override
    public Object getValue(){
        return this;
    }

    /**
     * 不予许替换整体的值
     * @param newValue
     * @throws Exception
     */
    @Override
    public void setValue(Object newValue) throws Exception {
        throw new Exception("Can't perform replacing whole value on Element Array. Replace the element in parent structure.");
    }

    /**
     * 返回数组的iterator迭代器
     * @return
     */
    @Override
    public Iterator<IElementValue> iterator(){
        return arrayItems.iterator();
    }

    /**
     * 向数组后，追加元素
     * @param value IElementValue或者Object
     * @return
     */
    @Override
    public boolean append(Object value){
        // Can't support concurrenctly append. Otherwise Counter will be wrong
        count+=1;
        if(value instanceof  IElementValue){
            arrayItems.add((IElementValue) value);
        }else{
            if (value == null){
                arrayItems.add(CSONElement.NullValue);
            }else if (thisElementType.getElementSchema() == null){
                CSONElement element = new CSONElement(elementTypeCode);
                element.setValue(value);
                arrayItems.add(element);
            }else{
                // Can't append a complex object to an array. Instead you should serialize this object into ComplexElement then append
                throw new IllegalArgumentException();
            }
        }
        return true;
    }

    @Override
    public int length(){
        return count;
    }

    @Override
    public Object getValue(Object index){
        return apply(index).getValue();
    }
    @Override
    public IEntitySchema getSchema(){
        return thisElementType.getElementSchema();
    }
    @Override
    public IElementValue getElement(Object index){
        return apply(index);
    }

    /**
     * 获取数组元素的二进制rawValue
     * @param index
     * @return
     */
    @Override
    public byte[] getRawValue(Object index){
        return getRawValue(index,true);
    }

    @Override
    public byte[] getRawValue(Object index, boolean needTypeCode){
        ByteBuffer outBuffer = ByteBuffer.wrap(new byte[4096]).order(ByteOrder.LITTLE_ENDIAN);
        IElementValue itemElement = apply(index);
        int len = itemElement.elementType().getRawValue(outBuffer, itemElement.getValue(), needTypeCode).flip().limit();
        byte[] ret = new byte[len];
        outBuffer.get(ret);
        return  ret;
    }
    @Override public IElementType elementType(){
        return  thisElementType;
    }
    public Object getElementSchemaOrType() {
        if (thisElementType.getElementSchema() != null) {
            return thisElementType.getElementSchema();
        } else {
            return thisElementType.getElementType();
        }
    }

    /**
     * 修改数组的第index个值
     * @param index
     * @param value
     * @throws Exception
     */
    @Override
    public void setValue(Object index, Object value)throws  Exception{
        int i = CSONElement.getIndex(index);
        if (i < count) {
            Object value2Set = null;
            if (value == null) {
                value2Set = CSONElement.NullValue;
            } else{
                value2Set = value;
            }
            if (value2Set instanceof  IElementValue){
                arrayItems.set(i, (IElementValue)value2Set);
            }else {
                if (apply(i) == CSONElement.NullValue){arrayItems.set(i, new CSONElement(elementTypeCode, value2Set));}
                else{
                        arrayItems.get(i).setValue(value2Set);
                }
            }
        }
    }
}
