package com.cffex.nogc.cson.core.types;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.cffex.nogc.cson.core.ReadCursor;
import com.cffex.nogc.cson.core.WriteCursor;
import com.cffex.nogc.cson.core.elements.CSONArrayElement;
import com.cffex.nogc.cson.core.entityInterface.common.IElementType;

/**
 * Created by sunke on 2014/7/29.
 * 基础类型-->实现了IElementType接口
 * 作用：同lambda表达式，建立基本类型的读写函数表和读写对应数组的函数表
 */
public class CSONSimpleType extends IElementType {

    public CSONSimpleType(CSONTypes types){
        this.csonTypes = types;
        typeCode = types.getValue();
        getValue = getValueFunc(types);
        writeRawFunc = getWriteValueFunc(types);
        writeElement = (Object value,WriteCursor cur,int index)->{
            try {
                cur.getWriter().writeValue(typeCode, value, cur, index);
            }catch (Exception e){e.printStackTrace();}
        };
        readArrayValue = getSimpleTypeArrayFunc(csonTypes);
        writeArrayValue = getSimpleTypeArrayWriter(csonTypes);
    }

    public byte getTypeCode(){
        return typeCode;
    }

    public BiConsumer<CSONArrayElement,Object> readArrayValue;
    public Function<CSONArrayElement,Object> writeArrayValue;

    /**
     * 根据类型获取简单类型的写数组lambda表达式
     * @param types 基本类型 enum
     * @return  写数组lambda表达式
     */
    private Function<CSONArrayElement,Object> getSimpleTypeArrayWriter(CSONTypes types) {
        switch(types){
            case Boolean:
                return  (elementArr)-> {
                    boolean[] ret = new boolean[elementArr.length()];
                    for (int i= 0;i< elementArr.length();i++) {
                        ret[i] = (Boolean)elementArr.getValue(i);
                    }
                    return  ret;
                };
            case Int8:
                return (CSONArrayElement elementArr) -> {
                    byte[] ret = new byte[elementArr.length()];
                    for (int i = 0; i < elementArr.length();i++){
                        ret[i] = (Byte)elementArr.getValue(i);
                    }
                    return ret;
                };
            case Int16:  return (CSONArrayElement elementArr) -> {
                    short[] ret = new short[elementArr.length()];
                    for (int i = 0; i < elementArr.length();i++) {
                        ret[i] = (Short) elementArr.getValue(i);
                    }
                    return ret;
                };
            case Int32:  return (CSONArrayElement elementArr) -> {
                    int[] ret = new int[elementArr.length()];
                    for (int i = 0; i < elementArr.length();i++){ ret[i] =(Integer)elementArr.getValue(i);}
                    return  ret;
                };
            case FloatingPoint :  return (CSONArrayElement elementArr) -> {
                    double[] ret = new double[elementArr.length()];
                    for (int i = 0; i < elementArr.length();i++){ ret[i] = (Double)elementArr.getValue(i);}
                    return ret;
                };
            case Single :  return (CSONArrayElement elementArr) -> {
                    float[] ret = new float[elementArr.length()];
                    for (int i = 0; i < elementArr.length();i++){ ret[i] = (Float)elementArr.getValue(i);}
                    return ret;
                };
            case UTF8String :  return (CSONArrayElement elementArr) -> {
                    String[] ret = new String[elementArr.length()];
                    for (int i = 0; i < elementArr.length();i++){ ret[i] = (String)elementArr.getValue(i);}
                    return ret;
                };
            case BinaryData :  return (CSONArrayElement elementArr) -> {
                    byte[][] ret = new byte[elementArr.length()][];
                    for (int i = 0; i < elementArr.length();i++){ ret[i] = (byte[])elementArr.getValue(i);}
                    return ret;
                };
            case Decimal :  return (CSONArrayElement elementArr) ->  {
                    BigDecimal[] ret = new BigDecimal[elementArr.length()];
                    for (int i = 0; i < elementArr.length();i++){ ret[i]= (BigDecimal)elementArr.getValue(i);}
                    return  ret;
                };
            case Int64 :  return (CSONArrayElement elementArr) ->  {
                    long[] ret = new long[elementArr.length()];
                    for (int i = 0; i < elementArr.length();i++){ ret[i] = (Long)elementArr.getValue(i);}
                    return ret;
            };
            case Timestamp :  return (CSONArrayElement elementArr) ->  {
                    long[] ret = new long[elementArr.length()];
                    for (int i = 0; i < elementArr.length();i++){ ret[i] = (Long)elementArr.getValue(i);}
                    return ret;
                };
            case UTCDatetime :  return (CSONArrayElement elementArr) ->  {
                    Date[] ret = new Date[elementArr.length()];
                    for (int i = 0; i < elementArr.length();i++){ ret[i] = (Date)elementArr.getValue(i);}
                    return ret;
                };
            case ObjectId :  return (CSONArrayElement elementArr) ->  {
                    UUID[] ret = new UUID[elementArr.length()];
                    for (int i = 0; i < elementArr.length();i++){ ret[i] = (UUID)elementArr.getValue(i);}
                    return ret;
                };
            case DBPointer : return (CSONArrayElement elementArr) -> {
                    UUID[] ret = new UUID[elementArr.length()];
                    for (int i = 0; i < elementArr.length();i++){ ret[i] = (UUID)elementArr.getValue(i);}
                    return ret;
                };
            case JavaScriptCode : return (CSONArrayElement elementArr) -> {
                    String[] ret = new String[elementArr.length()];
                    for (int i = 0; i < elementArr.length();i++){ ret[i] = (String)elementArr.getValue(i);}
                    return ret;
                };
            default:
                return null;
        }
    }

