package com.cffex.nogc.cson.core.utils;

/**
 * Created by sunke on 2014/7/22.
 * static field length of cson format
 */
public class CSONStructure {
    private CSONStructure(){
    }

    /**
     * index区一个field所占的长度byte
     */
    public static final int IndexSegSize = 5;
    /**
     * len区所占的长度
     */
    public static final int LenthFieldSize = 4;
    /**
     * num区所占的长度
     */
    public static final int NumFieldSize = 4;
    /**
     * typecode所占的长度
     */
    public static final int TypeCodeSize = 1;

}
