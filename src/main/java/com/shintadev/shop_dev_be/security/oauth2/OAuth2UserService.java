package com.shintadev.shop_dev_be.security.oauth2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.shintadev.shop_dev_be.constant.ResourceName;
import com.shintadev.shop_dev_be.constant.kafka.EmailNotificationType;
import com.shintadev.shop_dev_be.constant.kafka.KafkaMessageDataAttribute;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.domain.model.enums.user.RoleName;
import com.shintadev.shop_dev_be.domain.model.enums.user.UserStatus;
import com.shintadev.shop_dev_be.exception.ResourceNotFoundException;
import com.shintadev.shop_dev_be.kafka.producer.MessageProducer;
import com.shintadev.shop_dev_be.repository.user.UserRepo;
import com.shintadev.shop_dev_be.repository.user.RoleRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for OAuth2 user authentication
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepo userRepo;
  private final RoleRepo roleRepo;
  private final MessageProducer messageProducer;

  /**
   * Load user details from OAuth2 provider
   * 
   * @param userRequest the OAuth2 user request
   * @return the OAuth2 user
   * @throws OAuth2AuthenticationException if the user is not found
   */
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    log.info("OAuth2User: {}", oAuth2User);

    try {
      return processOAuth2User(userRequest, oAuth2User);
    } catch (AuthenticationException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
    }
  }

  /**
   * Process the OAuth2 user
   * 
   * @param userRequest the OAuth2 user request
   * @param oAuth2User  the OAuth2 user
   * @return the OAuth2 user
   */
  private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
    Map<String, Object> attributes = oAuth2User.getAttributes();
    log.info("Attributes: {}", attributes);

    if (!attributes.containsKey("email") || !StringUtils.hasText((String) attributes.get("email"))) {
      throw new ResourceNotFoundException("Email not found from OAuth2 provider");
    }

    String email = (String) attributes.get("email");
    log.info("Email: {}", email);

    // Check if user already exists in database
    Optional<User> userOpt = userRepo.findByEmail(email);
    User user;

    if (userOpt.isPresent()) {
      user = userOpt.get();
    } else {
      user = registerOAuth2User(attributes);
      Map<String, Object> emailData = new HashMap<>();
      emailData.put(KafkaMessageDataAttribute.EMAIL_TYPE_KEY, EmailNotificationType.WELCOME);
      emailData.put(KafkaMessageDataAttribute.RECIPIENT_EMAIL_KEY, user.getEmail());
      emailData.put(KafkaMessageDataAttribute.RECIPIENT_NAME_KEY, user.getDisplayName());
      emailData.put(KafkaMessageDataAttribute.SUBJECT_KEY, "Welcome to Shop Dev");
      messageProducer.sendEmailNotification(emailData);
    }

    return new DefaultOAuth2User(
        user.getAuthorities(),
        attributes,
        "email");
  }

  /**
   * Register the OAuth2 user
   * 
   * @param attributes the OAuth2 user attributes from the provider
   * @return the registered user
   */
  private User registerOAuth2User(Map<String, Object> attributes) {
    User user = User.builder()
        .email((String) attributes.get("email"))
        .displayName((String) attributes.get("name"))
        .firstName((String) attributes.get("given_name"))
        .lastName((String) attributes.get("family_name"))
        .avatarUrl((String) attributes.get("picture"))
        .status(UserStatus.ACTIVE)
        .roles(
            Set.of(roleRepo.findByName(RoleName.USER)
                .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.ROLE, "name", RoleName.USER))))
        .build();

    log.info("Registered user: {}", user);

    return userRepo.save(user);
  }
}
