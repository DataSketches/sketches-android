/*
 * Copyright 2017, Yahoo! Inc. Licensed under the terms of the
 * Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.memory;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.testng.annotations.Test;

public class WritableMemoryTest {

  @Test
  public void wrapBigEndian() {
    ByteBuffer bb = ByteBuffer.allocate(64); //big endian
    bb.order((ByteOrder.BIG_ENDIAN));
    WritableMemory wmem = WritableMemory.wrap(bb);
    assertFalse(wmem.isSwapBytes()); // forced to LITTLE_ENDIAN in wrap()

    bb.order((ByteOrder.BIG_ENDIAN));
    assertTrue(wmem.isSwapBytes());
    assertEquals(wmem.getResourceOrder(), ByteOrder.BIG_ENDIAN);
  }
}
