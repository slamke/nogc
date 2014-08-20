package com.cffex.nogc.cson.core.utils;

/**
 * Created by sunke on 2014/8/6.
 * 泛型二元组
 */
public class Tuple<A,B>{
    public final A first;
    public final B second;
    public Tuple(A a,B b){
        first=a;
        second=b;
    }
}
