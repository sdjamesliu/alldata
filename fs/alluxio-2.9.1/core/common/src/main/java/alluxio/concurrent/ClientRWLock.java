/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.concurrent;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Read/write lock associated with clients rather than threads. Either its read lock or write lock
 * can be released by a thread different from the one acquiring them (but supposed to be requested
 * by the same client).
 */
@ThreadSafe
public final class ClientRWLock implements ReadWriteLock {
  private final Semaphore mAvailable;
  /** Reference count. */
  private final AtomicInteger mReferences = new AtomicInteger();
  private final int mMaxReaders;

  /**
   * Constructs a new {@link ClientRWLock}.
   *
   * @param maxReaders total number of permits, decides the max number of concurrent readers
   */
  public ClientRWLock(int maxReaders) {
    mMaxReaders = maxReaders;
    // Uses the unfair lock to prevent a read lock
    // that fails to release from locking the block forever
    // and thus blocking all the subsequent write access.
    mAvailable = new Semaphore(maxReaders, false);
  }

  @Override
  public Lock readLock() {
    return new SessionLock(1);
  }

  @Override
  public Lock writeLock() {
    return new SessionLock(mMaxReaders);
  }

  /**
   * @return the reference count
   */
  public int getReferenceCount() {
    return mReferences.get();
  }

  /**
   * Increments the reference count.
   */
  public void addReference() {
    mReferences.incrementAndGet();
  }

  /**
   * Decrements the reference count.
   *
   * @return the new reference count
   */
  public int dropReference() {
    return mReferences.decrementAndGet();
  }

  private final class SessionLock implements Lock {
    private final int mPermits;

    private SessionLock(int permits) {
      mPermits = permits;
    }

    @Override
    public void lock() {
      mAvailable.acquireUninterruptibly(mPermits);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
      mAvailable.acquire(mPermits);
    }

    @Override
    public boolean tryLock() {
      return mAvailable.tryAcquire(mPermits);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
      return mAvailable.tryAcquire(mPermits, time, unit);
    }

    @Override
    public void unlock() {
      mAvailable.release(mPermits);
    }

    @Override
    public Condition newCondition() {
      throw new UnsupportedOperationException("newCondition() is not supported");
    }
  }
}
