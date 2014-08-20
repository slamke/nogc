package com.cffex.nogc.cson.core.types;

import java.nio.ByteBuffer;
import java.util.Iterator;

import com.cffex.nogc.cson.core.CSONWriter;
import com.cffex.nogc.cson.core.WriteCursor;
import com.cffex.nogc.cson.core.elements.CSONComplexElement;
import com.cffex.nogc.cson.core.entityInterface.common.IElementType;
import com.cffex.nogc.cson.core.entityInterface.common.IElementValue;
import com.cffex.nogc.cson.core.entityInterface.common.IEntitySchema;
import com.cffex.nogc.cson.core.types.CSONTypesArray;

/**
 * Created by sunke on 2014/7/29.
 * 复杂类型-->实现了IElementType接口
 * 作用：提供抽象的type定义，提供获取schema接口 写数据接口
 */
public class CSONComplexType extends IElementType {
    private IEntitySchema schema;

    public CSONComplexType(IEntitySchema typeSchema){
        typeCode = CSONTypes.EmbeddedDocument.getValue();
        schema = typeSchema;
        getValue = null;
        writeRawFunc = (Object value, ByteBuffer outBB)-> {
            CSONWriter writer = new CSONWriter(outBB);
            writeElement.writeElement(value,writer.beginWriteComplexBody(writer.getBeginOffset(),schema.count()),-1);
        };

        writeElement = (Object value, WriteCursor cur,int index) -> {
            byte tc = CSONTypes.EmbeddedDocument.getValue();
            CSONWriter thisWriter = cur.getWriter();
            if (value == null){
                tc = CSONTypes.NullValue.getValue();
            }

            WriteCursor complexElementCursor = null;
            if (index>=0) {
                thisWriter.writeComplexValueIndex(tc, cur, index);
                complexElementCursor =  thisWriter.beginWriteComplexBody(cur.GetDataValueOffset(), schema.count());
            }else{
                complexElementCursor = cur;
            }
            // Begin write this complex element in data area
            // Write a temp length value for this Complex Element(Need to updated later)
            thisWriter.writeLength(complexElementCursor, 0);
            thisWriter.writeCount(complexElementCursor, schema.count());

            int n = 0;
            try {
                Iterator<IElementValue> iterator = ((CSONComplexElement)value).iterator();
                while (iterator.hasNext()){
                    IElementValue item = iterator.next();
                    if (item !=null){
                        item.elementType().writeElement(item.getValue(),complexElementCursor,n);
                    }else {
                        CSONTypesArray.NullType.writeElement(null, complexElementCursor, n);
                    }
                    n+=1;
                }
                thisWriter.endWriteComplexBody(complexElementCursor);
                // Update the parent cursor last available data area offset
                if (index>=0) cur.UpdateDataValueOffset(complexElementCursor.GetDataValueOffset());
            }catch (Exception e){
                e.printStackTrace();
            }
        };
    }
    public IEntitySchema getSchema() {
        return schema;
    }
}
