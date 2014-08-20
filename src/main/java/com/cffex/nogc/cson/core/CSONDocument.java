package com.cffex.nogc.cson.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.function.Function;

import com.cffex.nogc.cson.core.elements.CSONArrayElement;
import com.cffex.nogc.cson.core.elements.CSONComplexElement;
import com.cffex.nogc.cson.core.entityInterface.access.IEntityRandomAccess;
import com.cffex.nogc.cson.core.entityInterface.access.IEntitySequenceAccess;
import com.cffex.nogc.cson.core.entityInterface.common.IElementValue;
import com.cffex.nogc.cson.core.entityInterface.common.IEntitySchema;
import com.cffex.nogc.cson.core.types.CSONTypesArray;
import com.cffex.nogc.cson.core.utils.Tuple3;

/**
 * Created by sunke on 2014/7/30.
 */
public class CSONDocument implements IEntityRandomAccess, IEntitySequenceAccess {
    public CSONDocument(){
        isDirty = false;
        reader = null;
        schema = null;
        docBody  = new CSONArrayElement(CSONTypesArray.NullType,null);
    }

    private boolean isDirty;
    private CSONReader reader;
    private IEntitySchema schema;
    private IEntityRandomAccess docBody;

    public CSONDocument(IEntitySchema docSchema,ByteBuffer buffer) {
        this();
        schema = docSchema;
        if (buffer != null) {
            ByteBuffer dataBB = buffer.order(ByteOrder.LITTLE_ENDIAN);
            reader = new CSONReader(dataBB);
            docBody = new CSONComplexElement(schema,reader.getRootCursor());
        }else {
            isDirty=true;
            docBody = new CSONComplexElement(schema,null);
        }
    }
    @Override public IEntitySchema getSchema(){return  schema;}
    @Override public Object getValue(Object index){
        try {
            return docBody.getValue(index);
        }catch (Exception e){ e.printStackTrace();}
        return null;
    }
    @Override public IElementValue getElement(Object index){ try {
            return docBody.getElement(index);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
    @Override public byte[] getRawValue(Object index){
        return getRawValue(index,true);
    }
    @Override public byte[] getRawValue(Object index,boolean needTypeCode){
        try {
            docBody.getRawValue(index, needTypeCode);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    @Override public void setValue(Object index, Object value){
        try {
            docBody.setValue(index,value);
            isDirty=true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override public Iterator<IElementValue> iterator(){
        try {
            return  ((IEntitySequenceAccess)docBody).iterator();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    @Override public boolean append(Object value){ isDirty=true; return ((IEntitySequenceAccess)docBody).append(value); }
    @Override public int length(){ return ((IEntitySequenceAccess)docBody).length(); }

    /**
     * serialize the docBody to the byteBuffer
     * @param dataBuffer
     * @return
     */
    private ByteBuffer serialize(ByteBuffer dataBuffer){
        CSONWriter writer = new CSONWriter(dataBuffer);
        WriteCursor cur = writer.beginWriteComplexBody(writer.getBeginOffset(),length());
        ((IElementValue)docBody).elementType().writeElement(docBody,cur,-1);
        // Move the current position to the latest data area byte+1 position
        dataBuffer.position(cur.GetDataValueOffset());
        return dataBuffer;
    }
    /**
     * Get the document's body as one IElementValue, thus we can insert it into another CSON structure or read it into an object
     * Read ONLY. Don't try to set value or remove content inside this ElementValue. Because all your modification can't serialized.
     */
    public IElementValue toCSONElement(){return (IElementValue)docBody;}

    public  ByteBuffer toByteBuffer(ByteBuffer dataBuffer){
        // Document was updated or appended with new element, then serialize
        if (reader != null) {
            if (isDirty) {
                isDirty=false;
                return retryToByteBuffer(dataBuffer,(dataBuf)->{return this.serialize(dataBuf);});
            }else {
                // As the source ByteBuffer may be shared by multiple csonDocs in one IO buffer
                int csonBufferLength = reader.readLength(reader.getRootCursor()) + 4;
                byte[] arrayBuf = new byte[csonBufferLength];
                reader.getDataBuffer().position(reader.getRootCursor().getLenOffset());
                reader.getDataBuffer().get(arrayBuf);
                // defensive way is to copy the contents out to the new ByteBuffer
                if ((dataBuffer.capacity() - dataBuffer.position()) >= arrayBuf.length) return dataBuffer.put(arrayBuf);
                else{
                    return retryToByteBuffer(dataBuffer,(dataBuf)->{return dataBuf.put(arrayBuf);});
                }
            }
        }else {
           return  retryToByteBuffer(dataBuffer,(dataBuf)->{ return this.serialize(dataBuf);});
        }
    }

    /**
     *
     * @param dataBuf
     * @param op  for example: (dataBuf)->{ return this.serialize(dataBuf);}
     * @return
     */
    private ByteBuffer retryToByteBuffer(ByteBuffer dataBuf ,Function<ByteBuffer,ByteBuffer> op){
        int lastPos = dataBuf.position();
        ByteBuffer ret = dataBuf;
        boolean retrySign = true;
        int retryTimes = 0; // loop 2 times
        while (retrySign && 2 >= retryTimes) {
            try {
                op.apply(ret);
                retrySign = false;
            }
            catch (Exception e){
                    retryTimes += 1;
                    if (2 < retryTimes) throw e;
                    else {
                        System.out.println("Error: serialize buffer size exceeded:" + ret.capacity());
                        //扩大10倍，进行扩容
                        ret = ByteBuffer.allocate(ret.capacity() * 10);
                        // Enlarge the double size buffer and copy the contents
                        if (lastPos>0) {
                            dataBuf.position(lastPos).flip();
                            ret.put(dataBuf);
                        }
                    }
            }
        }
        return ret;
    }

//    public static class  TempTuple{
//        public ByteBuffer buffer;
//        public int offset;
//        public int length;
//
//        public TempTuple(ByteBuffer buffer, int offset, int length) {
//            this.buffer = buffer;
//            this.offset = offset;
//            this.length = length;
//        }
//    }
    public Tuple3<ByteBuffer,Integer,Integer> getByteArrayBuffer(){
        if (isDirty || reader==null) {
            int length = 4096;
            ByteBuffer retByteBuf = toByteBuffer(ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN));
            length = retByteBuf.position();
            return new Tuple3<ByteBuffer,Integer,Integer>(retByteBuf,0,length);
        } else  return new Tuple3<ByteBuffer,Integer,Integer>(reader.getDataBuffer(),reader.getRootCursor().getLenOffset(),reader.readLength(reader.getRootCursor())+4);
    }

    // As the csonDoc may be in dirty status(need to redo serialization) or with an long IO buffer read by multiple item
    // and may be read by not only one thread(this method is not thread safe), we copy the bytes to new array every time
    public byte[] getBytes(){
        // If there is original buffer and no dirty for this cson, just use null as buffer input as it return the original buf
        Tuple3<ByteBuffer,Integer,Integer> tempTuple = getByteArrayBuffer();
        byte[] retByteArray = new byte[tempTuple.third];
        tempTuple.first.position(tempTuple.second);
        tempTuple.first.get(retByteArray);
        return  retByteArray;
    }
    /**
     *  if we got a ByteString with sequence of CSON objects, we must call this method after construct each cson docuement
     *  from this byte buffer (Thus the byteBuffer cursor moved the correct posion of next item. Do NOT FORGET!!!!)
     *  Called when finished reading CSONBuffer ByteBuffer, move the position of this ByteBuffer to CSONBuffer.end+1
     */
    public ByteBuffer completeRead(){
        return (ByteBuffer)reader.getDataBuffer().position(reader.getRootCursor().getNumOffset()+reader.readLength(reader.getRootCursor()));
    }
}
