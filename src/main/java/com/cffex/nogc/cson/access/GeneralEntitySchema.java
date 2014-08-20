package com.cffex.nogc.cson.access;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

import com.cffex.nogc.cson.core.ReadCursor;
import com.cffex.nogc.cson.core.entityInterface.common.IElementType;
import com.cffex.nogc.cson.core.entityInterface.common.IEntitySchema;
import com.cffex.nogc.cson.core.enumeration.EntityType;
import com.cffex.nogc.cson.core.types.CSONArrayType;
import com.cffex.nogc.cson.core.types.CSONComplexType;
import com.cffex.nogc.cson.core.types.CSONSimpleType;
import com.cffex.nogc.cson.core.types.CSONTypes;
import com.cffex.nogc.cson.core.types.CSONTypesArray;

/**
 * Created by sunke on 2014/7/22.
 *  Super DTO class with child classes, should implement a function named "schemaChooser" for get correct schema and serializer
 */
public class GeneralEntitySchema extends IEntitySchema{
    public GeneralEntitySchema(Class<?> objectType){
        this.objectType = objectType;
        entityName = objectType.getName();
        entityFieldInfo = EntitySchemaCache.getFieldInfo(objectType);
        propertyNames = null;
        propertyTypes = null;
        chooser = null;
        _getChildSchema = null;
    }
    private Class objectType;
    private String entityName;
    private Field[] entityFieldInfo;
    private String[] propertyNames;
    private IElementType[] propertyTypes;
    private Method chooser;
    private Function<ReadCursor,IEntitySchema> _getChildSchema;


    private Function<ReadCursor,IEntitySchema> childSchemaChooser(Class<?> objType){
        try {
            chooser = objType.getDeclaredMethod("schemaChooser", Object.class);
        }catch (NoSuchMethodException e){ // Ignore the method not found exception
            chooser = null;
        }catch (Exception e){
            throw  e;
        }
        if (chooser==null) return null;
        else return (ReadCursor cur) ->{
            try {
                return (IEntitySchema)chooser.invoke(objectType.newInstance(),(Object)(getElementType(0).getValue.apply(cur,0)));
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }catch (InstantiationException e){
                e.printStackTrace();
            }catch (InvocationTargetException e){
                e.printStackTrace();
            }
            return  null;
        };
    }

