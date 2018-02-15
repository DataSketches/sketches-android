/*
 * Copyright 2016, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.sketches.quantiles;

import java.util.Comparator;

import com.yahoo.sketches.ArrayOfDoublesSerDe;
import com.yahoo.sketches.ArrayOfItemsSerDe;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yahoo.memory.Memory;

public class SerDeCompatibilityTest {

  private static final ArrayOfItemsSerDe<Double> serDe = new ArrayOfDoublesSerDe();

  @Test
  public void itemsToDoubles() {
    final ItemsSketch<Double> sketch1 = ItemsSketch.newInstance(Comparator.naturalOrder());
    for (int i = 1; i <= 500; i++) { sketch1.update((double) i); }

    final byte[] bytes = sketch1.toByteArray(serDe);
    final ItemsSketch<Double> sketch2;
    sketch2 = ItemsSketch.heapify(Memory.wrap(bytes), Comparator.naturalOrder(), new ArrayOfDoublesSerDe());

    for (int i = 501; i <= 1000; i++) { sketch2.update((double) i); }
    Assert.assertEquals(sketch2.getN(), 1000);
    Assert.assertTrue(sketch2.getRetainedItems() < 1000);
    Assert.assertEquals(sketch2.getMinValue(), 1.0);
    Assert.assertEquals(sketch2.getMaxValue(), 1000.0);
    // based on ~1.7% normalized rank error for this particular case
    Assert.assertEquals(sketch2.getQuantile(0.5), 500.0, 17);
  }

  @Test
  public void doublesToItems() {
    final ItemsSketch<Double> sketch1 = ItemsSketch.newInstance(Comparator.naturalOrder()); //SerVer = 3
    for (int i = 1; i <= 500; i++) { sketch1.update((double) i); }

    final byte[] bytes = sketch1.toByteArray(true, new ArrayOfDoublesSerDe()); // must be compact
    
    //reconstruct with ItemsSketch
    final ItemsSketch<Double> sketch2 = ItemsSketch.heapify(Memory.wrap(bytes), Comparator.naturalOrder(), serDe);

    for (int i = 501; i <= 1000; i++) { sketch2.update((double) i); }
    Assert.assertEquals(sketch2.getN(), 1000);
    Assert.assertTrue(sketch2.getRetainedItems() < 1000);
    Assert.assertEquals(sketch2.getMinValue(), 1.0);
    Assert.assertEquals(sketch2.getMaxValue(), 1000.0);
    // based on ~1.7% normalized rank error for this particular case
    Assert.assertEquals(sketch2.getQuantile(0.5), 500.0, 17);
  }
  
}
