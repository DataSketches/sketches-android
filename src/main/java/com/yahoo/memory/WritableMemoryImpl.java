/*
 * Copyright 2017, Yahoo! Inc. Licensed under the terms of the
 * Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.memory;

import static com.yahoo.memory.Util.BOOLEAN_SHIFT;
import static com.yahoo.memory.Util.BYTE_SHIFT;
import static com.yahoo.memory.Util.CHAR_SHIFT;
import static com.yahoo.memory.Util.DOUBLE_SHIFT;
import static com.yahoo.memory.Util.FLOAT_SHIFT;
import static com.yahoo.memory.Util.INT_SHIFT;
import static com.yahoo.memory.Util.LONG_SHIFT;
import static com.yahoo.memory.Util.SHORT_SHIFT;
import static com.yahoo.memory.Util.assertBounds;
import static com.yahoo.memory.Util.LS;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Implementation of WritableMemory, using ByteBuffer for all operations
 * @author Jon Malkin
 * @author Roman Leventov
 * @author Lee Rhodes
 */
class WritableMemoryImpl extends WritableMemory {
  final private int capacity;
  final private int offset; // Holds the cumulative offset to the start of data.
  // Static variable for cases where byteBuf/array sizes are zero
  final static WritableMemoryImpl ZERO_SIZE_MEMORY;

  final private ByteBuffer byteBuf; // buffer holding the backing data

  static {
    ZERO_SIZE_MEMORY = new WritableMemoryImpl(ByteBuffer.wrap(new byte[0]), ByteOrder.nativeOrder());
  }

  WritableMemoryImpl(final ByteBuffer bb, final ByteOrder byteOrder) {
    assert bb != null;
    this.byteBuf = bb;
    capacity = bb.capacity();
    offset = 0;
    byteBuf.order(byteOrder);
  }

  private WritableMemoryImpl(final ByteBuffer bb, final int offsetBytes, final int capacityBytes,
                             final ByteOrder byteOrder) {
    assert bb != null;
    this.byteBuf = bb;
    capacity = capacityBytes;
    offset = offsetBytes;
    byteBuf.order(byteOrder);
  }

  //REGIONS/DUPLICATES XXX
  @Override
  public WritableMemory writableDuplicate() {
    return writableRegion(0, capacity);
  }

  @Override
  public Memory region(final long offsetBytes, final long capacityBytes) {
    // just a wrapper around writableRegion() to return non-writable Memory
    return writableRegion(offsetBytes, capacityBytes);
  }

  @Override
  public WritableMemory writableRegion(final long offsetBytes, final long capacityBytes) {
    Util.checkBounds(offsetBytes, capacityBytes, capacity);
    return new WritableMemoryImpl(byteBuf, offset + (int) offsetBytes, (int) capacityBytes, byteBuf.order());
  }

  ///PRIMITIVE getXXX() and getXXXArray() XXX
  @Override
  public boolean getBoolean(final long offsetBytes) {
    assertBounds(offsetBytes, Byte.BYTES, capacity);
    return byteBuf.get(offset + (int) offsetBytes) != 0;
  }

  @Override
  public void getBooleanArray(final long offsetBytes, final boolean[] dstArray, final int dstOffset,
                              final int lengthBooleans) {
    final long copyBytes = lengthBooleans << BOOLEAN_SHIFT;
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    Util.checkBounds(dstOffset, lengthBooleans, dstArray.length);
    int srcIdx = (int) (offset + offsetBytes);
    for (int i = dstOffset; i < dstOffset + lengthBooleans; ++i, ++srcIdx) {
      dstArray[i] = byteBuf.get(srcIdx) != 0;
    }
  }

  @Override
  public byte getByte(final long offsetBytes) {
    assertBounds(offsetBytes, Byte.BYTES, capacity);
    return byteBuf.get(offset + (int) offsetBytes);
  }

