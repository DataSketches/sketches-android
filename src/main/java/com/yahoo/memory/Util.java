/*
 * Copyright 2018, Yahoo! Inc. Licensed under the terms of the
 * Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.memory;

import java.util.Random;

/**
 * @author Lee Rhodes
 */
public final class Util {
  //Used to convert "type" to bytes:  bytes = longs << LONG_SHIFT
  public static final int BOOLEAN_SHIFT   = 0;
  public static final int BYTE_SHIFT      = 0;
  public static final int SHORT_SHIFT     = 1;
  public static final int CHAR_SHIFT      = 1;
  public static final int INT_SHIFT       = 2;
  public static final int LONG_SHIFT      = 3;
  public static final int FLOAT_SHIFT     = 2;
  public static final int DOUBLE_SHIFT    = 3;

  /**
   * The java line separator character as a String.
   */
  static final String LS = System.getProperty("line.separator");

  /**
   * Perform bounds checking using java assert (if enabled) checking the requested offset and
   * length against the allocated size.
   * If reqOff + reqLen &gt; allocSize or any of the parameters are negative an exception will
   * be thrown.
   * @param reqOff the requested offset
   * @param reqLen the requested length
   * @param allocSize the allocated size.
   */
  public static void assertBounds(final long reqOff, final long reqLen, final long allocSize) {
    assert ((reqOff | reqLen | (reqOff + reqLen) | (allocSize - (reqOff + reqLen))) >= 0) :
            "reqOffset: " + reqOff + ", reqLength: " + reqLen
                    + ", (reqOff + reqLen): " + (reqOff + reqLen) + ", allocSize: " + allocSize;
  }

  /**
   * Check the requested offset and length against the allocated size.
   * If reqOff + reqLen &gt; allocSize or any of the parameters are negative an exception will
   * be thrown.
   * @param reqOff the requested offset
   * @param reqLen the requested length
   * @param allocSize the allocated size.
   */
  public static void checkBounds(final long reqOff, final long reqLen, final long allocSize) {
    if ((reqOff | reqLen | (reqOff + reqLen) | (allocSize - (reqOff + reqLen))) < 0) {
      throw new IllegalArgumentException(
              "reqOffset: " + reqOff + ", reqLength: "
                      + ", (reqOff + reqLen): " + (reqOff + reqLen) + ", allocSize: " + allocSize);
    }
  }

  /**
   * Return true if the given offsets and length do not overlap.
   * @param srcOff the start of the source region
   * @param dstOff the start of the destination region
   * @param length the length of both regions
   * @return true if the given offsets and length do not overlap.
   */
  public static boolean checkOverlap(final long srcOff, final long dstOff, final long length) {
    final long min = Math.min(srcOff, dstOff);
    final long max = Math.max(srcOff, dstOff);
    return (min + length) <= max;
  }


  static final void nullCheck(final Object obj) {
    if (obj == null) {
      throw new IllegalArgumentException("An input argument is null.");
    }
  }
}
