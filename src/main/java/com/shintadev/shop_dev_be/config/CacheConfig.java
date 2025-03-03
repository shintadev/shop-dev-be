package com.shintadev.shop_dev_be.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

/**
 * Configuration for Redis caching
 */
@Configuration
@RequiredArgsConstructor
public class CacheConfig {

  @Value("${app.cache.product.ttl}")
  private long timeToLive;

  @Value("${spring.redis.host}")
  private String host;

  @Value("${spring.redis.port}")
  private int port;

  @Value("${spring.redis.password:#{null}}")
  private String password;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
    if (StringUtils.hasText(password)) {
      config.setPassword(password);
    }
    return new LettuceConnectionFactory(config);
  }

  /**
   * Creates a new RedisTemplate bean
   * 
   * @param redisConnectionFactory the RedisConnectionFactory
   * @return the RedisTemplate bean
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

    template.afterPropertiesSet();
    return template;
  }

  /**
   * Creates a new CacheManager bean
   * 
   * @param redisConnectionFactory the RedisConnectionFactory
   * @return the CacheManager bean
   */
  @Bean
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    // Default cache configuration
    RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMillis(timeToLive))
        .disableCachingNullValues()
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

    // Cache configurations for different caches
    Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

    // Product: stable cache
    cacheConfigs.put("products", defaultCacheConfig.entryTtl(Duration.ofHours(2)));

    // Category: less frequently updated cache
    cacheConfigs.put("category", defaultCacheConfig.entryTtl(Duration.ofHours(24)));

    // Product by category: frequently updated cache
    cacheConfigs.put("product-by-category", defaultCacheConfig.entryTtl(Duration.ofHours(1)));

    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(defaultCacheConfig)
        .withInitialCacheConfigurations(cacheConfigs)
        .transactionAware()
        .build();
  }
}