    /**
     * 根据类型获取简单类型的读数组lambda表达式
     * @param types 基本类型enum
     * @return 读数组lambda表达式
     */
    private BiConsumer<CSONArrayElement,Object> getSimpleTypeArrayFunc(CSONTypes types){
        switch (types){
            case Boolean: return (CSONArrayElement elementArr,Object arr)-> {
                boolean[] valueArr = (boolean[])arr;
                for (boolean a:valueArr) elementArr.append(a);
            };
            case Int8 : return (CSONArrayElement elementArr,Object arr)-> {
                byte[] valueArr = (byte[])arr;
                for (byte v: valueArr) elementArr.append(v);
            };
            case Int16:  return (CSONArrayElement elementArr,Object arr)-> {
                short[] valueArr = (short[])arr;
                for (short v :valueArr) elementArr.append(v);
            };
            case Int32:  return (CSONArrayElement elementArr,Object arr)-> {
                int[] valueArr = (int[])arr;
                for (int v: valueArr) elementArr.append(v);
            };
            case FloatingPoint :  return (CSONArrayElement elementArr,Object arr)-> {
                double[] valueArr = (double[])arr;
                for (double v : valueArr) elementArr.append(v);
            };
            case Single :  return (CSONArrayElement elementArr,Object arr)-> {
                float[] valueArr = (float[])arr;
                for (float v : valueArr) elementArr.append(v);
            };
            case UTF8String :  return (CSONArrayElement elementArr,Object arr)-> {
                String[] valueArr = (String[])arr;
                for (String v : valueArr) elementArr.append(v);
            };
            case BinaryData :  return (CSONArrayElement elementArr,Object arr)-> {
                byte[][] valueArr = (byte[][])arr;
                for (byte[] v : valueArr) elementArr.append(v);
            };
            case Decimal :  return (CSONArrayElement elementArr,Object arr)-> {
                BigDecimal[] valueArr = (BigDecimal[])arr;
                for (BigDecimal v : valueArr) elementArr.append(v);
            };
            case Int64 :  return (CSONArrayElement elementArr,Object arr)-> {
                long[] valueArr = (long[])arr;
                for (long v : valueArr) elementArr.append(v);
            };
            case Timestamp :  return (CSONArrayElement elementArr,Object arr)-> {
                long[] valueArr = (long[])arr;
                for (long v : valueArr) elementArr.append(v);
            };
            case UTCDatetime :  return (CSONArrayElement elementArr,Object arr)-> {
                Date[] valueArr = (Date[])arr;
                for (Date v : valueArr) elementArr.append(v);
            };
            case ObjectId :  return (CSONArrayElement elementArr,Object arr)-> {
                UUID[] valueArr = (UUID[])arr;
                for (UUID v : valueArr) elementArr.append(v);
            };
            case DBPointer :  return (CSONArrayElement elementArr,Object arr)-> {
                UUID[]  valueArr = (UUID[])arr;
                for (UUID v : valueArr) elementArr.append(v);
            };
            case JavaScriptCode :  return (CSONArrayElement elementArr,Object arr)-> {
                String[] valueArr = (String[])arr;
                for (String v : valueArr) elementArr.append(v);
            };
            default: return null;
        }
    }