  @Override
  public void getByteArray(final long offsetBytes, final byte[] dstArray, final int dstOffset,
                           final int lengthBytes) {
    final int copyBytes = lengthBytes << BYTE_SHIFT;
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    Util.checkBounds(dstOffset, lengthBytes, dstArray.length);
    if (byteBuf.hasArray()) {
      final byte[] srcArray = byteBuf.array();
      final int srcOffset = offset + (int) offsetBytes;
      System.arraycopy(srcArray, srcOffset, dstArray, dstOffset, copyBytes);
    } else {
      int srcIdx = (int) (offset + offsetBytes);
      for (int i = dstOffset; i < dstOffset + lengthBytes; ++i, ++srcIdx) {
        dstArray[i] = byteBuf.get(srcIdx);
      }
    }
  }

  @Override
  public char getChar(final long offsetBytes) {
    assertBounds(offsetBytes, Character.BYTES, capacity);
    return byteBuf.getChar(offset + (int) offsetBytes);
  }

  @Override
  public void getCharArray(final long offsetBytes, final char[] dstArray, final int dstOffset,
                           final int lengthChars) {
    final long copyBytes = lengthChars << CHAR_SHIFT;
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    Util.checkBounds(dstOffset, lengthChars, dstArray.length);
    int srcIdx = offset + (int) offsetBytes;
    for (int i = dstOffset; i < dstOffset + lengthChars; ++i, srcIdx += Character.BYTES) {
      dstArray[i] = byteBuf.getChar(srcIdx);
    }
  }

  @Override
  public double getDouble(final long offsetBytes) {
    assertBounds(offsetBytes, Double.BYTES, capacity);
    return byteBuf.getDouble(offset + (int) offsetBytes);
  }

  @Override
  public void getDoubleArray(final long offsetBytes, final double[] dstArray, final int dstOffset,
                             final int lengthDoubles) {
    final long copyBytes = lengthDoubles << DOUBLE_SHIFT;
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    Util.checkBounds(dstOffset, lengthDoubles, dstArray.length);
    int srcIdx = offset + (int) offsetBytes;

    for (int i = dstOffset; i < dstOffset + lengthDoubles; ++i, srcIdx += Double.BYTES) {
      dstArray[i] = byteBuf.getDouble(srcIdx);
    }
  }

  @Override
  public float getFloat(final long offsetBytes) {
    assertBounds(offsetBytes, Float.BYTES, capacity);
    return byteBuf.getFloat(offset + (int) offsetBytes);
  }

  @Override
  public void getFloatArray(final long offsetBytes, final float[] dstArray, final int dstOffset,
                            final int lengthFloats) {
    final long copyBytes = lengthFloats << FLOAT_SHIFT;
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    Util.checkBounds(dstOffset, lengthFloats, dstArray.length);
    int srcIdx = offset + (int) offsetBytes;
    for (int i = dstOffset; i < dstOffset + lengthFloats; ++i, srcIdx += Float.BYTES) {
      dstArray[i] = byteBuf.getFloat(srcIdx);
    }
  }

  @Override
  public int getInt(final long offsetBytes) {
    assertBounds(offsetBytes, Integer.BYTES, capacity);
    return byteBuf.getInt(offset + (int) offsetBytes);
  }

  @Override
  public void getIntArray(final long offsetBytes, final int[] dstArray, final int dstOffset,
                          final int lengthInts) {
    final long copyBytes = lengthInts << INT_SHIFT;
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    Util.checkBounds(dstOffset, lengthInts, dstArray.length);
    int srcIdx = offset + (int) offsetBytes;
    for (int i = dstOffset; i < dstOffset + lengthInts; ++i, srcIdx += Integer.BYTES) {
      dstArray[i] = byteBuf.getInt(srcIdx);
    }
  }

  @Override
  public long getLong(final long offsetBytes) {
    assertBounds(offsetBytes, Long.BYTES, capacity);
    return byteBuf.getLong(offset + (int) offsetBytes);
  }

