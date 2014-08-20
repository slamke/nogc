package com.cffex.nogc.cson.core.types;

import java.nio.ByteBuffer;
import java.util.Iterator;

import com.cffex.nogc.cson.core.CSONWriter;
import com.cffex.nogc.cson.core.WriteCursor;
import com.cffex.nogc.cson.core.elements.CSONArrayElement;
import com.cffex.nogc.cson.core.elements.CSONElement;
import com.cffex.nogc.cson.core.entityInterface.access.IEntitySequenceAccess;
import com.cffex.nogc.cson.core.entityInterface.common.IElementType;
import com.cffex.nogc.cson.core.entityInterface.common.IElementValue;
import com.cffex.nogc.cson.core.entityInterface.common.IEntitySchema;

/**
 * Created by sunke on 2014/7/29.
 * 数组类型-->实现了IElementType接口
 * 作用：提供抽象的type定义，提供获取schema type接口 写数据接口
 */
public class CSONArrayType extends IElementType {
    public CSONArrayType(Object childSchemaOrType){
        elementSchema = CSONElement.getSchema(childSchemaOrType);
        elementType = CSONElement.getElementType(childSchemaOrType);
        typeCode = CSONTypes.Array.getValue();
        // This function pointer should not be used, as this operation was performed in CSONElementArray constructor
        getValue = null;
        writeRawFunc = (Object value, ByteBuffer outBB)-> {
            CSONWriter writer = new CSONWriter(outBB);
            writeElement.writeElement(value,
                    writer.beginWriteComplexBody(outBB.position(), ((IEntitySequenceAccess) value).length()), -1);
        };
        writeElement = (Object value, WriteCursor cur,int index) -> {
            byte tc = CSONTypes.Array.getValue();
            CSONWriter thisWriter = cur.getWriter();
            int itemCount = ((IEntitySequenceAccess)value).length();
            if (value==null) tc = CSONTypes.NullValue.getValue();
            WriteCursor  arrayCursor = null;
            if (index>=0) {
                thisWriter.writeComplexValueIndex(tc, cur, index);
                arrayCursor = thisWriter.beginWriteComplexBody(cur.GetDataValueOffset(), itemCount);
            }else{
                arrayCursor = cur;
            }
            // Begin write this element array in data area
            // Write a temp length value for this Element Array(Need to updated later)
            thisWriter.writeLength(arrayCursor, 0);
            thisWriter.writeCount(arrayCursor, itemCount);

            int n = 0;

            Iterator<IElementValue> iterator = ((CSONArrayElement)value).iterator();
           while (iterator.hasNext()){
               IElementValue item = iterator.next();
               item.elementType().writeElement(item.getValue(), arrayCursor, n);
               n+=1;
           }
           thisWriter.endWriteComplexBody(arrayCursor);
           // Update the parent cursor last available data area offset
           if (index>=0) cur.UpdateDataValueOffset(arrayCursor.GetDataValueOffset());
        };
    }

    /**
     * 元素的类型
     */
    private CSONSimpleType elementType;
    /**
     * 元素的schema
     */
    private IEntitySchema elementSchema;

    public CSONSimpleType getElementType() {
        return elementType;
    }

    public IEntitySchema getElementSchema() {
        return elementSchema;
    }
}
