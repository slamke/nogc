package com.cffex.nogc.cson.core;

import static com.cffex.nogc.cson.core.utils.CSONStructure.*;
/**
 * Created by sunke on 2014/7/23.
 * The CSON reader cursor class for reading element and move to next element in a CSON data
 */
public class ReadCursor {
    private CSONReader reader;
    private int lenOffset;
    private int numOffset;
    private int indexArrayOffset;
    private int dataOffset;

    public int getDataOffset() {
        return dataOffset;
    }

    public ReadCursor(int startPos, CSONReader reader){
        this.reader = reader;
        this.lenOffset = startPos;
        this.numOffset = startPos+LenthFieldSize;
        this.indexArrayOffset = numOffset+NumFieldSize;
        this.dataOffset = indexArrayOffset +(reader.readCount(this)*IndexSegSize);
    }

    public int getIndexTypeOffset(int index){
        return indexArrayOffset+index*IndexSegSize;
    }

    public int getIndexValueOffset(int index){
        return  indexArrayOffset+(index*IndexSegSize)+TypeCodeSize;
    }

    public int getDataValueOffset(int elementOffset){
        return dataOffset+elementOffset;
    }


    public int getLenOffset() {
        return lenOffset;
    }

    public int getNumOffset() {
        return numOffset;
    }

    public CSONReader getReader() {
        return reader;
    }
}
