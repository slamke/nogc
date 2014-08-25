package com.cffex.nogc.cson.access;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.cffex.nogc.cson.core.CSONDocument;
import com.cffex.nogc.cson.core.elements.CSONArrayElement;
import com.cffex.nogc.cson.core.elements.CSONComplexElement;
import com.cffex.nogc.cson.core.elements.CSONElement;
import com.cffex.nogc.cson.core.entityInterface.access.IEntityRandomAccess;
import com.cffex.nogc.cson.core.entityInterface.common.IElementType;
import com.cffex.nogc.cson.core.entityInterface.common.IElementValue;
import com.cffex.nogc.cson.core.entityInterface.common.IEntitySchema;
import com.cffex.nogc.cson.core.types.CSONArrayType;
import com.cffex.nogc.cson.core.types.CSONComplexType;
import com.cffex.nogc.cson.core.types.CSONSimpleType;
import com.cffex.nogc.cson.core.types.CSONTypes;
import com.cffex.nogc.cson.core.types.CSONTypesArray;
import com.cffex.nogc.cson.core.utils.Tuple;

/**
 * Created by sunke on 2014/7/22.
 * 通用的cson序列化和反序列化的工具类
 */
public class GeneralEntityToCSON {

    /**
     *构造函数
     * @param schema--对象的schema元数据
     */
    public GeneralEntityToCSON(IEntitySchema schema){
        if (schema != null){
            count = schema.count();
        }else{
            count = 0;
        }
        this.schema = schema;
    }

    /**
     * 构造函数
     * @param classOfEntity 对象的Class
     */
    public GeneralEntityToCSON(Class classOfEntity){
        //根据entitySchemaCache提取Class的schema，并且进行缓存
        this(EntitySchemaCache.objectSchemaFactory(classOfEntity));
    }

