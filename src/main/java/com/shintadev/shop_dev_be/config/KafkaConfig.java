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

@Configuration
public class KafkaConfig {
  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    return new KafkaAdmin(configs);
  }

  @Bean
  public NewTopic verifyEmailTopic() {
    return TopicBuilder.name(KafkaConstants.VERIFICATION_EMAILS_TOPIC)
        .partitions(3)
        .replicas(1)
        .build();
  }

  @Bean
  public NewTopic passwordResetEmailTopic() {
    return TopicBuilder.name(KafkaConstants.PASSWORD_RESET_EMAILS_TOPIC)
        .partitions(3)
        .replicas(1)
        .build();
  }

  @Bean
  public NewTopic orderConfirmationTopic() {
    return TopicBuilder.name(KafkaConstants.ORDER_CONFIRMATION_EMAILS_TOPIC)
        .partitions(3)
        .replicas(1)
        .build();
  }
}
