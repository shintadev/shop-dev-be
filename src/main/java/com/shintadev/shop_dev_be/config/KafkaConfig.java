package com.shintadev.shop_dev_be.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import com.shintadev.shop_dev_be.constant.kafka.KafkaTopic;

/**
 * Configuration for Kafka
 */
@Configuration
public class KafkaConfig {
  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  /**
   * Creates a new KafkaAdmin bean
   * 
   * @return the KafkaAdmin bean
   */
  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    return new KafkaAdmin(configs);
  }

  @Bean
  public ProducerFactory<String, Map<String, Object>> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    // Retry
    configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
    configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
    // Batch
    configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 16KB
    configProps.put(ProducerConfig.LINGER_MS_CONFIG, 1); // This is the delay between batches
    configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 32MB
    // Compression
    configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // snappy is the fastest compression algorithm
    // ACKS
    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public ConsumerFactory<String, Map<String, Object>> consumerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
    configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);
    configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 60000);
    configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 20000);
    return new DefaultKafkaConsumerFactory<>(configProps);
  }

  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Map<String, Object>>> kafkaListenerContainerFactory(
      ConsumerFactory<String, Map<String, Object>> consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, Map<String, Object>> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setConcurrency(3);
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

    // Configure the error handling with retry
    factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(5000, 3)));

    return factory;
  }

  @Bean
  public KafkaTemplate<String, Map<String, Object>> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  /**
   * Creates a new NewTopic bean for the email notifications topic
   * 
   * @return the NewTopic bean
   */
  @Bean
  public NewTopic emailNotificationsTopic() {
    return TopicBuilder.name(KafkaTopic.EMAIL_NOTIFICATIONS_TOPIC)
        .partitions(3)
        .replicas(1)
        .build();
  }

  /**
   * Creates a new NewTopic bean for the payment processing topic
   * 
   * @return the NewTopic bean
   */
  @Bean
  public NewTopic paymentProcessingTopic() {
    return TopicBuilder.name(KafkaTopic.PAYMENT_PROCESSING_TOPIC)
        .partitions(3)
        .replicas(1)
        .build();
  }
}
