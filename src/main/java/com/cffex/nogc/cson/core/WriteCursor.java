package com.cffex.nogc.cson.core;

import static com.cffex.nogc.cson.core.utils.CSONStructure.*;
/**
 * Created by sunke on 2014/7/22.
 * The COSN writer cursor.
 */
public class WriteCursor {

    private CSONWriter writer;
    private int LenOffset;
    private int NumOffset;
    private int IndexArrayOffset;
    private int elementCount;
    private int DataOffset;
    private int lastDataPos;

    public WriteCursor(CSONWriter writer,int StartPos, int count){
        this.writer = writer;
        this.LenOffset = StartPos;
        this.NumOffset = StartPos + LenthFieldSize;
        this.IndexArrayOffset = NumOffset + NumFieldSize;
        this.elementCount = count;
        this.DataOffset = IndexArrayOffset + (elementCount * IndexSegSize);
        this.lastDataPos = DataOffset;
    }

    public CSONWriter getWriter() {
        return writer;
    }

    public int GetIndexTypeOffset(int Id ){
       return IndexArrayOffset + (Id * IndexSegSize);
    }
    public int GetIndexValueOffset(int Id){
      return IndexArrayOffset + (Id * IndexSegSize) + 1;
    }
    /**
     * Get the current available position in data area
     */
    public int GetDataValueOffset(){
        return lastDataPos;
    }
    /**
     * Track the last writable position in this cursor's data area
     */
    public void UpdateDataValueOffset(int lastPos){
        lastDataPos = lastPos;
    }
    /**
     * Get the relative offset for current large size element(Used to save in the index area)
     */
    public int DataAreaLength(){
      return lastDataPos-DataOffset;
    }

    public int getNumOffset() {
        return NumOffset;
    }

    public int getLenOffset() {
        return LenOffset;
    }
}