    private CSONArrayType getArrayTypeInfo(Class componentTypeInfo) throws  Exception{
        // We can define "Type Binary = Array[Byte]" in app then define binary array in this way "val barr = new Array[Binary](1)"
        IElementType elementType = getTypeInfo(componentTypeInfo);
        if (elementType instanceof CSONArrayType) throw new Exception("Can't support Array of Array type.");
        else return new CSONArrayType(elementType);
    }
    private IElementType getTypeInfo(Class fieldTypeInfo) throws  Exception{
        if (fieldTypeInfo.isArray()&&fieldTypeInfo.getName()!="[B") return getArrayTypeInfo(fieldTypeInfo.getComponentType());
        else {
            IElementType elementType = CSONTypesArray.typeFactory(fieldTypeInfo);
            // This is a class type
            if (elementType == null) return EntitySchemaCache.objectElementTypeFactory(fieldTypeInfo);
            else return elementType;
        }
    }
    private void getProperties(Field[] fields){
        propertyNames = new String[fields.length];
        propertyTypes = new IElementType[fields.length];
        Method[] fieldGetList = new Method[fields.length];
        Method[] fieldSetList = new Method[fields.length];
        int index = 0;
        try {
            for (Field field:fields){
                //int mf = field.getModifiers();
                //if (mf == 2){ //Modifiers为2对应于private属性
                try {
                    String fieldName = field.getName();
                    Class fieldTypeInfo = field.getType();
                    propertyNames[index] = fieldName;
                    //获取getter方法
                    Method getter = new PropertyDescriptor(fieldName,objectType).getReadMethod();
                    fieldGetList[index] = getter;//objectType.getMethod(fieldName);
                    //获取setter方法
                    Method setter = new PropertyDescriptor(fieldName,objectType).getWriteMethod();
                    fieldSetList[index] = setter;//objectType.getMethod(fieldName + "_$eq", fieldTypeInfo);
                    propertyTypes[index] = getTypeInfo(fieldTypeInfo);
                    index += 1;
                }catch (NoSuchMethodException e){
                    continue;  //index不增加，过滤该属性（没有对应的getter 和setter）
                }catch (IntrospectionException ex){
                    continue;//index不增加，过滤该属性（没有对应的getter 和setter）
                }
                //}
            }
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        String key = objectType.getName();
        if (fields.length>index) {
            Method[] tGetList = null;
            tGetList = Arrays.copyOfRange(fieldGetList, 0, index);
            EntitySchemaCache.addFiGet(key, tGetList);
            Method[] tSetList = null;
            tSetList = Arrays.copyOfRange(fieldSetList, 0, index);
            EntitySchemaCache.addFiSet(key, tSetList);
            String[] tpNames = null;
            tpNames = Arrays.copyOfRange(propertyNames, 0, index);
            propertyNames = tpNames;
            IElementType[] tpTypes = null;
            tpTypes = Arrays.copyOfRange(propertyTypes, 0, index);
            propertyTypes = tpTypes;
        }
        else {
            EntitySchemaCache.addFiGet(key, fieldGetList);
            EntitySchemaCache.addFiSet(key, fieldSetList);
        }
    }

    private int getIndex(Object propertyIndex){
        if (propertyIndex instanceof  Integer){
            return (Integer) propertyIndex;
        }else if(propertyIndex instanceof  String){
            if(Character.isDigit(((String) propertyIndex).charAt(0))){
                return Integer.parseInt((String)propertyIndex);
            }else{
                return getID((String)propertyIndex);
            }
        }else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * 处理继承问题。。。
     */
    public void populate(){
        if (propertyNames==null) {
            getProperties(entityFieldInfo);
            _getChildSchema = childSchemaChooser(objectType);
            getChildSchema = _getChildSchema;
        }
    }
    public String entityId(){     return objectType.getName(); }
    public int count(){ return  propertyNames.length; }
    public EntityType schemaType(){ return  EntityType.ComplexType; }
    public boolean containsProperty(String property){
        if (propertyNames != null){
            for (String p:propertyNames){
                if (p.equals(property)){
                    return true;
                }
            }
            return  false;
        }else{
            return false;
        }
    }
    public Class objType(){return objectType;}
    public int getID(String propertyName){
        if (propertyNames != null){
            for (int i =0;i<propertyName.length();i++){
                String p = propertyNames[i];
                if (p.equals(propertyName)){
                    return i;
                }
            }
            return  -1;
        }else{
            return  -1;
        }
    }
    public String getPropertyName(int ID){ return  propertyNames[ID];}
    public byte getTypeCode(Object index){
        IElementType elementTypes = propertyTypes[getIndex(index)];
        if (elementTypes instanceof  CSONSimpleType){
            return ((CSONSimpleType)elementTypes).getTypeCode();
        }else if(elementTypes  instanceof  CSONComplexType){
            return CSONTypes.EmbeddedDocument.getValue();
        }else if(elementTypes instanceof  CSONArrayType){
            return  CSONTypes.Array.getValue();
        }else{
            return -1;
        }
    }
    /**
     * Return the element IElementType if this property is an array,
     * return CSONComplexType if this property is a Complex Element.
     */
    public IElementType getElementType(Object property){
        return propertyTypes[getIndex(property)];
    }
    public Function<ReadCursor,IEntitySchema> getChildSchema(ReadCursor cursor){return _getChildSchema;}
    public static IEntitySchema apply(Class objectType){
        return ((CSONComplexType)(EntitySchemaCache.objectElementTypeFactory(objectType))).getSchema();
    }
}
