package com.cffex.nogc.cson.access;

import java.util.ArrayList;
import java.util.List;

import com.cffex.nogc.cson.core.entityInterface.common.IElementType;
import com.cffex.nogc.cson.core.entityInterface.common.IEntitySchema;
import com.cffex.nogc.cson.core.enumeration.EntityType;
import com.cffex.nogc.cson.core.types.CSONArrayType;
import com.cffex.nogc.cson.core.types.CSONComplexType;
import com.cffex.nogc.cson.core.types.CSONSimpleType;
import com.cffex.nogc.cson.core.types.CSONTypes;

/**
 * Created by sunke on 2014/7/30.
 */
public class DynamicSchema extends IEntitySchema {
    public DynamicSchema(String id){
        this.id = id;
    }
    private String id;
    private List<String> propertyList = new ArrayList<String>();
    private List<IElementType> propertyTypeList = new ArrayList<IElementType>();
    public int getIndex(Object property){
        if (property instanceof  Integer){
            return (Integer)property;
        }else if (property instanceof String){
            if (Character.isDigit(((String) property).charAt(0)))
                return Integer.parseInt((String)property);
            else
                return  getID((String)property);
        }else{
            throw new IllegalArgumentException("Property index should be int or String.");
        }
    }


    public List<String> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(ArrayList<String> propertyList) {
        this.propertyList = propertyList;
    }

    public List<IElementType> getPropertyTypeList() {
        return propertyTypeList;
    }

    public void setPropertyTypeList(ArrayList<IElementType> propertyTypeList) {
        this.propertyTypeList = propertyTypeList;
    }

    @Override public String entityId(){
        return id;
    }
    @Override public int count(){return propertyList.size();}
    @Override public EntityType schemaType(){return EntityType.View;}
    @Override public boolean containsProperty(String property){ return  propertyList.contains(property);}
    @Override public Class objType(){ return null;}
    @Override public int getID(String propertyName){ return  propertyList.indexOf(propertyName);}
    @Override public String getPropertyName(int ID){return  propertyList.get(ID);}
    @Override public byte getTypeCode(Object property){
        IElementType type = getElementType(property);
        if (type instanceof CSONSimpleType){
            return  ((CSONSimpleType)type).getTypeCode();
        }else if (type instanceof CSONComplexType){
            return CSONTypes.EmbeddedDocument.getValue();
        }else if (type instanceof CSONArrayType){
            return CSONTypes.Array.getValue();
        }
        return  -1;
    }
    @Override public IElementType getElementType(Object property){return propertyTypeList.get(getIndex(property));}
}
