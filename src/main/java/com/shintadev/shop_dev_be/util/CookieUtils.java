package com.shintadev.shop_dev_be.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Utility class for handling cookies
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtils {

  /**
   * Get a cookie from the request
   * 
   * @param request the request
   * @param name    the name of the cookie
   * @return the cookie
   */
  public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
      return Arrays.stream(cookies)
          .filter(cookie -> name.equals(cookie.getName()))
          .findFirst();
    }
    return Optional.empty();
  }

  /**
   * Add a cookie to the response
   * 
   * @param response the response
   * @param name     the name of the cookie
   * @param value    the value of the cookie
   * @param maxAge   the maximum age of the cookie
   */
  public static void addCookie(
      HttpServletResponse response,
      String name,
      String value,
      int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  /**
   * Delete a cookie from the response
   * 
   * @param request  the request
   * @param response the response
   * @param name     the name of the cookie
   */
  public static void deleteCookie(
      HttpServletRequest request,
      HttpServletResponse response,
      String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
      Arrays.stream(cookies)
          .filter(cookie -> name.equals(cookie.getName()))
          .forEach(cookie -> {
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
          });
    }
  }

  /**
   * Serialize an object to a string
   * 
   * @param object the object to serialize
   * @return the serialized object
   */
  public static String serialize(Object object) {
    return Base64.getUrlEncoder()
        .encodeToString(SerializationUtils.serialize(object));
  }

  /**
   * Deserialize a string to an object
   * 
   * @param cookie the cookie to deserialize
   * @param cls    the class of the object
   * @return the deserialized object
   */
  public static <T> T deserialize(Cookie cookie, Class<T> cls) {
    byte[] data = Base64.getUrlDecoder().decode(cookie.getValue());
    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
      return cls.cast(ois.readObject());
    } catch (ClassCastException | IOException | ClassNotFoundException e) {
      throw new IllegalArgumentException("Failed to deserialize cookie value", e);
    }
  }
}
