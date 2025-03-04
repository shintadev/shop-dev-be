package com.shintadev.shop_dev_be.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import com.shintadev.shop_dev_be.constant.KafkaConstants;

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

  /**
   * Creates a new NewTopic bean for the verify email topic
   * 
   * @return the NewTopic bean
   */
  @Bean
  public NewTopic verifyEmailTopic() {
    return TopicBuilder.name(KafkaConstants.VERIFICATION_EMAILS_TOPIC)
        .partitions(3)
        .replicas(1)
        .build();
  }

  /**
   * Creates a new NewTopic bean for the password reset email topic
   * 
   * @return the NewTopic bean
   */
  @Bean
  public NewTopic passwordResetEmailTopic() {
    return TopicBuilder.name(KafkaConstants.PASSWORD_RESET_EMAILS_TOPIC)
        .partitions(3)
        .replicas(1)
        .build();
  }

  /**
   * Creates a new NewTopic bean for the order confirmation email topic
   * 
   * @return the NewTopic bean
   */
  @Bean
  public NewTopic welcomeEmailTopic() {
    return TopicBuilder.name(KafkaConstants.WELCOME_EMAILS_TOPIC)
        .partitions(3)
        .replicas(1)
        .build();
  }
}
