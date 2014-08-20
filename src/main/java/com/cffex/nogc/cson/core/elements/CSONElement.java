package com.cffex.nogc.cson.core.elements;

import java.util.Date;

import com.cffex.nogc.cson.core.ReadCursor;
import com.cffex.nogc.cson.core.entityInterface.common.IElementType;
import com.cffex.nogc.cson.core.entityInterface.common.IElementValue;
import com.cffex.nogc.cson.core.entityInterface.common.IEntitySchema;
import com.cffex.nogc.cson.core.types.CSONArrayType;
import com.cffex.nogc.cson.core.types.CSONComplexType;
import com.cffex.nogc.cson.core.types.CSONSimpleType;
import com.cffex.nogc.cson.core.types.CSONTypes;
import com.cffex.nogc.cson.core.types.CSONTypesArray;

/**
 * Created by sunke on 2014/7/29.
 */
public class CSONElement implements IElementValue {

    public static CSONElement NullElement = new CSONElement(CSONTypes.NullElement.getValue());
    public static CSONElement NullValue = new CSONElement(CSONTypes.NullValue.getValue());
    /**
     * Static function to new (read value from dataBuffer pointed by cur(parent object(Complex or Array)) and index(point to the element))
     */
    public static CSONElement getElement(ReadCursor cur,int index){
        byte elementTC = cur.getReader().readElementType(cur, index);
        if (elementTC==CSONTypes.NullValue.getValue()) return CSONElement.NullValue;
        else if (elementTC==CSONTypes.NullElement.getValue()) return CSONElement.NullElement;
        else return new CSONElement(elementTC,cur,index);
    }
    public static int getIndex(Object propertyIndex){
        if (propertyIndex instanceof  Integer){
            return (Integer)propertyIndex;
        }else if(propertyIndex instanceof  String){
            return Integer.parseInt((String)propertyIndex);
        }else{
            throw new IllegalArgumentException();
        }
    }
    public static IEntitySchema getSchema(Object schemaType){
        if(schemaType instanceof IEntitySchema){
            return  (IEntitySchema)schemaType;
        }else if (schemaType instanceof CSONComplexType){
            return ((CSONComplexType)schemaType).getSchema();
        }else if(schemaType instanceof CSONArrayType){
            return ((CSONArrayType)schemaType).getElementSchema();
        }else{
            return null;
        }
    }
    /**
     * Used by CSONArrayType constructor. If this array should contain a simple type element, the caller must send CSONSimpleType
     */
    public static CSONSimpleType getElementType(Object schemaOrType){
        if(schemaOrType instanceof  CSONSimpleType){
            return (CSONSimpleType)schemaOrType;
        }else{
            return null;
        }
    }
    public static byte getElementTypeCode(Object schemaOrType){
        CSONSimpleType simpleType = getElementType(schemaOrType);
        if (simpleType != null){
            return  simpleType.getTypeCode();
        }else{
            return CSONTypesArray.NullType.getTypeCode();
        }
    }
    public static CSONComplexType getComplexType(Object schemaType){
        if (schemaType instanceof IEntitySchema){
            return new CSONComplexType((IEntitySchema)schemaType);
        }else{
            return (CSONComplexType)schemaType;
        }
    }
    public static CSONArrayType getArrayType(Object schemaType){
        if (schemaType instanceof IEntitySchema){
            return new CSONArrayType((IEntitySchema)schemaType);
        }else if (schemaType instanceof  CSONArrayType) {
            return  (CSONArrayType)schemaType;
        }else{
          return new CSONArrayType(null);
        }
    }

    public CSONElement(byte typeCode){
        thisElementType = CSONTypesArray.CSONElementTypes[typeCode];
        value = null;
    }



    private IElementType thisElementType;
    private Object value;

    public CSONElement(byte typeCode,ReadCursor cur,int index){
        this(typeCode);
        CSONTypes tc = CSONTypesArray.CSONElementTypes[typeCode].getCsonTypes();
        if (tc != CSONTypes.NullValue && tc != CSONTypes.NullElement){
            value = thisElementType.getValue.apply(cur,index);
        }
    }
    public CSONElement (byte typeCode, Object v) {
        this(typeCode);
        value = v;
    }
    @Override
    public IElementType elementType(){
        return  thisElementType;
    }

    @Override
    public void setValue(Object newValue){
        if (newValue instanceof CSONElement){
            value = ((CSONElement)newValue).getValue();
        }else{
            // Timestamp type element is a long value store the Unix ticket
           if (thisElementType.getTypeCode() == CSONTypes.Timestamp.getValue() && value instanceof Date){
               value = ((Date)newValue).getTime();
           }else{
               value =newValue;
           }
        }
    }
    @Override
    public Object getValue(){
        return  value;
    }
}
