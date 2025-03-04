package com.shintadev.shop_dev_be.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.shintadev.shop_dev_be.exception.RedisLockException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisLockUtils {

  private final RedissonClient redissonClient;

  /**
   * Execute a task with distributed lock
   *
   * @param lockKey   the key for the lock
   * @param waitTime  the maximum time to wait for the lock
   * @param leaseTime the maximum time to hold the lock
   * @param timeUnit  the time unit
   * @param supplier  the task to execute
   * @param <T>       the return type
   * @return the result of the task
   * @throws InterruptedException if the thread is interrupted while waiting for
   *                              the lock
   */
  public <T> T executeWithLock(
      String lockKey,
      long waitTime,
      long leaseTime,
      TimeUnit timeUnit,
      Supplier<T> supplier) throws InterruptedException {
    RLock lock = redissonClient.getLock(lockKey);
    boolean locked = false;

    try {
      locked = lock.tryLock(waitTime, leaseTime, timeUnit);

      if (!locked) {
        throw new RedisLockException("Failed to acquire lock: " + lockKey);
      }

      return supplier.get();
    } finally {
      if (locked && lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }

  /**
   * Execute a task with distributed lock using default timeout settings
   *
   * @param lockKey  the key for the lock
   * @param supplier the task to execute
   * @param <T>      the return type
   * @return the result of the task
   */
  public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
    try {
      return executeWithLock(lockKey, 10, 30, TimeUnit.SECONDS, supplier);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RedisLockException("Lock operation was interrupted", e);
    }
  }

  /**
   * Execute a task with distributed lock that returns void
   *
   * @param lockKey   the key for the lock
   * @param waitTime  the maximum time to wait for the lock
   * @param leaseTime the maximum time to hold the lock
   * @param timeUnit  the time unit
   * @param runnable  the task to execute
   * @throws InterruptedException if the thread is interrupted while waiting for
   *                              the lock
   */
  public void executeWithLock(
      String lockKey,
      long waitTime,
      long leaseTime,
      TimeUnit timeUnit,
      Runnable runnable) throws InterruptedException {
    executeWithLock(lockKey, waitTime, leaseTime, timeUnit, () -> {
      runnable.run();
      return null;
    });
  }

  /**
   * Execute a task with distributed lock using default timeout settings
   *
   * @param lockKey  the key for the lock
   * @param runnable the task to execute
   */
  public void executeWithLock(String lockKey, Runnable runnable) {
    try {
      executeWithLock(lockKey, 10, 30, TimeUnit.SECONDS, runnable);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RedisLockException("Lock operation was interrupted", e);
    }
  }

}
