/*
 * Copyright 2017, Yahoo! Inc. Licensed under the terms of the
 * Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.memory;

/**
 * Implements a very simple memory management function that allocates new requests onto the heap.
 * @author Lee Rhodes
 */
final class DefaultMemoryManager implements MemoryRequestServer {
  private static final DefaultMemoryManager memMgr = new DefaultMemoryManager();

  private DefaultMemoryManager() {}

  static DefaultMemoryManager getInstance() {
    return memMgr;
  }

  @Override
  public WritableMemory request(final long capacityBytes) { //default allocate on heap
    final WritableMemory mem = WritableMemory.allocate((int) capacityBytes);
    mem.setMemoryRequest(this);
    return mem;
  }

  @Override
  public void requestClose(final WritableMemory memoryToClose, final WritableMemory newMemory) {
  }

}
