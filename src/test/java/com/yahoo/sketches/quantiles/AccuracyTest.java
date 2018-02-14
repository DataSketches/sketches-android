/*
 * Copyright 2017, Yahoo! Inc. Licensed under the terms of the
 * Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.sketches.quantiles;

import java.util.Comparator;
import java.util.Random;

import org.testng.annotations.Test;

/**
 * @author Lee Rhodes
 */
public class AccuracyTest {
  static Random rand = new Random();

  @Test
  public void baseTest() {
    int n = 1 << 20;
    int k = 1 << 5;

    Double[] seqArr = new Double[n];
    //build sequential array
    for (int i = 1; i <= n; i++) {
      seqArr[i - 1] = (double) i;
    }
    Double[] randArr = seqArr.clone();
    shuffle(randArr);
    ItemsSketch<Double> sketch = ItemsSketch.getInstance(k, Comparator.naturalOrder());
    for (int i = 0; i < n; i++) {
      sketch.update(randArr[i]);
    }
    double[] ranks = sketch.getCDF(seqArr);
    double maxDelta = 0;
    for (int i = 0; i < n; i++) {
      double actRank = (double)i/n;
      double estRank = ranks[i];
      double delta = actRank - estRank;
      maxDelta = Math.max(maxDelta, delta);
      //println("Act: " +  + " \tEst: " + ranks[i]);
    }
    println("Max delta: " + maxDelta);
    println(sketch.toString());

  }

  public static void shuffle(Double[] arr) {
    int n = arr.length;
    for (int i = 0; i < n; i++) {
      int j = i + rand.nextInt(n - i);
      swap(arr, i, j);
    }
  }

  public static void swap(Double[] arr, int i, int j) {
    double t = arr[i];
    arr[i] = arr[j];
    arr[j] = t;
  }


  //@Test
  public void getEpsilon() {
    for (int lgK = 4; lgK < 22; lgK++) {
      int k = 1 << lgK;
      double eps = Util.EpsilonFromK.getAdjustedEpsilon(k);
      println(k + "\t" + eps);
    }
  }

  @Test
  public void printlnTest() {
    println("PRINTING: " + this.getClass().getName());
  }

  /**
   * @param s value to print
   */
  static void println(final String s) {
    //System.out.println(s); //disable here
  }

}
