package com.cffex.nogc.cson.core.types;

import com.cffex.nogc.cson.core.entityInterface.common.IElementType;

/**
 * Created by sunke on 2014/7/23.
 * Array of IElementType -->index is the typeCode
 * CSON所有支持的数据类型对应的IElementType类型数组-->CSONSimpleType数组
 * 作用：通过初始化全局的IElementType类型数组，提供运行时的缓存，提高效率
 */
public class CSONTypesArray {
    public static  IElementType[] CSONElementTypes = new IElementType[CSONTypes.Single.getValue()+1];
    //初始化操作
    static{
        for (CSONTypes types:CSONTypes.values()){
            if (types !=CSONTypes.EmbeddedDocument && types!=CSONTypes.Array){
                CSONSimpleType simpleType = new CSONSimpleType(types);
                //simpleType.setCsonTypes(types);
                //simpleType.setTypeCode(types.getValue());
                CSONElementTypes[types.getValue()] = simpleType;
            }else if(types == CSONTypes.Array){   //仅仅用于解析typeCode至CSONTypes
                CSONSimpleType simpleType = new CSONSimpleType(types);
                CSONElementTypes[types.getValue()] = simpleType;
            }else if (types == CSONTypes.EmbeddedDocument){ //仅仅用于解析typeCode至CSONTypes
                CSONSimpleType simpleType = new CSONSimpleType(types);
                CSONElementTypes[types.getValue()] = simpleType;
            }
        }
    }

    public static CSONSimpleType NullType = (CSONSimpleType)CSONElementTypes[CSONTypes.NullElement.getValue()];
    public static CSONSimpleType BinaryType = (CSONSimpleType)CSONElementTypes[CSONTypes.BinaryData.getValue()];
    public static CSONSimpleType IntElementType = (CSONSimpleType)CSONElementTypes[CSONTypes.Int32.getValue()];
    public static CSONSimpleType StrElementType = (CSONSimpleType)CSONElementTypes[CSONTypes.UTF8String.getValue()];

    /**
     * 根据基本类型的类型名称解析对应IElementType-->CSONSimpleType
     * @param classType  基本类型的类型名称
     * @return 对应IElementType-->CSONSimpleType
     */
    public static IElementType typeFactory(Class classType){
        String typeName =  null;
        if (classType.isEnum()){
            typeName = "int";
        }else{ typeName = classType.getName();}
        if (typeName.equals("boolean")|| typeName.equals("java.lang.Boolean")) return CSONElementTypes[CSONTypes.Boolean.getValue()];
        else if(typeName.equals("byte") || typeName.equals("java.lang.Byte")) return CSONElementTypes[CSONTypes.Int8.getValue()];
        else if(typeName.equals("short")|| typeName.equals("java.lang.Short")) return CSONElementTypes[CSONTypes.Int16.getValue()];
        else if(typeName.equals("int")||typeName.equals("java.lang.Integer")) return CSONElementTypes[CSONTypes.Int32.getValue()];
        else if(typeName.equals("java.math.BigDecimal")) return CSONElementTypes[CSONTypes.Decimal.getValue()];
        else if(typeName.equals("double" )||typeName.equals("java.lang.Double")) return CSONElementTypes[CSONTypes.FloatingPoint.getValue()];
        else if(typeName.equals("float")|| typeName.equals("java.lang.Float")) return CSONElementTypes[CSONTypes.Single.getValue()];
        else if(typeName.equals("long") || typeName.equals("java.lang.Long")) return CSONElementTypes[CSONTypes.Int64.getValue()];
        else if(typeName.equals("java.lang.String")) return CSONElementTypes[CSONTypes.UTF8String.getValue()];
        else if(typeName.equals("java.util.Date")) return CSONElementTypes[CSONTypes.UTCDatetime.getValue()];
        else if(typeName.equals("java.util.UUID")) return CSONElementTypes[CSONTypes.ObjectId.getValue()];
        else if(typeName.equals("[B")||typeName.equals("[Ljava.lang.Byte")) return CSONElementTypes[CSONTypes.BinaryData.getValue()];
        else return null;
    }

    /**
     * 根据基本类型的类型名称解析对应的枚举类型bytecode
     * @param classType 基本类型的类型名称
     * @return 对应的枚举类型bytecode
     */
    public static byte typeCodeOf(Class classType){
        String typeName =  null;
        if (classType.isEnum()){
            typeName = "int";
        }else{ typeName = classType.getName();}

        if (typeName.equals("boolean")|| typeName.equals("java.lang.Boolean")) return CSONTypes.Boolean.getValue();
        else if(typeName.equals("byte") || typeName.equals("java.lang.Byte")) return  CSONTypes.Int8.getValue() ;
        else if(typeName.equals("short")|| typeName.equals("java.lang.Short")) return  CSONTypes.Int16.getValue() ;
        else if(typeName.equals("int")||typeName.equals("java.lang.Integer")) return  CSONTypes.Int32.getValue() ;
        else if(typeName.equals("java.math.BigDecimal")) return  CSONTypes.Decimal.getValue() ;
        else if(typeName.equals("double" )||typeName.equals("java.lang.Double")) return  CSONTypes.FloatingPoint.getValue() ;
        else if(typeName.equals("float")|| typeName.equals("java.lang.Float")) return  CSONTypes.Single.getValue() ;
        else if(typeName.equals("long") || typeName.equals("java.lang.Long")) return  CSONTypes.Int64.getValue() ;
        else if(typeName.equals("java.lang.String")) return  CSONTypes.UTF8String.getValue() ;
        else if(typeName.equals("java.util.Date")) return  CSONTypes.UTCDatetime.getValue() ;
        else if(typeName.equals("java.util.UUID")) return  CSONTypes.ObjectId.getValue() ;
        else if(typeName.equals("[B")||typeName.equals("[Ljava.lang.Byte")) return  CSONTypes.BinaryData.getValue() ;
        else return -1;
    }
}
