/*
 * Copyright 2018, Yahoo! Inc. Licensed under the terms of the
 * Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.memory;

/**
 * Implements a very simple memory management function that allocates new requests onto the heap.
 * @author Lee Rhodes
 */
final class DefaultMemoryRequestServer implements MemoryRequestServer {
  private static final DefaultMemoryRequestServer memMgr = new DefaultMemoryRequestServer();

  private DefaultMemoryRequestServer() {}

  static DefaultMemoryRequestServer getInstance() {
    return memMgr;
  }

  @Override
  public WritableMemory request(final long capacityBytes) { // default allocate on heap
    return WritableMemory.allocate((int) capacityBytes);
  }

  @Override
  public void requestClose(final WritableMemory memoryToClose, final WritableMemory newMemory) {
  }

}
