package com.cffex.nogc.cson.core;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.UUID;

import com.cffex.nogc.cson.core.types.CSONTypes;
import com.cffex.nogc.cson.core.types.CSONTypesArray;

/**
 * Created by sunke on 2014/7/22.
 */
public class CSONWriter {

    private ByteBuffer dataBuffer;
    private int beginOffset;
    private int lastDataPos;

    public CSONWriter(ByteBuffer dBuf){
        this.dataBuffer =dBuf;
        this.beginOffset = dataBuffer.position();
        this.lastDataPos = 0;
    }

    public int getBeginOffset() {
        return beginOffset;
    }

    private void seek2Index(WriteCursor cur,int index){
        dataBuffer.position(cur.GetIndexTypeOffset(index));
    }
    /**
     * Seek to the current writable position in data Buffer(Current tail of DataBuffer area)
     */
    private void seek2DataBuf(WriteCursor cur){
        dataBuffer.position(cur.GetDataValueOffset());
    }

    public WriteCursor beginWriteComplexBody(int startPos,int count){
         return new WriteCursor(this,startPos,count);
    }
    /**
     * End the writing of specified complex element pointed by cursor
     */
    public void endWriteComplexBody(WriteCursor cur){
        seek2DataBuf(cur);
        dataBuffer.put(CSONTypes.BSONEOF.getValue());
        //get the total length by using last position of Data Area - Count field
        writeLength(cur, cur.GetDataValueOffset()-cur.getNumOffset());
    }
    public void writeLength(WriteCursor cur,int len){
        dataBuffer.position(cur.getLenOffset());
        dataBuffer.putInt(len);
    }
    public void writeCount(WriteCursor cur,int count){
        dataBuffer.position(cur.getNumOffset());
        dataBuffer.putInt(count);
    }
    public void writeElementType(byte typeCode,WriteCursor cur,int index){
        seek2Index(cur,index);
        dataBuffer.put(typeCode);
    }
    /**
     * Write the index area with element type code and relative offset for this element in data area
     */
    public void writeComplexValueIndex(byte typeCode,WriteCursor cur,int index){
        seek2Index(cur,index);
        dataBuffer.put(typeCode);
        if (typeCode==CSONTypes.NullValue.getValue()){
            dataBuffer.putInt(0);
        } else {
            //set the offset in the buffer(how to deal with the index)
            dataBuffer.putInt(cur.DataAreaLength());
        }
    }
    public void writeValue(byte typeCode,Object value, WriteCursor cur, int index) throws  Exception {
        byte tc = typeCode;
        CSONTypes elementType = CSONTypesArray.CSONElementTypes[typeCode].getCsonTypes();
        if (value == null) {
            tc = CSONTypes.NullValue.getValue();
            elementType = CSONTypes.NullValue;
        }
        writeElementType(tc,cur,index);
        // Write the 4 Byte size value or the offset in data area
        boolean _continue = false;
        switch (elementType){
            case Boolean:
                /**
                 * 单个布尔类型变量使用 int 值来表示，布尔数组采用 byte 数组来表示
                 */
                //dataBuffer.putInt(value[0]);
                if ((Boolean) value){
                    dataBuffer.putInt(1);
                }else{
                    dataBuffer.putInt(0);
                }
                break;
            case Int8 :
                //dataBuffer.putInt(value[0]);
                dataBuffer.putInt((Byte)value);
                break;
            case Int16:
                //dataBuffer.putInt(Bits.getShort(value,0));
                dataBuffer.putInt((Short)value);
                break;
            case Int32:
                //dataBuffer.putInt(if (value.isInstanceOf[Int])value.asInstanceOf[Int] else value.asInstanceOf[CSONTypes.CSONTypes].id)
                //dataBuffer.putInt(Bits.getInt(value,0));
                if (value instanceof  Integer){
                    dataBuffer.putInt((Integer)value);
                }else{ //枚举类型保存ordinal
                    dataBuffer.putInt( ((Enum<?>)value).ordinal());
                }
                break;
            case NullElement:
                dataBuffer.putInt(0);
                break;
            case NullValue:
                dataBuffer.putInt(0);
                break;
            default:
                dataBuffer.putInt(cur.DataAreaLength());
                _continue = true;
                break;
        }
        if (_continue){
            writeDataArea(elementType,value, cur);
        }
    }

    private void writeDataArea(CSONTypes elementType,Object value, WriteCursor cur)throws Exception{
        seek2DataBuf(cur);
        switch (elementType) {
            case Int64:
                //dataBuffer.putLong(Bits.getLong(value,0));
                dataBuffer.putLong((Long) value);
                break;
            case Timestamp:
                //dataBuffer.putLong(Bits.getLong(value,0));
                dataBuffer.putLong((Long) value);
                break;
            case UTCDatetime:
                //dataBuffer.putLong(Bits.getLong(value,0));
                dataBuffer.putLong(((Date)value).getTime());
                break;
            case FloatingPoint:
                //dataBuffer.putFloat(Bits.getFloat(value,0));
                dataBuffer.putDouble((Double) value);
                break;
            case Single:
                //dataBuffer.putFloat(Bits.getFloat(value,0));
                dataBuffer.putFloat((Float) value);
                break;
            case ObjectId:
                dataBuffer.putLong(((UUID) value).getMostSignificantBits());
                dataBuffer.putLong(((UUID) value).getLeastSignificantBits());
                break;
            case DBPointer:
                dataBuffer.putLong(((UUID) value).getMostSignificantBits());
                dataBuffer.putLong(((UUID) value).getLeastSignificantBits());
                break;
            case Decimal:
                BigDecimal v = (BigDecimal)value;
                int signPart = v.signum();
                v = v.abs();
                dataBuffer.putInt(signPart);
                dataBuffer.putLong(v.longValue());
                dataBuffer.putInt(v.movePointRight(4).remainder(new BigDecimal("10000")).intValue());
                break;
            case UTF8String:
                byte[] strByteBuf = ((String)value).getBytes("UTF-8");
                dataBuffer.putInt(strByteBuf.length);
                dataBuffer.put(strByteBuf);
                break;
            case JavaScriptCode:
                byte[] codeArray = ((String)value).getBytes("UTF-8");
                dataBuffer.putInt(codeArray.length);
                dataBuffer.put(codeArray);
                break;
            case BinaryData:
                byte[] buf = (byte[]) value;
                dataBuffer.putInt(buf.length);
                dataBuffer.put(buf);
                break;
            default:
                throw new Exception("Not implemented for this type");
        }
        cur.UpdateDataValueOffset(dataBuffer.position());
    }
}
