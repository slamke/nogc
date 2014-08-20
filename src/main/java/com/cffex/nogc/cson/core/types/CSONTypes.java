package com.cffex.nogc.cson.core.types;

/**
 * Created by sunke on 2014/7/22.
 * CSON所有支持的数据类型的枚举类型定义
 */
public enum  CSONTypes {
        BSONEOF(0),
        FloatingPoint(1),
        UTF8String(2),
        EmbeddedDocument(3),
        Array(4),
        BinaryData(5),
        Undefined(6),
        ObjectId (7),
        Boolean(8),
        UTCDatetime(9),
        NullValue(10),
        RegularExpression(11),
        DBPointer(12),
        JavaScriptCode(0x0D),
        Symbol(0x0E),
        JavaScriptCodeWScope(0x0F),
        Int32(0x10),
        Timestamp(0x11),
        Int64(0x12),
        /*    MinKey(0xFF)
          MaxKey(0x7F) */
        Decimal(0x13),
        NullElement(0x14),
        Int8(0x15),
        Int16(0x16),
        Single(0x17);

        private final byte value;
        public byte getValue() {
            return value;
        }
        //构造器默认也只能是private, 从而保证构造函数只能在内部使用
        private CSONTypes(int value) {
            this.value = (byte)value;
        }

        public static  CSONTypes getByByteCode(byte value){
            switch(value){
                case 0:
                    return BSONEOF;
                case 1:
                    return FloatingPoint;
                case 2:
                    return UTF8String;
                case 3:
                    return EmbeddedDocument;
                case 4:
                    return Array;
                case 5:
                    return BinaryData;
                case 6:
                    return Undefined;
                case 7:
                    return ObjectId;
                case 8:
                    return Boolean;
                case 9:
                    return UTCDatetime;
                case 10:
                    return NullValue;
                case 11:
                    return RegularExpression;
                case 12:
                    return DBPointer;
                case 0x0D:
                    return JavaScriptCode;
                case 0x0E:
                    return Symbol;
                case 0x0F:
                    return JavaScriptCodeWScope;
                case 0x10:
                    return Int32;
                case 0x11:
                    return Timestamp;
                case 0x12:
                    return Int64;
                /*    MinKey(0xFF)
                 MaxKey(0x7F) */
                case 0x13:
                    return  Decimal;
                case 0x14:
                    return NullElement;
                case 0x15:
                    return Int8;
                case 0x16:
                    return  Int16;
                case 0x17:
                    return Single;
            }
            return Decimal;
        }
}
