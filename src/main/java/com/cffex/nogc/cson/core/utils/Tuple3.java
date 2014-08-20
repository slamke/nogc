package com.cffex.nogc.cson.core.utils;

/**
 * Created by sunke on 2014/8/6.
 */
public class Tuple3<A,B,C> {
    public final A first;
    public final B second;
    public final C third;
    public Tuple3(A a,B b,C c){
        first=a;
        second=b;
        third =c;
    }
}
