package com.cffex.nogc.cson.core;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.UUID;

import com.cffex.nogc.cson.core.types.CSONTypes;
import com.cffex.nogc.cson.core.types.CSONTypesArray;

/**
 * Created by sunke on 2014/7/22.
 * CSON element and structure reader.
 * Input: ByteBuffer with position() pointing to the start of CSON data
 */
public class CSONReader {

    private ByteBuffer dataBuffer;
    private ReadCursor rootCursor;

    public ByteBuffer getDataBuffer() {
        return dataBuffer;
    }

    public ReadCursor getRootCursor() {
        return rootCursor;
    }

    public CSONReader(ByteBuffer buffer){
        this.dataBuffer =buffer;
        this.rootCursor = new ReadCursor(dataBuffer.position(),this);
    }

    private int getElementDataOffset(ReadCursor cursor,int index){
        dataBuffer.position(cursor.getIndexValueOffset(index));
        return dataBuffer.getInt();
    }

    private void seekElementDataPos(ReadCursor cursor,int index){
        dataBuffer.position(cursor.getDataValueOffset(getElementDataOffset(cursor,index)));
    }

    public int readLength(ReadCursor cursor){
        dataBuffer.position(cursor.getLenOffset());
        return dataBuffer.getInt();
    }

    public int readCount(ReadCursor cursor){
        dataBuffer.position(cursor.getNumOffset());
        return dataBuffer.getInt();
    }

    /**
     * Read the type code of element pointed by index
     */
    public byte readElementType(ReadCursor cursor,int index){
        dataBuffer.position(cursor.getIndexTypeOffset(index));
        return dataBuffer.get();
    }

    public int getComplexElementOffset(ReadCursor cursor,int index){
        return getElementDataOffset(cursor,index);
    }

    public ReadCursor getChildElementCursor(ReadCursor cursor,int index){
        CSONTypes ceType = CSONTypesArray.CSONElementTypes[(readElementType(cursor, index))].getCsonTypes();
        if ( ceType== CSONTypes.Array || ceType==CSONTypes.EmbeddedDocument )
            return new ReadCursor(getComplexElementOffset(cursor,index)+cursor.getDataOffset(),this);
        else return null;
    }

    public boolean readBoolean(ReadCursor cursor,int index){
        dataBuffer.position(cursor.getIndexValueOffset(index));
        return dataBuffer.getInt() == 1;
    }

    public int readInt(ReadCursor cursor,int index){
        return getElementDataOffset(cursor,index);
    }

    public String readStrBytes(ReadCursor cursor,int index)throws UnsupportedEncodingException{
        seekElementDataPos(cursor,index);
        int len = dataBuffer.getInt();
        byte[] buf = new byte[len];
        dataBuffer.get(buf);
        return  new String(buf,"UTF-8");
    }

    public byte[] readBuf(ReadCursor cursor,int index){
        seekElementDataPos(cursor,index);
        int len = dataBuffer.getInt();
        byte[] buf = new byte[len];
        dataBuffer.get(buf);
        return buf;
    }
    public float readFloat(ReadCursor cursor,int index){
        seekElementDataPos(cursor,index);
        return dataBuffer.getFloat();
    }

    public double readDouble(ReadCursor cursor,int index){
        seekElementDataPos(cursor,index);
        return dataBuffer.getDouble();
    }

    public long readLong(ReadCursor cursor,int index){
        seekElementDataPos(cursor,index);
        return dataBuffer.getLong();
    }


    private BigDecimal retrieveDecimal(){
        int signPart = dataBuffer.getInt();
        if (signPart == 0) return BigDecimal.ZERO;
        BigDecimal longPart = new BigDecimal(dataBuffer.getLong());
        BigDecimal mantissaPart = (new BigDecimal(dataBuffer.getInt())).movePointLeft(4);
        if (signPart<0) {
            return  longPart.add(mantissaPart).negate();
        }else return longPart.add(mantissaPart);
    }
    public BigDecimal readDecimal(ReadCursor cursor,int index){
        seekElementDataPos(cursor, index);
        return retrieveDecimal();
    }
    public Date readDatetime(ReadCursor cursor,int index){
        return new java.util.Date(readLong(cursor,index));
    }

    public UUID readGuid(ReadCursor cursor,int index){
        seekElementDataPos(cursor,index);
        long highBits = dataBuffer.getLong();
        long lowBits = dataBuffer.getLong();
        return new java.util.UUID(highBits,lowBits);
    }
    public byte[] readRawBuf(ReadCursor cursor,int index, int length, byte typeCode){
        byte[] retBuf = new byte[length+1];
        retBuf[0] = typeCode;
        seekElementDataPos(cursor,index);
        dataBuffer.get(retBuf, 1, length);
        return retBuf;
    }
    /**
     * Read variable size value in CSON raw value mode
     */
    public byte[] ReadRawLargeVaue(ReadCursor cursor,int index,  byte typeCode){
        seekElementDataPos(cursor,index);
        int length = dataBuffer.getInt();
        byte[] retBuf = new byte[length+1+4];
        retBuf[0] = typeCode;
        seekElementDataPos(cursor,index);
        dataBuffer.get(retBuf,1,length+4);
        return retBuf;
    }
}
