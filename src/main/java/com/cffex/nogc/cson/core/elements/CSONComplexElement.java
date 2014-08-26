package com.cffex.nogc.cson.core.elements;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cffex.nogc.cson.core.ReadCursor;
import com.cffex.nogc.cson.core.entityInterface.access.IEntityRandomAccess;
import com.cffex.nogc.cson.core.entityInterface.access.IEntitySequenceAccess;
import com.cffex.nogc.cson.core.entityInterface.common.IElementType;
import com.cffex.nogc.cson.core.entityInterface.common.IElementValue;
import com.cffex.nogc.cson.core.entityInterface.common.IEntitySchema;
import com.cffex.nogc.cson.core.types.CSONComplexType;
import com.cffex.nogc.cson.core.types.CSONTypes;
import com.cffex.nogc.cson.core.types.CSONTypesArray;

/**
 * Created by sunke on 2014/7/29.
 */
public class CSONComplexElement implements IEntityRandomAccess, IEntitySequenceAccess, IElementValue {

    public CSONComplexElement(Object schemaOrType, ReadCursor rawData){
        thisSchema  = CSONElement.getSchema(schemaOrType);
        thisElementType  = null;
        cursor  = null;
        keyValueIterator = null;
        fullLoaded = false;
        // If this schema(this "standard" schema may only be the parent class's schema) has defined "GetChildSchema" function
        // Try to get the real child element Schema by let this function read data in cursor
        if ( thisSchema.getChildSchema != null && rawData != null ) {
            thisSchema = thisSchema.getChildSchema.apply(rawData);
            thisElementType = new CSONComplexType(thisSchema);
        }else{
            thisElementType = CSONElement.getComplexType(schemaOrType);
        }
        elementList = new ArrayList<IElementValue>(thisSchema.count());
        //初始化elementList的元素为null--->lazy load
        for(int i =0;i<thisSchema.count();i++){
            elementList.add(i,null);
        }
        if (rawData != null){
        	cursor = rawData;
        }
    }
    private boolean fullLoaded;
    private IEntitySchema thisSchema;
    private CSONComplexType thisElementType;
    private ReadCursor cursor;
    private Iterator keyValueIterator;

    // This must be initiated after get the "real" schema
    private ArrayList<IElementValue> elementList;



    private int getIndex(Object propertyIndex){
        if (propertyIndex instanceof  Integer){
            return (Integer)propertyIndex;
        }else if(propertyIndex instanceof String ){
            String s = (String)propertyIndex;
            if (Character.isDigit(s.charAt(0))){
                return Integer.parseInt(s);
            }else{
                return thisSchema.getID(s);
            }
        }else{
            throw new IllegalArgumentException();
        }
    }
    private IElementValue readElement(ReadCursor cur,int index)throws Exception{
        byte elementTypeCode = cur.getReader().readElementType(cur, index);
        CSONTypes types = CSONTypesArray.CSONElementTypes[elementTypeCode].getCsonTypes();
        switch(types) {
            case Array:
                return new CSONArrayElement(thisSchema.getElementType(index), cur.getReader().getChildElementCursor(cur,index));
            case EmbeddedDocument:
                return new CSONComplexElement(thisSchema.getElementType(index),cur.getReader().getChildElementCursor(cur,index));
            default: return CSONElement.getElement(cur, index);
        }
    }

    // Complex element use lazy load strategy for accessing item inside of CSON buffer
    // The apply function will read element from CSON buffer and store item in elementList array
    public IElementValue apply(Object index)throws Exception{
        int i = getIndex(index);
        if (i < thisSchema.count()) {
            if (elementList.size()<=i || elementList.get(i) == null){
                if (cursor != null) elementList.add(i,readElement(cursor,i));
                else elementList.add(i,CSONElement.NullValue);
            }
            return elementList.get(i);
        }else throw new IllegalArgumentException();
    }

    @Override public IElementType elementType() {
        return thisElementType;
    }
    @Override public void setValue(Object newValue)throws  Exception{
        if (newValue instanceof  CSONComplexElement) {
            CSONComplexElement srcComplexElement = (CSONComplexElement)newValue;
            for (int i=0;i<this.length();i++){
                setValue(i, srcComplexElement.apply(i));
            }
        }
        else throw new Exception("Can't copy from a none ComplexElement object.");
    }
    @Override
    public Object getValue(){
        return  this;
    }

    @Override
    public IEntitySchema getSchema(){
        return thisSchema;
    }
    @Override
    public Object getValue(Object index) throws Exception{
        return apply(index).getValue();
    }
    @Override public IElementValue getElement(Object index) throws  Exception{
        return apply(index);
    }
    @Override public byte[] getRawValue(Object index)throws  Exception{
        return getRawValue(index,true);
    }
    @Override public byte[] getRawValue(Object index,boolean needTypeCode)throws  Exception{
        ByteBuffer outBuffer = ByteBuffer.wrap(new byte[4096]).order(ByteOrder.LITTLE_ENDIAN);
        IElementValue childElement = getElement(index);
        int len = childElement.elementType().getRawValue(outBuffer, childElement.getValue(), needTypeCode).flip().limit();
        byte[] ret = new byte[len];
        outBuffer.get(ret);
        return ret;
    }
    @Override public void setValue(Object index, Object value) throws  Exception{
        int i = getIndex(index);
        if (value instanceof IElementValue){
            elementList.set(i,(IElementValue)value);
        }else {
            IElementValue item = elementList.get(i);
            if (item == null || item == CSONElement.NullValue){
                elementList.set(i,new CSONElement(thisSchema.getTypeCode(i), value));
            }else {
                item.setValue(value);
            }
        }
    }

    // As this complex structure will be lazy loaded. It must be performed a full load from buffer
    @Override
    public Iterator<IElementValue> iterator()throws Exception{
        if (!fullLoaded) {
            for (int i = 0;i<length();i++){
                apply(i);
            }
            fullLoaded = true;
        }
        return elementList.iterator();
    }
    public Map<String,IElementValue> mapIterator() throws  Exception{
        Map<String,IElementValue> map = new HashMap<String, IElementValue>();
        iterator();
        for(int i = 0;i < thisSchema.count();i++){
            map.put(thisSchema.getPropertyName(i),elementList.get(i));
        }
        return map;
    }
    @Override
    public boolean append(Object value){
        return false;
    }
    @Override
    public int length(){
        return elementList.size();
    }
    public  Object selectDynamic(String index)throws  Exception{
        return apply(index).getValue();
    }
    public void updateDynamic(String index ,Object args) throws  Exception{
        int i = getIndex(index);
        if (args instanceof  IElementValue){
            elementList.set(i,(IElementValue)args);
        }else {
            IElementValue item = elementList.get(i);
            if (item == null || item == CSONElement.NullValue){
                elementList.set(i,new CSONElement(thisSchema.getTypeCode(i), args));
            }else{ item.setValue(args);}
        }
    }
}