  @Override
  public void getLongArray(final long offsetBytes, final long[] dstArray, final int dstOffset,
                           final int lengthLongs) {
    final long copyBytes = lengthLongs << LONG_SHIFT;
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    Util.checkBounds(dstOffset, lengthLongs, dstArray.length);
    int srcIdx = offset + (int) offsetBytes;
    for (int i = dstOffset; i < dstOffset + lengthLongs; ++i, srcIdx += Long.BYTES) {
      dstArray[i] = byteBuf.getLong(srcIdx);
    }
  }

  @Override
  public short getShort(final long offsetBytes) {
    assertBounds(offsetBytes, Short.BYTES, capacity);
    return byteBuf.getShort(offset + (int) offsetBytes);
  }

  @Override
  public void getShortArray(final long offsetBytes, final short[] dstArray, final int dstOffset,
                            final int lengthShorts) {
    final long copyBytes = lengthShorts << SHORT_SHIFT;
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    Util.checkBounds(dstOffset, lengthShorts, dstArray.length);
    int srcIdx = offset + (int) offsetBytes;
    for (int i = dstOffset; i < dstOffset + lengthShorts; ++i, srcIdx += Short.BYTES) {
      dstArray[i] = byteBuf.getShort(srcIdx);
    }
  }

  //OTHER PRIMITIVE READ METHODS: copyTo, compareTo XXX
  @Override
  public int compareTo(final long thisOffsetBytes, final long thisLengthBytes, final Memory that,
                       final long thatOffsetBytes, final long thatLengthBytes) {
    Util.checkBounds(thisOffsetBytes, thisLengthBytes, capacity);
    Util.checkBounds(thatOffsetBytes, thatLengthBytes, that.getCapacity());

    if (thisLengthBytes < thatLengthBytes) {
      return -1;
    }
    if (thisLengthBytes > thatLengthBytes) {
      return 1;
    }

    int thisIdx = (int) getRegionOffset(thisOffsetBytes);
    WritableMemoryImpl thatMemObj = (WritableMemoryImpl) that;
    int thatIdx = (int) thatMemObj.getRegionOffset(thatOffsetBytes);

    int endIdx = thisIdx + (int) thisLengthBytes; // thisLengthBytes == thatLengthBytes so either works for end check
    for (; thisIdx < endIdx; ++thisIdx, ++thatIdx) {
      final int thisByte = byteBuf.get(thisIdx);
      final int thatByte = thatMemObj.byteBuf.get(thatIdx);
      if (thisByte < thatByte) {
        return -1;
      }
      if (thisByte > thatByte) {
        return 1;
      }
    }

    return 0;
  }

  @Override
  public void copyTo(final long srcOffsetBytes, final WritableMemory destination,
                     final long dstOffsetBytes, final long lengthBytes) {
    Util.checkBounds(srcOffsetBytes, lengthBytes, capacity);
    Util.checkBounds(dstOffsetBytes, lengthBytes, destination.getCapacity());

    if (isSameResource(destination)) {
      if (srcOffsetBytes == dstOffsetBytes) {
        return;
      }
    }

    final int length = (int) lengthBytes;
    final byte[] srcData = new byte[length];
    // TODO: there's got to be an efficient way to do this without an intermediate array
    getByteArray(srcOffsetBytes, srcData, 0, length); // uses System.arraycopy() if possible
    destination.putByteArray(dstOffsetBytes, srcData, 0, length);
  }

  //OTHER READ METHODS XXX
  @Override
  public long getCapacity() {
    return capacity;
  }

  @Override
  public void checkBounds(final long offsetBytes, final long length) {
    Util.checkBounds(offsetBytes, length, capacity);
  }

  @Override
  public long getRegionOffset(final long offsetBytes) {
    return offset + offsetBytes;
  }

  @Override
  public ByteOrder getResourceOrder() {
    return byteBuf.order();
  }

  @Override
  public boolean hasArray() {
    return byteBuf.hasArray();
  }

