package com.shintadev.shop_dev_be.security.oauth2;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.security.jwt.JwtTokenProvider;
import com.shintadev.shop_dev_be.util.CookieUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    String targetUrl = determineTargetUrl(authentication);

    if (response.isCommitted()) {
      log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    // clearAuthenticationAttributes(request, response);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  private String determineTargetUrl(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    String token = jwtTokenProvider.generateToken(user);

    return UriComponentsBuilder.fromUriString("/")
        .queryParam("token", token)
        .build()
        .toUriString();
  }

  protected void clearAuthenticationAttributes(
      HttpServletRequest request,
      HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    CookieUtils.deleteCookie(request, response, "JSESSIONID");
    CookieUtils.deleteCookie(request, response, "XSRF-TOKEN");
    CookieUtils.deleteCookie(request, response, "X-XSRF-TOKEN");
    CookieUtils.deleteCookie(request, response, "X-AUTH-TOKEN");
  }
}
