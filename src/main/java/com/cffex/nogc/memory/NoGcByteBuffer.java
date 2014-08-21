/**
 * @time 2014年8月21日
 * @author sunke
 */
package com.cffex.nogc.memory;

import java.lang.reflect.Field;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.InvalidMarkException;

import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

/**
 * @author sunke
 * @ClassName NoGcByteBuffer
 * @Description: NOGC进行数据操作时的buffer类，不拷贝真实数据，只是一个空的操作类
 */
public class NoGcByteBuffer{
	/**
	 * 标签
	 */
    private int mark = -1;
    /**
     * 当前位置--->相对于startPosition的相对位置
     */
    private int position = 0;
    /**
     * limit长度限制
     */
    private int limit;
    /**
     * 在directBuffer中可以使用的长度容量
     */
    private int capacity;
    /**
     * 相对于directBuffer的起始位置
     */
    private int startPosition;
    
    /**
     * 真实数据的引用-->DirectBuffer
     */
    private DirectBuffer buffer;
    
    /**
     * 数据操作工具
     */
    private final  Unsafe UNSAFE;
	private long BYTES_OFFSET;
	

	public NoGcByteBuffer(int startPosition, int cap,DirectBuffer buffer,int lim) {
		if (cap < 0)
            throw new IllegalArgumentException("Negative capacity: " + cap);
        this.capacity = cap;
        /**
         * 初始时，没有数据，每次更新数据结束后，设定limit
         */
        limit(lim);
        position(0);
        mark = -1;
        this.startPosition = startPosition;
        this.buffer = buffer;
        try {
			@SuppressWarnings("all")
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			UNSAFE = (Unsafe) theUnsafe.get(null);
			BYTES_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}
	
	public NoGcByteBuffer(int startPosition, int cap,DirectBuffer buffer){
		this(startPosition, cap, buffer, cap);
	}

	/**
     * Returns this buffer's capacity.
     *
     * @return  The capacity of this buffer
     */
    public final int capacity() {
        return capacity;
    }

    /**
     * Returns this buffer's position.
     *
     * @return  The position of this buffer
     */
    public final int position() {
        return position;
    }
    
    /**
     * Sets this buffer's position.  If the mark is defined and larger than the
     * new position then it is discarded.
     *
     * @param  newPosition
     *         The new position value; must be non-negative
     *         and no larger than the current limit
     *
     * @return  This buffer
     *
     * @throws  IllegalArgumentException
     *          If the preconditions on <tt>newPosition</tt> do not hold
     */
    public final NoGcByteBuffer position(int newPosition) {
        if ((newPosition > limit) || (newPosition < 0))
            throw new IllegalArgumentException();
        position = newPosition;
        if (mark > position) mark = -1;
        return this;
    }
    
    /**
     * Returns this buffer's limit.
     *
     * @return  The limit of this buffer
     */
    public final int limit() {
        return limit;
    }

    /**
     * Sets this buffer's limit.  If the position is larger than the new limit
     * then it is set to the new limit.  If the mark is defined and larger than
     * the new limit then it is discarded.
     *
     * @param  newLimit
     *         The new limit value; must be non-negative
     *         and no larger than this buffer's capacity
     *
     * @return  This buffer
     *
     * @throws  IllegalArgumentException
     *          If the preconditions on <tt>newLimit</tt> do not hold
     */
    public final NoGcByteBuffer limit(int newLimit) {
        if ((newLimit > capacity) || (newLimit < 0))
            throw new IllegalArgumentException();
        limit = newLimit;
        if (position > limit) position = limit;
        if (mark > limit) mark = -1;
        return this;
    }
    /**
     * Sets this buffer's mark at its position.
     *
     * @return  This buffer
     */
    public final NoGcByteBuffer mark() {
        mark = position;
        return this;
    }
    
    /**
     * Clears this buffer.  The position is set to zero, the limit is set to
     * the capacity, and the mark is discarded.
     *
     * <p> Invoke this method before using a sequence of channel-read or
     * <i>put</i> operations to fill this buffer.  For example:
     *
     * <blockquote><pre>
     * buf.clear();     // Prepare buffer for reading
     * in.read(buf);    // Read data</pre></blockquote>
     *
     * <p> This method does not actually erase the data in the buffer, but it
     * is named as if it did because it will most often be used in situations
     * in which that might as well be the case. </p>
     *
     * @return  This buffer
     */
    public final NoGcByteBuffer clear() {
        position = 0;
        limit = capacity;
        mark = -1;
        return this;
    }
    
    /**
     * Flips this buffer.  The limit is set to the current position and then
     * the position is set to zero.  If the mark is defined then it is
     * discarded.
     *
     * <p> After a sequence of channel-read or <i>put</i> operations, invoke
     * this method to prepare for a sequence of channel-write or relative
     * <i>get</i> operations.  For example:
     *
     * <blockquote><pre>
     * buf.put(magic);    // Prepend header
     * in.read(buf);      // Read data into rest of buffer
     * buf.flip();        // Flip buffer
     * out.write(buf);    // Write header + data to channel</pre></blockquote>
     *
     * <p> This method is often used in conjunction with the {@link
     * java.nio.ByteBuffer#compact compact} method when transferring data from
     * one place to another.  </p>
     *
     * @return  This buffer
     */
    public final NoGcByteBuffer flip() {
        limit = position;
        position = 0;
        mark = -1;
        return this;
    }
    
    /**
     * Rewinds this buffer.  The position is set to zero and the mark is
     * discarded.
     *
     * <p> Invoke this method before a sequence of channel-write or <i>get</i>
     * operations, assuming that the limit has already been set
     * appropriately.  For example:
     *
     * <blockquote><pre>
     * out.write(buf);    // Write remaining data
     * buf.rewind();      // Rewind buffer
     * buf.get(array);    // Copy data into array</pre></blockquote>
     *
     * @return  This buffer
     */
    public final NoGcByteBuffer rewind() {
        position = 0;
        mark = -1;
        return this;
    }

    /**
     * Returns the number of elements between the current position and the
     * limit.
     *
     * @return  The number of elements remaining in this buffer
     */
    public final int remaining() {
        return limit - position;
    }

    /**
     * Tells whether there are any elements between the current position and
     * the limit.
     *
     * @return  <tt>true</tt> if, and only if, there is at least one element
     *          remaining in this buffer
     */
    public final boolean hasRemaining() {
        return position < limit;
    }
    /**
     * Resets this buffer's position to the previously-marked position.
     *
     * <p> Invoking this method neither changes nor discards the mark's
     * value. </p>
     *
     * @return  This buffer
     *
     * @throws  InvalidMarkException
     *          If the mark has not been set
     */
    public final NoGcByteBuffer reset() {
        int m = mark;
        if (m < 0)
            throw new InvalidMarkException();
        position = m;
        return this;
    }
	

	public NoGcByteBuffer duplicate() {
		return new NoGcByteBuffer(startPosition, capacity, buffer, limit);
	}

	/**
     * Checks the current position against the limit, throwing a {@link
     * BufferUnderflowException} if it is not smaller than the limit, and then
     * increments the position.
     *
     * @return  The current position value, before it is incremented
     */
    final int nextGetIndex() {                          // package-private
        if (position >= limit)
            throw new BufferUnderflowException();
        return position++;
    }
	final int nextGetIndex(int nb) {                    // package-private
        if (limit - position < nb)
            throw new BufferUnderflowException();
        int p = position;
        position += nb;
        return p;
    }

    /**
     * Checks the current position against the limit, throwing a {@link
     * BufferOverflowException} if it is not smaller than the limit, and then
     * increments the position.
     *
     * @return  The current position value, before it is incremented
     */
    final int nextPutIndex() {                          // package-private
        if (position >= limit)
            throw new BufferOverflowException();
        return position++;
    }
    final int nextPutIndex(int nb) {                    // package-private
        if (limit - position < nb)
            throw new BufferOverflowException();
        int p = position;
        position += nb;
        return p;
    }
	final int checkIndex(int i) {                       
        if ((i < 0) || (i >= limit))
            throw new IndexOutOfBoundsException();
        return i;
    }
	final int checkIndex(int i, int nb) {               // package-private
        if ((i < 0) || (nb > limit - i))
            throw new IndexOutOfBoundsException();
        return i;
    }
	public byte get(int index) {
		long thisPosition = buffer.address()+startPosition+checkIndex(index);
		return this.UNSAFE.getByte(thisPosition);
	}


	public NoGcByteBuffer put(int index, byte b) {
		// TODO Auto-generated method stub
		long thisPosition = buffer.address()+startPosition+checkIndex(index);
		this.UNSAFE.putByte(thisPosition, b);
		return this;
	}
	public byte get() {
		long thisPosition = buffer.address()+startPosition+nextGetIndex();
		return this.UNSAFE.getByte(thisPosition);
	}


	public NoGcByteBuffer put(byte b) {
		long thisPosition = buffer.address()+startPosition+nextPutIndex();
		this.UNSAFE.putByte(thisPosition, b);
		return this;
	}
	public int getInt() {
		long thisPosition = buffer.address()+startPosition+nextGetIndex(4);
		return this.UNSAFE.getInt(thisPosition);
	}


	public NoGcByteBuffer putInt(int value) {
		// TODO Auto-generated method stub
		long thisPosition = buffer.address()+startPosition+nextPutIndex(4);
		this.UNSAFE.putInt(thisPosition, value);
		return this;
	}


	public int getInt(int index) {
		long thisPositon = buffer.address()+startPosition+checkIndex(index);
		return this.UNSAFE.getInt(thisPositon);
	}


	
	public NoGcByteBuffer putInt(int index, int value) {
		long thisPosition = buffer.address()+startPosition+checkIndex(index);
		this.UNSAFE.putInt(thisPosition, value);
		return this;
	}




	public long getLong() {
		long thisPosition = buffer.address()+startPosition+nextGetIndex(8);
		return this.UNSAFE.getLong(thisPosition);
	}


	public NoGcByteBuffer putLong(long value) {
		long thisPosition = buffer.address() + startPosition +nextPutIndex(8);
		this.UNSAFE.putLong(thisPosition, value);
		return this;
	}


	public long getLong(int index) {
		long thisPosition = buffer.address()+startPosition+checkIndex(index);
		return this.UNSAFE.getLong(thisPosition);
	}


	public NoGcByteBuffer putLong(int index, long value) {
		long thisPosition = buffer.address() + startPosition +checkIndex(index);
		this.UNSAFE.putLong(thisPosition, value);
		return this;
	}
	
	public byte[] getBytes(int length) {
		byte[] b = new byte[length];
  		for(int i=0;i<length;i++){  
  			b[i]=get();
  		}
		return b;
	}


	public NoGcByteBuffer putBytes(byte[] b) {
		if (b == null ) {
			throw new NullPointerException("Write byte array into buffer with null value.");
		}
		long thisPosition = buffer.address() + startPosition +nextPutIndex(b.length);
		this.UNSAFE.copyMemory(b, BYTES_OFFSET, null, thisPosition, b.length);
		return this;
	}


	public byte[] getBytes(int index,int length) {
		byte[] b = new byte[length];
  		for(int i=0;i<length;i++){  
  			b[i]=get(index+i);
  		}
		return b;
	}


	public NoGcByteBuffer putBytes(int index, byte[] b) {
		if (b == null ) {
			throw new NullPointerException("Write byte array into buffer with null value.");
		}
		long thisPosition = buffer.address() + startPosition +checkIndex(index);
		this.UNSAFE.copyMemory(b, BYTES_OFFSET, null, thisPosition, b.length);
		return this;
	}
	
	public void copyBytes(int fromIndex, int length, Integer toIndex) {
		// TODO Auto-generated method stub
		byte[] b = getBytes(fromIndex, length);
		long thisPosition = buffer.address() + startPosition +checkIndex(toIndex);
		this.UNSAFE.copyMemory(b, BYTES_OFFSET, null, thisPosition, b.length);
	}

}