    /**
     * 根据类型获取简单类型的读数据lambda表达式
     * @param types 基本类型enum
     * @return 读数据lambda表达式
     */
    private BiFunction<ReadCursor ,Integer,Object> getValueFunc(CSONTypes types) {
        switch (types){
            case Boolean : return (ReadCursor cur,Integer index)-> { return cur.getReader().readBoolean(cur, index); };
            case Int8 : return (ReadCursor cur,Integer index)-> { return (byte)cur.getReader().readInt(cur, index); };
            case Int16: return (ReadCursor cur,Integer index)->  { return (short)cur.getReader().readInt(cur, index); };
            case Int32: return (ReadCursor cur,Integer index)->  {return  cur.getReader().readInt(cur, index); };
            case FloatingPoint : return (ReadCursor cur,Integer index)->  {return cur.getReader().readDouble(cur, index);};
            case Single : return (ReadCursor cur,Integer index)->  {return cur.getReader().readFloat(cur, index);};
            case UTF8String : return (ReadCursor cur,Integer index)->  {
                try {
                    return cur.getReader().readStrBytes(cur, index);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            };
            case BinaryData : return (ReadCursor cur,Integer index)->  {return cur.getReader().readBuf(cur, index);};
            case Decimal : return (ReadCursor cur,Integer index)->  {return cur.getReader().readDecimal(cur, index);};
            case Int64 : return (ReadCursor cur,Integer index)->  {return cur.getReader().readLong(cur, index);};
            case Timestamp : return (ReadCursor cur,Integer index)->  {return cur.getReader().readLong(cur, index);};
            case UTCDatetime : return (ReadCursor cur,Integer index)->  {return cur.getReader().readDatetime(cur, index);};
            case ObjectId : return (ReadCursor cur,Integer index)->  {return cur.getReader().readGuid(cur, index);};
            case DBPointer : return (ReadCursor cur,Integer index)->  {return cur.getReader().readGuid(cur, index);};
            case JavaScriptCode : return (ReadCursor cur,Integer index)->  {try {
                    return cur.getReader().readStrBytes(cur, index);
                }catch (Exception e){
                    e.printStackTrace();
                }
            return  null;
            };
            case NullValue :return (ReadCursor cur,Integer index)->  {return null;};
            case NullElement :return (ReadCursor cur,Integer index)->  {return null;};
            default: return null;
        }
    }

    /**
     * 根据类型获取简单类型的写数据lambda表达式
     * @param types 基本类型enum
     * @return 写数据lambda表达式
     */
    private BiConsumer<Object,ByteBuffer> getWriteValueFunc(CSONTypes types){
        switch (types) {
            case Boolean : return (Object value,ByteBuffer outBB)-> {
                if (value instanceof Boolean) outBB.putInt(1); else outBB.putInt(0);
            };
            case Int8 : return (Object value,ByteBuffer outBB)-> outBB.putInt((Byte)value);
            case Int16: return (Object value,ByteBuffer outBB)-> outBB.putInt((Short)value);
            case Int32: return (Object value,ByteBuffer outBB)-> outBB.putInt((Integer)value);
            case FloatingPoint : return (Object value,ByteBuffer outBB)-> outBB.putDouble((Double)value);
            case Single : return (Object value,ByteBuffer outBB)-> outBB.putFloat((Float)value);
            case UTF8String : return (Object value,ByteBuffer outBB)-> {
                try {
                    byte[] strByteBuf = ((String)value).getBytes("UTF-8");
                    outBB.putInt(strByteBuf.length);
                    outBB.put(strByteBuf);
                }catch(Exception e){
                    e.printStackTrace();
                }
            };
            case JavaScriptCode : return (Object value,ByteBuffer outBB)-> {
                try {
                    byte[] strByteBuf = ((String)value).getBytes("UTF-8");
                    outBB.putInt(strByteBuf.length);
                    outBB.put(strByteBuf);
                }catch(Exception e){
                    e.printStackTrace();
                }
            };
            case BinaryData : return (Object value,ByteBuffer outBB)-> {
                outBB.putInt(((byte[])value).length);
                outBB.put((byte[])value);
            };
            case Decimal : return (Object value,ByteBuffer outBB)-> {
                BigDecimal v = (BigDecimal)value;
                int signPart = v.signum();
                v = v.abs();
                long longPart = v.longValue();
                int mantissaPart = v.movePointRight(4).remainder(new BigDecimal("10000")).intValue();
                outBB.putLong(longPart);
                outBB.putInt(mantissaPart);
                outBB.putInt(signPart);
            };
            case Int64 : return (Object value,ByteBuffer outBB)-> outBB.putLong((Long)value);
            case Timestamp :return  (Object value,ByteBuffer outBB)-> outBB.putLong((Long)value);
            case UTCDatetime : return (Object value,ByteBuffer outBB)-> outBB.putLong(((Date)value).getTime());
            case ObjectId : return (Object value,ByteBuffer outBB)-> {
                UUID v = (UUID)value;
                outBB.putLong(v.getMostSignificantBits());
                outBB.putLong(v.getLeastSignificantBits());
            };
            case DBPointer :return (Object value,ByteBuffer outBB)-> {
                UUID v = (UUID)value;
                outBB.putLong(v.getMostSignificantBits());
                outBB.putLong(v.getLeastSignificantBits());
            };
            case NullValue : return (Object value,ByteBuffer outBB)-> outBB.putInt(0);
            case NullElement :return  (Object value,ByteBuffer outBB)-> outBB.putInt(0);
            default: return null;
        }
    }
}
