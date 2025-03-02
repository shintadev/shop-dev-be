package com.shintadev.shop_dev_be.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration for asynchronous tasks
 */
@Configuration
@EnableAsync
public class AsyncConfig {

  /**
   * Creates a new task executor bean
   * 
   * @return the task executor bean
   */
  @Bean(name = "taskExecutor")
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(25);
    executor.setThreadNamePrefix("ShopDev-AsyncTask-");
    executor.initialize();
    return executor;
  }

  /**
   * Creates a new email executor bean
   * 
   * @return the email executor bean
   */
  @Bean(name = "emailExecutor")
  public Executor emailExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(5);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("ShopDev-EmailTask-");
    executor.initialize();
    return executor;
  }
}