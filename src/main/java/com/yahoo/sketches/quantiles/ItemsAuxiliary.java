/*
 * Copyright 2015-16, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.sketches.quantiles;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Auxiliary data structure for answering generic quantile queries
 *
 * @author Kevin Lang
 * @author Alexander Saydakov
 */
final class ItemsAuxiliary<T> {
  final long auxN_;
  final Object[] auxSamplesArr_; //array of size samples
  final long[] auxCumWtsArr_;

  /**
   * Constructs the Auxiliary structure from the ItemsSketch
   * @param qs an Itemsketch
   */
  @SuppressWarnings("unchecked")
  ItemsAuxiliary(final ItemsSketch<T> qs) {
    final int k = qs.getK();
    final long n = qs.getN();
    final long bitPattern = qs.getBitPattern();
    final Object[] combinedBuffer = qs.getCombinedBuffer();
    final int baseBufferCount = qs.getBaseBufferCount();
    final int numSamples = qs.getRetainedItems();

    final Object[] itemsArr = new Object[numSamples];
    final long[] cumWtsArr = new long[numSamples + 1]; /* the extra slot is very important */

    // Populate from ItemsSketch:
    // copy over the "levels" and then the base buffer, all with appropriate weights
    populateFromItemsSketch(k, n, bitPattern, (T[]) combinedBuffer, baseBufferCount,
        numSamples, (T[]) itemsArr, cumWtsArr, qs.getComparator());

    // Sort the first "numSamples" slots of the two arrays in tandem,
    // taking advantage of the already sorted blocks of length k
    ItemsMergeImpl.blockyTandemMergeSort((T[]) itemsArr, cumWtsArr, numSamples, k, qs.getComparator());

    // convert the item weights into totals of the weights preceding each item
    long subtot = 0;
    for (int i = 0; i < numSamples + 1; i++ ) {
      final long newSubtot = subtot + cumWtsArr[i];
      cumWtsArr[i] = subtot;
      subtot = newSubtot;
    }

    assert subtot == n;

    auxN_ = n;
    auxSamplesArr_ = itemsArr;
    auxCumWtsArr_ = cumWtsArr;
  }

  /**
   * Get the estimated value given phi
   * @param phi the fractional position where: 0 &le; &#966; &le; 1.0.
   * @return the estimated value given phi
   */
  T getQuantile(final double phi) {
    assert 0.0 <= phi;
    assert phi <= 1.0;
    if (auxN_ <= 0) { return null; }
    final long pos = posOfPhi(phi, auxN_);
    return (approximatelyAnswerPositionalQuery(pos));
  }

  /**
   * Returns the zero-based index (position) of a value in the hypothetical sorted stream of
   * values of size n. Also used by ItemsAuxiliary.
   * @param phi the fractional position where: 0 &le; &#966; &le; 1.0.
   * @param n the size of the stream
   * @return the index, a value between 0 and n-1.
   */
  static long posOfPhi(final double phi, final long n) { //don't tinker with this definition
    final long pos = (long) Math.floor(phi * n);
    return (pos == n) ? n - 1 : pos;
  }

  /**
   * Assuming that there are n items in the true stream, this asks what
   * item would appear in position 0 <= pos < n of a hypothetical sorted
   * version of that stream.
   *
   * <p>Note that since that since the true stream is unavailable,
   * we don't actually answer the question for that stream, but rather for
   * a <i>different</i> stream of the same length, that could hypothetically
   * be reconstructed from the weighted samples in our sketch.
   * @param pos position
   * @return approximate answer
   */
  @SuppressWarnings("unchecked")
  private T approximatelyAnswerPositionalQuery(final long pos) {
    assert 0 <= pos;
    assert pos < auxN_;
    final int index = chunkContainingPos(auxCumWtsArr_, pos);
    return (T) this.auxSamplesArr_[index];
  }

