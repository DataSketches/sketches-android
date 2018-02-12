/*
 * Copyright 2017, Yahoo! Inc. Licensed under the terms of the
 * Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.memory;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.testng.annotations.Test;

public class WritableMemoryImplTest {

  //Simple Native arrays

  @Test
  public void checkBooleanArray() {
    boolean[] srcArray = { true, false, true, false, false, true, true, false };
    boolean[] dstArray = new boolean[8];

    final ByteBuffer bb = ByteBuffer.wrap(new byte[srcArray.length]);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    for (boolean b : srcArray) { bb.put((byte) ((b ? 0x1 : 0x0) & 0xFF)); }

    Memory mem = Memory.wrap(bb);
    mem.getBooleanArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }

    WritableMemory wmem = WritableMemory.wrap(bb);
    wmem.getBooleanArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }
    assertTrue(mem.hasArray());
  }

  @Test
  public void checkByteArray() {
    byte[] srcArray = { 1, -2, 3, -4, 5, -6, 7, -8 };
    byte[] dstArray = new byte[8];

    final ByteBuffer bb = ByteBuffer.wrap(new byte[srcArray.length]);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    for (byte b : srcArray) { bb.put(b); }

    Memory mem = Memory.wrap(bb);
    mem.getByteArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }

    WritableMemory wmem = WritableMemory.wrap(bb);
    wmem.getByteArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }
  }

  @Test
  public void checkCharArray() {
    char[] srcArray = { 1, 2, 3, 4, 5, 6, 7, 8 };
    char[] dstArray = new char[8];

    final ByteBuffer bb = ByteBuffer.wrap(new byte[srcArray.length * Character.BYTES]);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    for (char c : srcArray) { bb.putChar(c); }

    Memory mem = Memory.wrap(bb);
    mem.getCharArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }

    WritableMemory wmem = WritableMemory.wrap(bb);
    wmem.getCharArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }
  }

  @Test
  public void checkShortArray() {
    short[] srcArray = { 1, -2, 3, -4, 5, -6, 7, -8 };
    short[] dstArray = new short[8];

    final ByteBuffer bb = ByteBuffer.wrap(new byte[srcArray.length * Short.BYTES]);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    for (short s : srcArray) { bb.putShort(s); }

    Memory mem = Memory.wrap(bb);
    mem.getShortArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }

    WritableMemory wmem = WritableMemory.wrap(bb);
    wmem.getShortArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }
  }

  @Test
  public void checkIntArray() {
    int[] srcArray = { 1, -2, 3, -4, 5, -6, 7, -8 };
    int[] dstArray = new int[8];

    final ByteBuffer bb = ByteBuffer.wrap(new byte[srcArray.length * Integer.BYTES]);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    for (int i : srcArray) { bb.putInt(i); }

    Memory mem = Memory.wrap(bb);
    mem.getIntArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }

    WritableMemory wmem = WritableMemory.wrap(bb);
    wmem.getIntArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }
  }

  @Test
  public void checkLongArray() {
    long[] srcArray = { 1, -2, 3, -4, 5, -6, 7, -8 };
    long[] dstArray = new long[8];

    final ByteBuffer bb = ByteBuffer.wrap(new byte[srcArray.length * Long.BYTES]);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    for (long l : srcArray) { bb.putLong(l); }

    Memory mem = Memory.wrap(bb);
    mem.getLongArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }

    WritableMemory wmem = WritableMemory.wrap(bb);
    wmem.getLongArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }
  }

  @Test
  public void checkFloatArray() {
    float[] srcArray = { 1, -2, 3, -4, 5, -6, 7, -8 };
    float[] dstArray = new float[8];

    final ByteBuffer bb = ByteBuffer.wrap(new byte[srcArray.length * Float.BYTES]);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    for (float f : srcArray) { bb.putFloat(f); }

    Memory mem = Memory.wrap(bb);
    mem.getFloatArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }

    WritableMemory wmem = WritableMemory.wrap(bb);
    wmem.getFloatArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }
  }

  @Test
  public void checkDoubleArray() {
    double[] srcArray = { 1, -2, 3, -4, 5, -6, 7, -8 };
    double[] dstArray = new double[8];

    final ByteBuffer bb = ByteBuffer.wrap(new byte[srcArray.length * Double.BYTES]);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    for (double d : srcArray) { bb.putDouble(d); }

    Memory mem = Memory.wrap(bb);
    mem.getDoubleArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }

    WritableMemory wmem = WritableMemory.wrap(bb);
    wmem.getDoubleArray(0, dstArray, 0, 8);
    for (int i=0; i<8; i++) {
      assertEquals(dstArray[i], srcArray[i]);
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void checkNativeBaseBound() {
    int memCapacity = 64;
    final ByteBuffer bb = ByteBuffer.allocateDirect(memCapacity);
    WritableMemory mem = WritableMemory.wrap(bb);
    mem.toHexString("Force Assertion Error", memCapacity, 8);
  }

  /*
  @Test
  public void checkNativeSrcArrayBound() {
    long memCapacity = 64;
    try (WritableDirectHandle wrh = WritableMemory.allocateDirect(memCapacity)) {
      WritableMemory mem = wrh.get();
      byte[] srcArray = { 1, -2, 3, -4 };
      mem.putByteArray(0L, srcArray, 0, 5);
    } catch (IllegalArgumentException e) {
      //pass
    }
  }

  //Copy Within tests

  @Test
  public void checkDegenerateCopyTo() {
    WritableMemory wmem = WritableMemory.allocate(64);
    wmem.copyTo(0, wmem, 0, 64);
    //TODO
  }

  @Test
  public void checkCopyWithinNativeSmall() {
    int memCapacity = 64;
    int half = memCapacity/2;
    try (WritableDirectHandle wrh = WritableMemory.allocateDirect(memCapacity)) {
      WritableMemory mem = wrh.get();
      mem.clear();

      for (int i=0; i<half; i++) { //fill first half
        mem.putByte(i, (byte) i);
      }

      mem.copyTo(0, mem, half, half);

      for (int i=0; i<half; i++) {
        assertEquals(mem.getByte(i+half), (byte) i);
      }
    }
  }

  @Test
  public void checkCopyWithinNativeLarge() {
    int memCapacity = (2 << 20) + 64;
    int memCapLongs = memCapacity / 8;
    int halfBytes = memCapacity / 2;
    int halfLongs = memCapLongs / 2;
    try (WritableDirectHandle wrh = WritableMemory.allocateDirect(memCapacity)) {
      WritableMemory mem = wrh.get();
      mem.clear();

      for (int i=0; i < halfLongs; i++) {
        mem.putLong(i*8,  i);
      }

      mem.copyTo(0, mem, halfBytes, halfBytes);

      for (int i=0; i < halfLongs; i++) {
        assertEquals(mem.getLong((i + halfLongs)*8), i);
      }
    }
  }

  @Test
  public void checkCopyWithinNativeSrcBound() {
    int memCapacity = 64;
    try (WritableDirectHandle wrh = WritableMemory.allocateDirect(memCapacity)) {
      WritableMemory mem = wrh.get();
      mem.copyTo(32, mem, 32, 33);  //hit source bound check
      fail("Did Not Catch Assertion Error: source bound");
    }
    catch (IllegalArgumentException e) {
      //pass
    }
  }

  @Test
  public void checkCopyWithinNativeDstBound() {
    int memCapacity = 64;
    try (WritableDirectHandle wrh = WritableMemory.allocateDirect(memCapacity)) {
      WritableMemory mem = wrh.get();
      mem.copyTo(0, mem, 32, 33);  //hit dst bound check
      fail("Did Not Catch Assertion Error: dst bound");
    }
    catch (IllegalArgumentException e) {
      //pass
    }
  }

  @Test
  public void checkCopyCrossNativeSmall() {
    int memCapacity = 64;

    try (WritableDirectHandle wrh1 = WritableMemory.allocateDirect(memCapacity);
        WritableDirectHandle wrh2 = WritableMemory.allocateDirect(memCapacity))
    {
      WritableMemory mem1 = wrh1.get();
      WritableMemory mem2 = wrh2.get();

      for (int i=0; i < memCapacity; i++) {
        mem1.putByte(i, (byte) i);
      }
      mem2.clear();
      mem1.copyTo(0, mem2, 0, memCapacity);

      for (int i=0; i<memCapacity; i++) {
        assertEquals(mem2.getByte(i), (byte) i);
      }
      wrh1.close();
      wrh2.close();
    }
  }

  @Test
  public void checkCopyCrossNativeLarge() {
    int memCapacity = (2<<20) + 64;
    int memCapLongs = memCapacity / 8;

    try (WritableDirectHandle wrh1 = WritableMemory.allocateDirect(memCapacity);
        WritableDirectHandle wrh2 = WritableMemory.allocateDirect(memCapacity))
    {
      WritableMemory mem1 = wrh1.get();
      WritableMemory mem2 = wrh2.get();

      for (int i=0; i < memCapLongs; i++) {
        mem1.putLong(i*8, i);
      }
      mem2.clear();

      mem1.copyTo(0, mem2, 0, memCapacity);

      for (int i=0; i<memCapLongs; i++) {
        assertEquals(mem2.getLong(i*8), i);
      }
    }
  }

  @Test
  public void checkCopyCrossNativeAndByteArray() {
    int memCapacity = 64;
    try (WritableDirectHandle wrh1 = WritableMemory.allocateDirect(memCapacity)) {
      WritableMemory mem1 = wrh1.get();

      for (int i= 0; i < mem1.getCapacity(); i++) {
        mem1.putByte(i, (byte) i);
      }

      WritableMemory mem2 = WritableMemory.allocate(memCapacity);
      mem1.copyTo(8, mem2, 16, 16);

      for (int i=0; i<16; i++) {
        assertEquals(mem1.getByte(8+i), mem2.getByte(16+i));
      }
      //println(mem2.toHexString("Mem2", 0, (int)mem2.getCapacity()));
    }
  }

  @Test
  public void checkCopyCrossRegionsSameNative() {
    int memCapacity = 128;

    try (WritableDirectHandle wrh1 = WritableMemory.allocateDirect(memCapacity)) {
      WritableMemory mem1 = wrh1.get();

      for (int i= 0; i < mem1.getCapacity(); i++) {
        mem1.putByte(i, (byte) i);
      }
      //println(mem1.toHexString("Mem1", 0, (int)mem1.getCapacity()));

      Memory reg1 = mem1.region(8, 16);
      //println(reg1.toHexString("Reg1", 0, (int)reg1.getCapacity()));

      WritableMemory reg2 = mem1.writableRegion(24, 16);
      //println(reg2.toHexString("Reg2", 0, (int)reg2.getCapacity()));
      reg1.copyTo(0, reg2, 0, 16);

      for (int i=0; i<16; i++) {
        assertEquals(reg1.getByte(i), reg2.getByte(i));
        assertEquals(mem1.getByte(8+i), mem1.getByte(24+i));
      }
      //println(mem1.toHexString("Mem1", 0, (int)mem1.getCapacity()));
    }
  }

  @Test
  public void checkCopyCrossNativeArrayAndHierarchicalRegions() {
    int memCapacity = 64;
    try (WritableDirectHandle wrh1 = WritableMemory.allocateDirect(memCapacity)) {
      WritableMemory mem1 = wrh1.get();

      for (int i= 0; i < mem1.getCapacity(); i++) { //fill with numbers
        mem1.putByte(i, (byte) i);
      }
      //println(mem1.toHexString("Mem1", 0, (int)mem1.getCapacity()));

      WritableMemory mem2 = WritableMemory.allocate(memCapacity);

      Memory reg1 = mem1.region(8, 32);
      Memory reg1B = reg1.region(8, 16);
      //println(reg1.toHexString("Reg1", 0, (int)reg1.getCapacity()));
      //println(reg1B.toHexString("Reg1B", 0, (int)reg1B.getCapacity()));

      WritableMemory reg2 = mem2.writableRegion(32, 16);
      reg1B.copyTo(0, reg2, 0, 16);
      //println(reg2.toHexString("Reg2", 0, (int)reg2.getCapacity()));

      //println(mem2.toHexString("Mem2", 0, (int)mem2.getCapacity()));
      for (int i = 32, j = 16; i < 40; i++, j++) {
        assertEquals(mem2.getByte(i), j);
      }
    }
  }
  */

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void checkRegionBounds() {
    int memCapacity = 64;
    final ByteBuffer bb = ByteBuffer.allocateDirect(memCapacity);
    WritableMemory mem = WritableMemory.wrap(bb);
    mem.writableRegion(1, 64);
  }

  @Test
  public void checkByteBufferWrap() {
    int memCapacity = 64;
    ByteBuffer byteBuf = ByteBuffer.allocate(memCapacity);
    byteBuf.order(ByteOrder.nativeOrder());

    for (int i=0; i<memCapacity; i++) {
      byteBuf.put(i, (byte) i);
    }

    WritableMemory wmem = WritableMemory.wrap(byteBuf);

    for (int i=0; i<memCapacity; i++) {
      assertEquals(wmem.getByte(i), byteBuf.get(i));
    }

    assertTrue(wmem.hasByteBuffer());
    ByteBuffer byteBuf2 = wmem.getByteBuffer();
    assertEquals(byteBuf2, byteBuf);
    //println( mem.toHexString("HeapBB", 0, memCapacity));
  }

  @Test
  public void checkWrapWithBBReadonly1() {
    int memCapacity = 64;
    ByteBuffer byteBuf = ByteBuffer.allocate(memCapacity);
    byteBuf.order(ByteOrder.nativeOrder());

    for (int i = 0; i < memCapacity; i++) {
      byteBuf.put(i, (byte) i);
    }

    Memory mem = WritableMemory.wrap(byteBuf);

    for (int i = 0; i < memCapacity; i++) {
      assertEquals(mem.getByte(i), byteBuf.get(i));
    }

    //println(mem.toHexString("HeapBB", 0, memCapacity));
  }

  @Test(expectedExceptions = ReadOnlyException.class)
  public void checkWrapWithBBReadonly2() {
    int memCapacity = 64;
    ByteBuffer byteBuf = ByteBuffer.allocate(memCapacity);
    byteBuf.order(ByteOrder.nativeOrder());
    ByteBuffer byteBufRO = byteBuf.asReadOnlyBuffer();

    WritableMemory.wrap(byteBufRO);
  }

  @Test
  public void checkWrapWithDirectBBReadonly() {
    int memCapacity = 64;
    ByteBuffer byteBuf = ByteBuffer.allocateDirect(memCapacity);
    byteBuf.order(ByteOrder.nativeOrder());

    for (int i = 0; i < memCapacity; i++) {
      byteBuf.put(i, (byte) i);
    }
    ByteBuffer byteBufRO = byteBuf.asReadOnlyBuffer();
    byteBufRO.order(ByteOrder.nativeOrder());

    Memory mem = Memory.wrap(byteBufRO);

    for (int i = 0; i < memCapacity; i++) {
      assertEquals(mem.getByte(i), byteBuf.get(i));
    }

    //println(mem.toHexString("HeapBB", 0, memCapacity));
  }

  @Test(expectedExceptions = ReadOnlyException.class)
  public void checkWrapWithDirectBBReadonlyPut() {
    int memCapacity = 64;
    ByteBuffer byteBuf = ByteBuffer.allocateDirect(memCapacity);
    ByteBuffer byteBufRO = byteBuf.asReadOnlyBuffer();
    byteBufRO.order(ByteOrder.nativeOrder());

    WritableMemory.wrap(byteBufRO);
  }

  @Test
  public void checkByteBufferWrapDirectAccess() {
    int memCapacity = 64;
    ByteBuffer byteBuf = ByteBuffer.allocateDirect(memCapacity);
    byteBuf.order(ByteOrder.nativeOrder());

    for (int i=0; i<memCapacity; i++) {
      byteBuf.put(i, (byte) i);
    }

    Memory mem = Memory.wrap(byteBuf);

    for (int i=0; i<memCapacity; i++) {
      assertEquals(mem.getByte(i), byteBuf.get(i));
    }

    //println( mem.toHexString("HeapBB", 0, memCapacity));
  }

  @Test
  public void checkIsDirect() {
    int memCapacity = 64;
    WritableMemory mem = WritableMemory.allocate(memCapacity);
    assertFalse(mem.isDirect());
    final ByteBuffer bb = ByteBuffer.allocateDirect(memCapacity);
    mem = WritableMemory.wrap(bb);
    assertTrue(mem.isDirect());
  }


  @Test
  public void checkIsReadOnly() {
    byte[] srcArray = { 1, -2, 3, -4, 5, -6, 7, -8 };

    WritableMemory wmem = WritableMemory.wrap(srcArray);
    assertFalse(wmem.isResourceReadOnly());

    Memory memRO = wmem;
    assertFalse(memRO.isResourceReadOnly());

    for (int i = 0; i < wmem.getCapacity(); i++) {
      assertEquals(wmem.getByte(i), memRO.getByte(i));
    }
  }

  @Test
  public void checkEmptyByteArray() {
    Memory memory = Memory.wrap(new byte[0]);
    assertEquals(memory.getCapacity(), 0);
  }

  @Test
  public void checkGoodBounds() {
    Util.checkBounds(50, 50, 100);
  }

  @Test
  public void checkCompareToHeap() {
    byte[] arr1 = new byte[] {0, 1, 2, 3};
    byte[] arr2 = new byte[] {0, 1, 2, 4};
    byte[] arr3 = new byte[] {0, 1, 2, 3, 4};

    Memory mem1 = Memory.wrap(arr1);
    Memory mem2 = Memory.wrap(arr2);
    Memory mem3 = Memory.wrap(arr3);
    Memory mem4 = Memory.wrap(arr3); //same resource

    int comp = mem1.compareTo(0, 3, mem2, 0, 3);
    assertEquals(comp, 0);
    comp = mem1.compareTo(0, 4, mem2, 0, 4);
    assertEquals(comp, -1);
    comp = mem2.compareTo(0, 4, mem1, 0, 4);
    assertEquals(comp, 1);
    //different lengths
    comp = mem1.compareTo(0, 4, mem3, 0, 5);
    assertEquals(comp, -1);
    comp = mem3.compareTo(0, 5, mem1, 0, 4);
    assertEquals(comp, 1);
    comp = mem3.compareTo(0,  5, mem4, 0, 5);
    assertEquals(comp, 0);
    comp = mem3.compareTo(0, 4, mem4, 1, 4);
    assertEquals(comp, -1);
    mem3.checkBounds(0, 5);
  }

  @Test
  public void checkDuplicate() {
    WritableMemory wmem = WritableMemory.allocate(64);
    for (int i = 0; i < 64; i++) { wmem.putByte(i, (byte)i); }

    WritableMemory wmem2 = wmem.writableDuplicate();
    for (int i = 0; i < 64; i++) {
      assertEquals(wmem2.getByte(i), i);
    }
  }

  @Test
  public void checkRegionOffset() {
    WritableMemory wmem = WritableMemory.allocate(64);
    for (int i = 0; i < 64; ++i) {
      wmem.putByte(i, (byte) i);
    }

    WritableMemory r = wmem.writableRegion(16, 32);
    assertEquals(r.getByte(0), 16);

    WritableMemory reg = wmem.writableRegion(32, 32);
    assertEquals(reg.getRegionOffset(0), 32);
    assertEquals(reg.getByte(0), 32);

    WritableMemory reg2 = reg.writableRegion(16, 16);
    assertEquals(reg2.getRegionOffset(0), 32 + 16);
    assertEquals(reg2.getByte(0), 48);
  }

  @Test
  public void checkIsSameResource() {
    byte[] byteArr = new byte[64];
    WritableMemory wmem1 = WritableMemory.wrap(byteArr);
    WritableMemory wmem2 = WritableMemory.wrap(byteArr);
    assertTrue(wmem1.isSameResource(wmem2));
  }

  @Test
  public void printlnTest() {
    println("PRINTING: "+this.getClass().getName());
  }

  /**
   * @param s value to print
   */
  static void println(String s) {
    //System.out.println(s); //disable here
  }

}
