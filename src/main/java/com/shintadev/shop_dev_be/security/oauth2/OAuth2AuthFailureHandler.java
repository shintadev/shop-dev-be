package com.shintadev.shop_dev_be.security.oauth2;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.shintadev.shop_dev_be.util.CookieUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles OAuth2 authentication failures
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  /**
   * Handles OAuth2 authentication failures
   * 
   * @param request   the HTTP request
   * @param response  the HTTP response
   * @param exception the authentication exception
   * @throws IOException      if an I/O error occurs
   * @throws ServletException if a servlet error occurs
   */
  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {
    String targetUrl = "/login?error=" + exception.getLocalizedMessage();

    clearAuthenticationAttributes(request, response);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  /**
   * Clears the authentication attributes
   * 
   * @param request  the HTTP request
   * @param response the HTTP response
   */
  protected void clearAuthenticationAttributes(
      HttpServletRequest request,
      HttpServletResponse response) {
    CookieUtils.deleteCookie(request, response, "JSESSIONID");
    CookieUtils.deleteCookie(request, response, "XSRF-TOKEN");
    CookieUtils.deleteCookie(request, response, "X-XSRF-TOKEN");
    CookieUtils.deleteCookie(request, response, "X-AUTH-TOKEN");
  }
}
