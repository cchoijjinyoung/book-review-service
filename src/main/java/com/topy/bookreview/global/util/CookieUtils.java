package com.topy.bookreview.global.util;

import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.global.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtils {

  public static String getCookieValue(
      HttpServletRequest request, String cookieName) throws CustomException {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookieName.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    throw new CustomException(ErrorCode.COOKIE_NOT_FOUND);
  }
}
