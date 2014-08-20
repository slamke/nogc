package com.cffex.nogc.cson.access;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import com.cffex.nogc.cson.core.entityInterface.common.IElementType;
import com.cffex.nogc.cson.core.entityInterface.common.IEntitySchema;
import com.cffex.nogc.cson.core.types.CSONArrayType;
import com.cffex.nogc.cson.core.types.CSONComplexType;
import com.cffex.nogc.cson.core.types.CSONTypesArray;

/**
 * Created by sunke on 2014/7/30.
 * 元数据cache
 */
public class EntitySchemaCache {
    private static ConcurrentHashMap<String,IElementType> schemaMap  = new ConcurrentHashMap<String,IElementType>();
    private static ConcurrentHashMap<String,Field[]> fieldInfoCache = new ConcurrentHashMap<String,Field[]>();
    private static ConcurrentHashMap<String,Method[]> fieldGetCache = new ConcurrentHashMap<String,Method[]>();
    private static ConcurrentHashMap<String,Method[]> fieldSetCache = new ConcurrentHashMap<String,Method[]>();

    private static IElementType addSchemaType(String key, Class objType){
        IElementType ret = null;
        //数组和类 二选一
        if (!objType.isArray()){
           ret = new CSONComplexType(new GeneralEntitySchema(objType));
        } else {
            Class objectTypeInfo = objType.getComponentType();
            IElementType elementType = CSONTypesArray.typeFactory(objectTypeInfo);
            if (elementType != null){
                ret = new CSONArrayType(elementType);
            }else {
                ret = new CSONArrayType(objectSchemaFactory(objectTypeInfo));
            }
        }
        schemaMap.put(key, ret);
        return ret;
    }
    private static Field[]  addFi(String key, Field[] fiList){
        fieldInfoCache.put(key, fiList);
        return fiList;
    }

    private static Field[] getAllFields(Class<?> typeInfo){
        Class superType = typeInfo.getSuperclass();
        if (superType.getName()=="java.lang.Object") return typeInfo.getDeclaredFields();
        else {
            Field[] superClassFields = getAllFields(superType);
            Field[] thisFields = typeInfo.getDeclaredFields();
            Field[] ret = new Field[superClassFields.length+thisFields.length];
            for (int i= 0;i<superClassFields.length;i++){
                ret[i] = superClassFields[i];
            }
            for (int i = 0;i<thisFields.length;i++){
                ret[i+superClassFields.length] = thisFields[i];
            }
            return ret;
        }
    }
    public static Field[] getFieldInfo(Class<?> typeInfo){
        String key = typeInfo.getName();
        Field[] fiInfo = fieldInfoCache.get(key);
        if (fiInfo==null) {
            Field[]  ret = getAllFields(typeInfo);
            fieldInfoCache.put(key, ret);
            return ret;
        }
        else return fiInfo;
    }
    public static Field[] getFiCache(String typeName){
        Field[] ret = fieldInfoCache.get(typeName);
        if (ret == null) return null;
        else return ret;
    }
    public static Method[] getFiGetCache(String typeName){return fieldGetCache.get(typeName);}
    public static Method[] addFiGet(String key, Method[] FiGetList){
        fieldGetCache.put(key, FiGetList);
        return FiGetList;
    }
    public static Method[] getFiSetCache(String typeName){return  fieldSetCache.get(typeName);}
    public static Method[] addFiSet(String key, Method[] FiSetList){
        fieldSetCache.put(key, FiSetList);
        return  FiSetList;
    }

    public static IEntitySchema objectSchemaFactory(Class<?> objectType){
        //提取Class的元素-->field等-->获取schema
        IElementType type = objectElementTypeFactory(objectType);
        if (type instanceof  CSONComplexType){
            return ((CSONComplexType) type).getSchema();
        }else if(type instanceof CSONArrayType){
            return ((CSONArrayType) type).getElementSchema();
        }else{
            return null;
        }
    }
    public static IElementType objectElementTypeFactory(Class<?> objectType){
        String typeKey = objectType.getName();
        IElementType schemaType = schemaMap.get(typeKey);
        // If can't find in the cache, use the concurrent putIfAbsent to create and get the schema. If this put fail, try to read
        // this class's schema from the cache (It may be put into the cache by another thread)
        if (schemaType == null) {
            //添加schema
            IElementType ret = addSchemaType(typeKey,objectType);
            if (ret instanceof  CSONComplexType){
                CSONComplexType complexType = (CSONComplexType)ret;
                ((GeneralEntitySchema)complexType.getSchema()).populate();
            }
            return ret;
        }else{
            return schemaType;
        }
    }
}
