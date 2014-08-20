package com.cffex.nogc.cson.core.enumeration;

/**
 * Created by sunke on 2014/7/23.
 */
public enum EntityType {
    Data(1),View(2),ComplexType(3);
    private int value;

    public int getValue() {
        return value;
    }
    EntityType(int value){
        this.value = value;
    }
}
