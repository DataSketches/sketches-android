/*
 * Copyright 2017, Yahoo! Inc. Licensed under the terms of the
 * Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.sketches.quantiles;

import java.util.Arrays;
import java.util.Comparator;

class ItemsPmfCdfImpl {

  static <T> double[] getPMFOrCDF(final ItemsSketch<T> sketch, final T[] splitPoints,
      final boolean isCDF) {
    final long[] counters = internalBuildHistogram(splitPoints, sketch);
    final int numCounters = counters.length;
    final double[] result = new double[numCounters];
    final double n = sketch.getN();
    long subtotal = 0;
    if (isCDF) {
      for (int j = 0; j < numCounters; j++) {
        final long count = counters[j];
        subtotal += count;
        result[j] = subtotal / n; //normalize by n
      }
    } else { // PMF
      for (int j = 0; j < numCounters; j++) {
        final long count = counters[j];
        subtotal += count;
        result[j] = count / n; //normalize by n
      }
    }
    assert subtotal == n; //internal consistency check
    return result;
  }

  /**
   * Shared algorithm for both PMF and CDF functions. The splitPoints must be unique, monotonically
   * increasing values.
   * @param splitPoints an array of <i>m</i> unique, monotonically increasing values
   * that divide the ordered domain into <i>m+1</i> consecutive disjoint intervals.
   * @param sketch the given quantiles sketch
   * @return the unnormalized, accumulated counts of <i>m + 1</i> intervals.
   */
  @SuppressWarnings("unchecked")
  private static <T> long[] internalBuildHistogram(final T[] splitPoints, final ItemsSketch<T> sketch) {
    final Object[] levelsArr  = sketch.getCombinedBuffer();
    final Object[] baseBuffer = levelsArr;
    final int bbCount = sketch.getBaseBufferCount();
    ItemsUtil.validateValues(splitPoints, sketch.getComparator());

    final int numSplitPoints = splitPoints.length;
    final int numCounters = numSplitPoints + 1;
    final long[] counters = new long[numCounters];

    long weight = 1;
    if (numSplitPoints < 50) { // empirically determined crossover
      // sort not worth it when few split points
      ItemsPmfCdfImpl.bilinearTimeIncrementHistogramCounters(
          (T[]) baseBuffer, 0, bbCount, weight, splitPoints, counters, sketch.getComparator());
    } else {
      Arrays.sort(baseBuffer, 0, bbCount);
      // sort is worth it when many split points
      linearTimeIncrementHistogramCounters(
          (T[]) baseBuffer, 0, bbCount, weight, splitPoints, counters, sketch.getComparator()
      );
    }

    long myBitPattern = sketch.getBitPattern();
    final int k = sketch.getK();
    assert myBitPattern == sketch.getN() / (2L * k); // internal consistency check
    for (int lvl = 0; myBitPattern != 0L; lvl++, myBitPattern >>>= 1) {
      weight += weight; // *= 2
      if ((myBitPattern & 1L) > 0L) { //valid level exists
        // the levels are already sorted so we can use the fast version
        linearTimeIncrementHistogramCounters(
            (T[]) levelsArr, (2 + lvl) * k, k, weight, splitPoints, counters, sketch.getComparator());
      }
    }
    return counters;
  }

  /**
   * Because of the nested loop, cost is O(numSamples * numSplitPoints), which is bilinear.
   * This method does NOT require the samples to be sorted.
   * @param samples array of samples
   * @param offset into samples array
   * @param numSamples number of samples in samples array
   * @param weight of the samples
   * @param splitPoints must be unique and sorted. Number of splitPoints + 1 == counters.length.
   * @param counters array of counters
   */
  static <T> void bilinearTimeIncrementHistogramCounters(final T[] samples, final int offset,
      final int numSamples, final long weight, final T[] splitPoints, final long[] counters,
      final Comparator<? super T> comparator) {
    assert (splitPoints.length + 1 == counters.length);
    for (int i = 0; i < numSamples; i++) {
      final T sample = samples[i + offset];
      int j = 0;
      for (j = 0; j < splitPoints.length; j++) {
        final T splitpoint = splitPoints[j];
        if (comparator.compare(sample, splitpoint) < 0) {
          break;
        }
      }
      assert j < counters.length;
      counters[j] += weight;
    }
  }

  /**
   * This one does a linear time simultaneous walk of the samples and splitPoints. Because this
   * internal procedure is called multiple times, we require the caller to ensure these 3 properties:
   * <ol>
   * <li>samples array must be sorted.</li>
   * <li>splitPoints must be unique and sorted</li>
   * <li>number of SplitPoints + 1 == counters.length</li>
   * </ol>
   * @param samples sorted array of samples
   * @param offset into samples array
   * @param numSamples number of samples in samples array
   * @param weight of the samples
   * @param splitPoints must be unique and sorted. Number of splitPoints + 1 = counters.length.
   * @param counters array of counters
   */
  static <T> void linearTimeIncrementHistogramCounters(final T[] samples, final int offset,
      final int numSamples, final long weight, final T[] splitPoints, final long[] counters,
      final Comparator<? super T> comparator) {
    int i = 0;
    int j = 0;
    while (i < numSamples && j < splitPoints.length) {
      if (comparator.compare(samples[i + offset], splitPoints[j]) < 0) {
        counters[j] += weight; // this sample goes into this bucket
        i++; // move on to next sample and see whether it also goes into this bucket
      } else {
        j++; // no more samples for this bucket. move on the next bucket.
      }
    }

    // now either i == numSamples(we are out of samples), or
    // j == numSplitPoints(out of buckets, but there are more samples remaining)
    // we only need to do something in the latter case.
    if (j == splitPoints.length) {
      counters[j] += (weight * (numSamples - i));
    }
  }

}
