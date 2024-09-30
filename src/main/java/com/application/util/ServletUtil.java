package com.application.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Optional;

@UtilityClass
public class ServletUtil {

    public static Optional<Cookie> getCookieByName(Cookie[] cookies, String cookieName) {
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName)).findAny();
    }

    public static void addCookie(HttpServletResponse response, Cookie[] cookies, String name, String value) {
        if (getCookieByName(cookies, name).isPresent()) {
            Cookie cookie = getCookieByName(cookies, name).get();
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
    }

    public static boolean checkCookiesContains(Cookie[] cookies, String cookieName) {
        return Arrays.stream(cookies).anyMatch(cookie -> cookie.getName().equals(cookieName));
    }

}
