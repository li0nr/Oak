/*
 * Copyright 2018 Oath Inc.
 * Licensed under the terms of the Apache 2.0 license.
 * Please see LICENSE file in the project root for terms.
 */

package com.oath.oak;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This is a generic class for key/value detached buffers.
 * This class is used for when a detached access to the key/value is needed without synchronization:
 *  - KeyIterator
 *  - EntryIterator (for keys)
 *  - KeyStreamIterator
 *  - ValueStreamIterator
 *  - EntryStreamIterator (for both keys and values)
 * <p>
 * It should only be used without other concurrent writes in the background to this buffer.
 * <p>
 * A child class that require synchronization, needs to override the following method that is used
 * internally by this class.
 *  - <T> T safeAccessToAttachedBuffer(Function<OakAttachedReadBuffer, T> transformer)
 * See the methods documentation below for more information.
 */
class OakDetachedReadBuffer<B extends OakAttachedReadBuffer> implements OakDetachedBuffer, OakUnsafeDirectBuffer {

    final B buffer;

    OakDetachedReadBuffer(B buffer) {
        this.buffer = buffer;
    }

    @Override
    public int capacity() {
        return buffer.capacity();
    }

    @Override
    public ByteOrder order() {
        return buffer.order();
    }

    @Override
    public byte get(int index) {
        return safeAccessToAttachedBuffer(OakAttachedReadBuffer::get, index);
    }

    @Override
    public char getChar(int index) {
        return safeAccessToAttachedBuffer(OakAttachedReadBuffer::getChar, index);
    }

    @Override
    public short getShort(int index) {
        return safeAccessToAttachedBuffer(OakAttachedReadBuffer::getShort, index);
    }

    @Override
    public int getInt(int index) {
        return safeAccessToAttachedBuffer(OakAttachedReadBuffer::getInt, index);
    }

    @Override
    public long getLong(int index) {
        return safeAccessToAttachedBuffer(OakAttachedReadBuffer::getLong, index);
    }

    @Override
    public float getFloat(int index) {
        return safeAccessToAttachedBuffer(OakAttachedReadBuffer::getFloat, index);
    }

    @Override
    public double getDouble(int index) {
        return safeAccessToAttachedBuffer(OakAttachedReadBuffer::getDouble, index);
    }

    /**
     * Returns a transformation of ByteBuffer content.
     * If the child class needs synchronization before accessing the data, it should implement the synchronization
     * in this method, surrounding the call for the transformer function.
     *
     * @param transformer the function that executes the transformation
     * @return a transformation of the ByteBuffer content
     * @throws NullPointerException if the transformer is null
     */
    public <T> T transform(OakTransformer<T> transformer) {
        if (transformer == null) {
            throw new NullPointerException();
        }
        return transformer.apply(buffer);
    }

    @FunctionalInterface
    public interface Getter<R> {
        R get(OakAttachedReadBuffer buffer, int index);
    }

    /**
     * Apply a get operation on the inner attached buffer in a safe manner (atomically if needed).
     * It is used internally by this class for when we use the internal buffer to read the data.
     * If the child class needs synchronization before accessing the data, it should implement the synchronization
     * in this method, surrounding the call for the getter function.
     */
    protected <R> R safeAccessToAttachedBuffer(Getter<R> getter, int index) {
        // Internal call. No input validation.
        return getter.get(buffer, index);
    }

    /*-------------- OakUnsafeDirectBuffer --------------*/

    @Override
    public ByteBuffer getByteBuffer() {
        return buffer.getByteBuffer();
    }

    @Override
    public int getOffset() {
        return buffer.getOffset();
    }

    @Override
    public int getLength() {
        return buffer.getLength();
    }

    @Override
    public long getAddress() {
        return buffer.getAddress();
    }
}
