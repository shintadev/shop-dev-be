package com.shintadev.shop_dev_be.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class RedissonConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Value("${spring.data.redis.password:#{null}}")
  private String password;

  @Value("${app.redisson.lock.watchdog-timeout:30000}")
  private long watchdogTimeout;

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();

    String address = String.format("redis://%s:%d", host, port);

    config.useSingleServer()
        .setAddress(address)
        .setConnectionMinimumIdleSize(5)
        .setConnectionPoolSize(20)
        .setRetryAttempts(3)
        .setRetryInterval(1500)
        .setTimeout(3000)
        .setDnsMonitoringInterval(5000);

    if (StringUtils.hasText(password)) {
      config.useSingleServer()
          .setPassword(password);
    }

    // Set the lock watchdog timeout
    // This is the maximum time for a thread to wait for a lock before it is
    // automatically released
    config.setLockWatchdogTimeout(watchdogTimeout);

    return Redisson.create(config);
  }

  @Bean
  public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redissonClient) {
    return new RedissonConnectionFactory(redissonClient);
  }
}
