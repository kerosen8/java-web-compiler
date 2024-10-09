package com.application.service;

import com.application.compiler.CompilationResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ServletService {

    public Map<Integer, CompilationResult> sortedRecentCodes(Map<Integer, CompilationResult> resultMap) throws IOException {
        return resultMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.comparing(CompilationResult::getCompilationTime).reversed()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e2,
                        LinkedHashMap::new
                ));
    }

    public String parseClassName(String code) {
        String target = "public class";
        int startIndex = code.indexOf(target) + target.length() + 1;
        StringBuilder result = new StringBuilder();
        for (int i = startIndex; ; i++) {
            char c = code.charAt(i);
            if (c != ' ') {
                result.append(code.charAt(i));
            } else break;
        }
        return result.toString();
    }

    public boolean recentCodePresent(Map<Integer, CompilationResult> results, int compilationNumber) {
        for (Integer entry : results.keySet()) {
            if (entry == compilationNumber) {
                return true;
            }
        }
        return false;
    }

    public Optional<Cookie> getCookieByName(Cookie[] cookies, String cookieName) {
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName)).findAny();
    }

    public void addCookie(HttpServletResponse response, Cookie[] cookies, String name, String value) {
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

    public boolean checkCookiesContains(Cookie[] cookies, String cookieName) {
        if (cookies != null) return Arrays.stream(cookies).anyMatch(cookie -> cookie.getName().equals(cookieName));
        return false;
    }

}