    /**
     * field数量
     */
    private int count;
    /**
     * 元数据类型
     */
    private IEntitySchema schema;
    // As implementation tricks of scala array, if the component is class object, we can use the generic "Array[AnyRef]" to hold it's content
    // For value type array, it's instance must be created in exact type, eg. "Array[Int], Array[String]..."
    private Object getComplexElementArray(CSONArrayElement elementArray,Class componentType){
        Object[] objArray = (Object[])java.lang.reflect.Array.newInstance(componentType, elementArray.length());
        for (int i= 0;i < elementArray.length();i++){
            objArray[i] = getValueFromCSONElement(elementArray.getElement(i));
        }
        return objArray;
    }
    /**
     * Deserializer for reading the object value from CSONBuffer
     */
    private Object getValueFromCSONElement(IElementValue element){
        //new Array[AnyRef]()
        Object ret = null;
        try {
            if (element instanceof CSONElement){
                ret = ((CSONElement)element).getValue();
            }else if(element instanceof CSONComplexElement){
                IEntitySchema elementSchema = ((CSONComplexElement)element).getSchema();
                Class objType = elementSchema.objType();
                String elementClassName = elementSchema.entityId();
                Field[] elementFieldInfo = EntitySchemaCache.getFiCache(elementClassName);
                ret = objType.newInstance();
                int i = 0;
                Method[] methods =  EntitySchemaCache.getFiSetCache(elementClassName);
                for (Method setter:methods){
                    if (elementFieldInfo[i].getType().isEnum()) {
                        CSONComplexElement csonComplexElement = (CSONComplexElement)element;
                        int a = (int)csonComplexElement.getValue(i);
                        //通过setter获取枚举的类型
                        //Type[] params = setter.getGenericParameterTypes();
                        Class clazz = elementFieldInfo[i].getType();
                        //获取枚举类型的static values函数
                        Method values = ((Class)clazz).getDeclaredMethod("values");
                        //调用values函数，获取所有的枚举项目
                        Object object = values.invoke((Class)clazz);
                        Object[] objects = (Object[]) object;
                        //Method ordinal = Enum.class.getDeclaredMethod("ordinal");
                        Object enumObj= objects[a];
                        //依次调用枚举项的ordinal函数，判断其是否与所存储数据相同，进行匹配
                        //for (Object en:objects){
                        //   Object res = ordinal.invoke(en);
                        //    if (a == (Integer)res){
                        //        enumObj = en;
                        //    }
                        //}
                        setter.invoke(ret,enumObj);
                    }else{
                        CSONComplexElement complexElement = (CSONComplexElement)element;
                        setter.invoke(ret, (Object)getValueFromCSONElement(complexElement.getElement(i)));
                    }
                    i+=1;
                }
            }else if(element instanceof CSONArrayElement){
                IEntitySchema elementSchema = ((CSONArrayElement)element).getSchema();
                if (elementSchema!=null){
                    ret = getComplexElementArray((CSONArrayElement)element,elementSchema.objType());
                }
                else ret = ((CSONArrayType)((CSONArrayElement)element).elementType()).getElementType().writeArrayValue.apply((CSONArrayElement) element);
            }
        }catch (InstantiationException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }
    public Object readCSONToObject(ByteBuffer dataBuffer){
        CSONDocument csonDoc = new CSONDocument(schema,dataBuffer);
        Object ret = getValueFromCSONElement(csonDoc.toCSONElement());
        csonDoc.completeRead();
        return  ret;
    }
    public Object getObject(CSONDocument csonDoc){
        return  getValueFromCSONElement(csonDoc.toCSONElement());
    }
    public Object getObject(IElementValue complexElement){
        return  getValueFromCSONElement(complexElement);
    }

    /*
        Serializer functions to write object values into CSONBuffer
     */
    private IEntityRandomAccess getCSONElementFromObject(Object value,IEntityRandomAccess target){
        IEntityRandomAccess targetElement = null;
        try {
            if (target != null){
                targetElement  = target;
            }else{
                targetElement = new CSONComplexElement(EntitySchemaCache.objectSchemaFactory(value.getClass()),null);
            }
            IEntitySchema schema = targetElement.getSchema();
            Method[] fiGetList = EntitySchemaCache.getFiGetCache(schema.entityId());
            for (int i = 0;i < schema.count();i++) {
                Object propertyValue = fiGetList[i].invoke(value);
                if (propertyValue == null) {
                    targetElement.setValue(i, CSONElement.NullValue);
                }else{
                    IElementType type = schema.getElementType(i);
                    Object object = null;
                    if (type instanceof  CSONSimpleType){
                        object = propertyValue;
                    }else if(type instanceof CSONComplexType){
                        object = getCSONElementFromObject(propertyValue,null);
                    } else if(type instanceof  CSONArrayType){
                        CSONSimpleType childElementType = ((CSONArrayType)schema.getElementType(i)).getElementType();
                        object = getCSONArrayFromObject(propertyValue,childElementType);
                    }
                    targetElement.setValue(i,object);
                }
            }
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (InvocationTargetException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return targetElement;
    }

    private CSONArrayElement getCSONArrayFromObject(Object value ,Object schemaOrType){
        CSONArrayElement elementArray = new CSONArrayElement(schemaOrType,null);
        CSONSimpleType elementType = CSONElement.getElementType(schemaOrType);
        if (elementType == null) {
            Object[] objArray = (Object[])value;
            int len = objArray.length;
            //println("Array2CSON("+len+")->"+value)
            for (int i =0;i <len;i++) {
                //println("item("+i+")="+objArray(i))
                if (objArray[i] == null) elementArray.append(CSONElement.NullValue );
                else elementArray.append(getCSONElementFromObject(objArray[i], null));

            }
        }else elementType.readArrayValue.accept(elementArray, value);
        return elementArray;
    }

    /**
     * Serialize the given value to a CSONDocument(in this serializer specified schema)
     * 将对象序列化至cson的接口
     * 前置条件：根据class构造 this GenernalEntityToCSON
     */
    public Tuple<IEntityRandomAccess,ByteBuffer> writeObjectToCSON(Object value,ByteBuffer dataBuffer){
        CSONDocument doc = new CSONDocument(schema,null);
        // 数据写入doc中
        getCSONElementFromObject(value,doc);
        if (dataBuffer!=null){
            return new Tuple<IEntityRandomAccess,ByteBuffer>(doc,doc.toByteBuffer(dataBuffer));
        }else{
            return new Tuple<IEntityRandomAccess,ByteBuffer>(doc,null);
        }
    }

    /**
     * Serialize Plain Object into a CSON format Byte Array
     * @param value plain object to be serialized
     * @param dataBuffer ByteBuffer to be written. Notice: if you want to get Array[Byte], you have to set this null
     * @return Array[Byte] encoded in CSON format if dataBuffer param is null. Otherwise return (null, ByteBuffer contains
     *         the encoded CSON format binary). This ByteBuffer size may be insufficient and new ByteBuffer generated and returned.
     */
    public Tuple<byte[],ByteBuffer> getObjectCsonBinary(Object value,ByteBuffer dataBuffer){
        CSONDocument doc = new CSONDocument(schema,null);
        getCSONElementFromObject(value,doc);
        if (dataBuffer!=null) return  new Tuple<byte[],ByteBuffer>(null,doc.toByteBuffer(dataBuffer));
        else return new Tuple<byte[],ByteBuffer>(doc.getBytes(),null);
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Tuple<IEntityRandomAccess,ByteBuffer> serializeObject(Object obj,ByteBuffer outBuffer){
        return  EntitySerializerCache.getEntityToCSON(obj.getClass()).writeObjectToCSON(obj,outBuffer);
    }
    public static  Object deserializeCSON(IEntityRandomAccess cson){
        GeneralEntityToCSON deserializer = new GeneralEntityToCSON(cson.getSchema());
        return deserializer.getObject((CSONDocument)cson);
    }
    public static  GeneralEntityToCSON apply(Class typeInfo){ return  EntitySerializerCache.getEntityToCSON(typeInfo);}
    public static  byte[] getRawValue(Object value ,boolean needTypeCode){
        ByteBuffer outBuffer = ByteBuffer.wrap(new byte[4096]).order(ByteOrder.LITTLE_ENDIAN);
        int len = 0;
        if (value==null) len = CSONElement.NullValue.elementType().getRawValue(outBuffer, value, needTypeCode).flip().limit();
        else {
            Class objectTypeInfo = value.getClass();
            IElementType elementType = CSONTypesArray.typeFactory(objectTypeInfo);
            if (elementType!=null) len = elementType.getRawValue(outBuffer, value, needTypeCode).flip().limit();
            else len = EntitySchemaCache.objectElementTypeFactory(objectTypeInfo).
                    getRawValue(outBuffer, value, needTypeCode).flip().limit();
        }
        byte[] ret = new byte[len];
        outBuffer.get(ret);
        return ret;
    }


    public static Object readRawValue2Obj(byte[] b,boolean needTypeCode){
        return readRawValue2Obj(b,needTypeCode,(byte)0);
    }
    /**
     * Deserialize Binary raw value to object
     * @param b :Array[Byte]
     * @param needTypeCode true for use the "typeCode" param to deserialize, false to read type code and decode
     * @param typeCode if needTypeCode = false, ignored
     * @return
     */
    public static Object readRawValue2Obj(byte[] b,boolean needTypeCode,byte typeCode){
        ByteBuffer bb = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN);
            byte rawValueType = 0;
        if (needTypeCode) rawValueType = typeCode; else rawValueType = bb.get();
        return  convertRawValue(rawValueType,bb);
    }


    private static  Object convertRawValue(byte typeCode,ByteBuffer bb){
        CSONTypes types = CSONTypesArray.CSONElementTypes[typeCode].getCsonTypes();
        switch (types){
        case Boolean: return (bb.getInt() == 1);
        case Int8: return bb.getInt();
        case Int16: return  bb.getInt();
        case Int32: return  bb.getInt();
        case FloatingPoint: return bb.getDouble();
        case Single: return bb.getFloat();
        case UTF8String:{
            try {
                byte[] strBytes = new byte[bb.getInt()];
                bb.get(strBytes);
                return new String(strBytes, "UTF-8");
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();;
            }
        }
        case JavaScriptCode:
            try {
                byte[] strBytes = new byte[bb.getInt()];
                bb.get(strBytes);
                return new String(strBytes, "UTF-8");
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();;
            }
        case BinaryData:
            byte[] buf = new byte[bb.getInt()];
            bb.get(buf);
            return buf;
        case Decimal:
            int signPart = bb.getInt();
            if (signPart == 0) return BigDecimal.ZERO;
            BigDecimal longPart = new BigDecimal(bb.getLong());
            BigDecimal mantissaPart = (new BigDecimal(bb.getInt())).movePointLeft(4);
            if (signPart < 0) return longPart.add(mantissaPart).negate();
            else  return  longPart.add(mantissaPart);
        case Int64: return  bb.getLong();
        case Timestamp: return  new java.util.Date(bb.getLong());
        case UTCDatetime: return  new java.util.Date(bb.getLong()).getTime();
        case ObjectId:
        case DBPointer:
            long highBits = bb.getLong();
            long LowBits = bb.getLong();
            return new java.util.UUID(highBits, LowBits);
        case NullValue: return  bb.getInt(0);
        case NullElement: return  bb.getInt(0);
        default: return null;
    }
    }
    public static String binaryToString(byte[] b){
        try {
            return new String(  b , "ISO-8859-1" );
        }catch (Exception e){
            return  null;
        }
    }
    public static byte[] hexToBinary(String s,ByteBuffer bb){
        return null;//val ret = if (bb!=null) bb else ByteBuffer.
    }

}