  @Override
  public boolean hasByteBuffer() {
    return true;
  }

  @Override
  public boolean isDirect() {
    return byteBuf.isDirect();
  }

  @Override
  public boolean isResourceReadOnly() {
    return byteBuf.isReadOnly();
  }

  /* A best effort, without knowing the underlying implementation of DirectByteBuffer on Android */
  @Override
  public boolean isSameResource(final Memory that) {
    // unclear how to compare if direct, so return false
    if ((that == null) || isDirect() || that.isDirect()) {
      return false;
    }

    final WritableMemoryImpl thatRef = (WritableMemoryImpl) that;

    if (hasArray() && that.hasArray()) {
      if ((byteBuf == thatRef.byteBuf) || (byteBuf.array() == thatRef.byteBuf.array())) {
        return (capacity == thatRef.capacity) && (offset == thatRef.offset);
      }
    }

    return false;
  }

  @Override
  public boolean swapBytes() {
    return byteBuf.order() != ByteOrder.nativeOrder();
  }

  @Override
  public String toHexString(final String header, final long offsetBytes, final int lengthBytes) {
    final String klass = this.getClass().getSimpleName();
    final String s1 = String.format("(..., %d, %d)", offsetBytes, lengthBytes);
    final long hcode = hashCode() & 0XFFFFFFFFL;
    final String call = ".toHexString" + s1 + ", hashCode: " + hcode;
    final StringBuilder sb = new StringBuilder();
    sb.append("### ").append(klass).append(" SUMMARY ###").append(LS);
    sb.append("Header Comment      : ").append(header).append(LS);
    sb.append("Call Params         : ").append(call);
    return toHex(sb.toString(), offsetBytes, lengthBytes, this);
  }

  static String toHex(final String preamble, final long offsetBytes, final int lengthBytes,
                      final Memory mem) {
    Util.checkBounds(offsetBytes, lengthBytes, mem.getCapacity());
    final WritableMemoryImpl memObj = (WritableMemoryImpl) mem;
    final StringBuilder sb = new StringBuilder();
    final String bbStr = (memObj.byteBuf == null) ? "null"
            : memObj.byteBuf.getClass().getSimpleName() + ", " + (memObj.byteBuf.hashCode() & 0XFFFFFFFFL);
    final MemoryRequestServer memReqSvr = memObj.getMemoryRequestServer();
    final String memReqStr = (memReqSvr == null) ? "null"
            : memReqSvr.getClass().getSimpleName() + ", " + (memReqSvr.hashCode() & 0XFFFFFFFFL);
    sb.append(preamble).append(LS);
    sb.append("ByteBuf, hashCode   : ").append(bbStr).append(LS);
    sb.append("RegionOffset        : ").append(memObj.getRegionOffset(0)).append(LS);
    sb.append("Capacity            : ").append(mem.getCapacity()).append(LS);
    sb.append("MemReq, hashCode    : ").append(memReqStr).append(LS);
    sb.append("Resource Read Only  : ").append(mem.isResourceReadOnly()).append(LS);
    sb.append("Resource Endianness : ").append(memObj.byteBuf.order().toString()).append(LS);
    //Data detail
    sb.append("Data, littleEndian  :  0  1  2  3  4  5  6  7");

    for (long i = 0; i < lengthBytes; i++) {
      final int b = mem.getByte(offsetBytes + i) & 0XFF;
      if ((i % 8) == 0) { //row header
        sb.append(String.format("%n%20s: ", offsetBytes + i));
      }
      sb.append(String.format("%02x ", b));
    }
    sb.append(LS);

    return sb.toString();
  }


  //PRIMITIVE putXXX() and putXXXArray() implementations XXX
  @Override
  public void putBoolean(final long offsetBytes, final boolean value) {
    assertBounds(offsetBytes, Byte.BYTES, capacity);
    byteBuf.put(offset + (int) offsetBytes, (byte) (value ? 0x1 : 0x0));
  }

