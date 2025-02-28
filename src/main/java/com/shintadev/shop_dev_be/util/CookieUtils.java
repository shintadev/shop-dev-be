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

@Component
public class CookieUtils {

  public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
      return Arrays.stream(cookies)
          .filter(cookie -> name.equals(cookie.getName()))
          .findFirst();
    }
    return Optional.empty();
  }

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

  public static String serialize(Object object) {
    return Base64.getUrlEncoder()
        .encodeToString(SerializationUtils.serialize(object));
  }

  public static <T> T deserialize(Cookie cookie, Class<T> cls) {
    byte[] data = Base64.getUrlDecoder().decode(cookie.getValue());
    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
      return cls.cast(ois.readObject());
    } catch (ClassCastException | IOException | ClassNotFoundException e) {
      throw new IllegalArgumentException("Failed to deserialize cookie value", e);
    }
  }
}
