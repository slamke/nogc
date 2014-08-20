package com.cffex.nogc.cson.access;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sunke on 2014/7/30.
 */
public class EntitySerializerCache {
    private static  ConcurrentHashMap<String,GeneralEntityToCSON> serializerCache = new ConcurrentHashMap<String, GeneralEntityToCSON>();

    public  static  GeneralEntityToCSON getEntityToCSON(Class typeInfo){
        String key = typeInfo.getName();
        GeneralEntityToCSON serializer = serializerCache.get(key);
        if (serializer==null) {
            GeneralEntityToCSON ret = new GeneralEntityToCSON(typeInfo);
            serializerCache.put(key, ret);
            return  ret;
        }
        else return serializer;
    }
}