  /**
   * Populate the arrays and registers from an ItemsSketch
   * @param k K value of sketch
   * @param n The current size of the stream
   * @param bitPattern the bit pattern for valid log levels
   * @param combinedBuffer the combined buffer reference
   * @param baseBufferCount the count of the base buffer
   * @param numSamples Total samples in the sketch
   * @param itemsArr the consolidated array of all items from the sketch populated here
   * @param cumWtsArr the cumulative weights for each item from the sketch populated here
   */
  private final static <T> void populateFromItemsSketch(
      final int k, final long n, final long bitPattern, final T[] combinedBuffer,
      final int baseBufferCount, final int numSamples, final T[] itemsArr, final long[] cumWtsArr,
      final Comparator<? super T> comparator) {
    long weight = 1;
    int nxt = 0;
    long bits = bitPattern;
    assert bits == n / (2L * k); // internal consistency check
    for (int lvl = 0; bits != 0L; lvl++, bits >>>= 1) {
      weight *= 2;
      if ((bits & 1L) > 0L) {
        final int offset = (2 + lvl) * k;
        for (int i = 0; i < k; i++) {
          itemsArr[nxt] = combinedBuffer[i + offset];
          cumWtsArr[nxt] = weight;
          nxt++;
        }
      }
    }

    weight = 1; //NOT a mistake! We just copied the highest level; now we need to copy the base buffer
    final int startOfBaseBufferBlock = nxt;

    // Copy BaseBuffer over, along with weight = 1
    for (int i = 0; i < baseBufferCount; i++) {
      itemsArr[nxt] = combinedBuffer[i];
      cumWtsArr[nxt] = weight;
      nxt++;
    }
    assert nxt == numSamples;

    // Must sort the items that came from the base buffer.
    // Don't need to sort the corresponding weights because they are all the same.
    Arrays.sort(itemsArr, startOfBaseBufferBlock, numSamples, comparator);
    cumWtsArr[numSamples] = 0;
  }

  /**
   * This is written in terms of a plain array to facilitate testing.
   * Also used by ItemsAuxiliary.
   * @param arr the chunk containing the position
   * @param pos the position
   * @return the index of the chunk containing the position
   */
  static int chunkContainingPos(final long[] arr, final long pos) {
    final int nominalLength = arr.length - 1; /* remember, arr contains an "extra" position */
    assert nominalLength > 0;
    final long n = arr[nominalLength];
    assert 0 <= pos;
    assert pos < n;
    final int l = 0;
    final int r = nominalLength;
    // the following three asserts should probably be retained since they ensure
    // that the necessary invariants hold at the beginning of the search
    assert l < r;
    assert arr[l] <= pos;
    assert pos < arr[r];
    return searchForChunkContainingPos(arr, pos, l, r);
  }

  // Let m_i denote the minimum position of the length=n "full" sorted sequence
  //   that is represented in slot i of the length = n "chunked" sorted sequence.
  //
  // Note that m_i is the same thing as auxCumWtsArr_[i]
  //
  // Then the answer to a positional query 0 <= q < n is l, where 0 <= l < len,
  // A)  m_l <= q
  // B)   q  < m_r
  // C)   l+1 = r
  //
  // A) and B) provide the invariants for our binary search.
  // Observe that they are satisfied by the initial conditions:  l = 0 and r = len.
  private static int searchForChunkContainingPos(
          final long[] arr, final long pos, final int l, final int r) {
    // the following three asserts can probably go away eventually, since it is fairly clear
    // that if these invariants hold at the beginning of the search, they will be maintained
    assert l < r;
    assert arr[l] <= pos;
    assert pos < arr[r];
    if (l + 1 == r) {
      return l;
    }
    else {
      final int m = l + (r - l) / 2;
      if (arr[m] <= pos) {
        return (searchForChunkContainingPos(arr, pos, m, r));
      }
      else {
        return (searchForChunkContainingPos(arr, pos, l, m));
      }
    }
  }


} // end of class Auxiliary