  @Override
  public void putBooleanArray(final long offsetBytes, final boolean[] srcArray, final int srcOffset,
                              final int length) {
    final long copyBytes = length << BOOLEAN_SHIFT;
    Util.checkBounds(srcOffset, length, srcArray.length);
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    int dstIdx = offset + (int) offsetBytes;
    for (int i = srcOffset; i < srcOffset + length; ++i, ++dstIdx) {
      byteBuf.put(dstIdx, (byte) (srcArray[i] ? 0x1 : 0x0));
    }
  }

  @Override
  public void putByte(final long offsetBytes, final byte value) {
    assertBounds(offsetBytes, Byte.BYTES, capacity);
    byteBuf.put(offset + (int) offsetBytes, value);
  }

  @Override
  public void putByteArray(final long offsetBytes, final byte[] srcArray, final int srcOffset,
                           final int length) {
    final int copyBytes = length << BYTE_SHIFT;
    Util.checkBounds(srcOffset, length, srcArray.length);
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    if (byteBuf.hasArray()) {
      final byte[] dstArray = byteBuf.array();
      final int dstOffset = offset + (int) offsetBytes;
      System.arraycopy(srcArray, srcOffset, dstArray, dstOffset, copyBytes);
    } else {
      int dstIdx = offset + (int) offsetBytes;
      for (int i = srcOffset; i < srcOffset + length; ++i, ++dstIdx) {
        byteBuf.put(dstIdx, srcArray[i]);
      }
    }
  }

  @Override
  public void putChar(final long offsetBytes, final char value) {
    assertBounds(offsetBytes, Character.BYTES, capacity);
    byteBuf.putChar(offset + (int) offsetBytes, value);
  }

  @Override
  public void putCharArray(final long offsetBytes, final char[] srcArray, final int srcOffset,
                           final int length) {
    final long copyBytes = length << CHAR_SHIFT;
    Util.checkBounds(srcOffset, length, srcArray.length);
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    int dstIdx = offset + (int) offsetBytes;
    for (int i = srcOffset; i < srcOffset + length; ++i, dstIdx += Character.BYTES) {
      byteBuf.putChar(dstIdx, srcArray[i]);
    }
  }

  @Override
  public void putDouble(final long offsetBytes, final double value) {
    assertBounds(offsetBytes, Double.BYTES, capacity);
    byteBuf.putDouble(offset + (int) offsetBytes, value);
  }

  @Override
  public void putDoubleArray(final long offsetBytes, final double[] srcArray, final int srcOffset,
                             final int length) {
    final long copyBytes = length << DOUBLE_SHIFT;
    Util.checkBounds(srcOffset, length, srcArray.length);
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    int dstIdx = offset + (int) offsetBytes;
    for (int i = srcOffset; i < srcOffset + length; ++i, dstIdx += Double.BYTES) {
      byteBuf.putDouble(dstIdx, srcArray[i]);
    }
  }

  @Override
  public void putFloat(final long offsetBytes, final float value) {
    assertBounds(offsetBytes, Float.BYTES, capacity);
    byteBuf.putFloat(offset + (int) offsetBytes, value);
  }

  @Override
  public void putFloatArray(final long offsetBytes, final float[] srcArray, final int srcOffset,
                            final int length) {
    final long copyBytes = length << FLOAT_SHIFT;
    Util.checkBounds(srcOffset, length, srcArray.length);
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    int dstIdx = offset + (int) offsetBytes;
    for (int i = srcOffset; i < srcOffset + length; ++i, dstIdx += Float.BYTES) {
      byteBuf.putFloat(dstIdx, srcArray[i]);
    }
  }

  @Override
  public void putInt(final long offsetBytes, final int value) {
    assertBounds(offsetBytes, Integer.BYTES, capacity);
    byteBuf.putInt(offset + (int) offsetBytes, value);
  }

  @Override
  public void putIntArray(final long offsetBytes, final int[] srcArray, final int srcOffset,
                          final int length) {
    final long copyBytes = length << INT_SHIFT;
    Util.checkBounds(srcOffset, length, srcArray.length);
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    int dstIdx = offset + (int) offsetBytes;
    for (int i = srcOffset; i < srcOffset + length; ++i, dstIdx += Integer.BYTES) {
      byteBuf.putInt(dstIdx, srcArray[i]);
    }
  }

  @Override
  public void putLong(final long offsetBytes, final long value) {
    assertBounds(offsetBytes, Long.BYTES, capacity);
    byteBuf.putLong(offset + (int) offsetBytes, value);
  }

  @Override
  public void putLongArray(final long offsetBytes, final long[] srcArray, final int srcOffset,
                           final int length) {
    final long copyBytes = length << LONG_SHIFT;
    Util.checkBounds(srcOffset, length, srcArray.length);
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    int dstIdx = offset + (int) offsetBytes;
    for (int i = srcOffset; i < srcOffset + length; ++i, dstIdx += Long.BYTES) {
      byteBuf.putLong(dstIdx, srcArray[i]);
    }
  }

  @Override
  public void putShort(final long offsetBytes, final short value) {
    assertBounds(offsetBytes, Short.BYTES, capacity);
    byteBuf.putShort(offset + (int) offsetBytes, value);
  }

  @Override
  public void putShortArray(final long offsetBytes, final short[] srcArray, final int srcOffset,
                            final int length) {
    final long copyBytes = length << SHORT_SHIFT;
    Util.checkBounds(srcOffset, length, srcArray.length);
    Util.checkBounds(offsetBytes, copyBytes, capacity);
    int dstIdx = offset + (int) offsetBytes;
    for (int i = srcOffset; i < srcOffset + length; ++i, dstIdx += Short.BYTES) {
      byteBuf.putShort(srcArray[i]);
    }
  }

  //OTHER WRITE METHODS XXX
  @Override
  public Object getArray() {
    return byteBuf.array();
  }

  @Override
  public ByteBuffer getByteBuffer() {
    return byteBuf;
  }

  @Override
  public void clear() {
    fill(0, capacity, (byte) 0);
  }

  @Override
  public void clear(final long offsetBytes, final long lengthBytes) {
    fill(offsetBytes, lengthBytes, (byte) 0);
  }

  @Override
  public void clearBits(final long offsetBytes, final byte bitMask) {
    assertBounds(offsetBytes, Byte.BYTES, capacity);
    final long tgtOffset = offset + offsetBytes;
    int value = byteBuf.get((int) tgtOffset) & 0XFF;
    value &= ~bitMask;
    byteBuf.put((int) tgtOffset, (byte) value);
  }

  @Override
  public void fill(final byte value) {
    fill(0, capacity, value);
  }

  @Override
  public void fill(final long offsetBytes, final long lengthBytes, final byte value) {
    Util.checkBounds((int) offsetBytes, (int) lengthBytes, capacity);
    int tgtIdx = offset + (int) offsetBytes;
    final int endIdx = tgtIdx + (int) lengthBytes;
    while (tgtIdx < endIdx) {
      byteBuf.put(tgtIdx, value);
      ++tgtIdx;
    }
  }

  @Override
  public void setBits(final long offsetBytes, final byte bitMask) {
    assertBounds(offsetBytes, Byte.BYTES, capacity);
    final int myOffset = offset + (int) offsetBytes;
    final byte value = byteBuf.get(myOffset);
    byteBuf.put(myOffset, (byte) (value | bitMask));
  }

  //OTHER XXX
  @Override
  public MemoryRequestServer getMemoryRequestServer() { //only applicable to writable
    return DefaultMemoryManager.getInstance();
  }

  @Override
  public void setMemoryRequest(final MemoryRequestServer memReqSvr) {
    //state.setMemoryRequestServer(memReqSvr);
  }
}
