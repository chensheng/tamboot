package com.tamboot.security.util;

import com.tamboot.common.tools.text.EscapeUtil;
import com.tamboot.common.tools.text.TextUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		if (request == null || TextUtil.isEmpty(cookieName)) {
			return TextUtil.EMPTY_STRING;
		}
		
		Cookie cookieList[] = request.getCookies();
		if (cookieList == null || cookieList.length < 1) {
			return TextUtil.EMPTY_STRING;
		}
		
		for (Cookie cookie : cookieList) {
			if (!cookieName.equals(cookie.getName())) {
				continue;
			}
			return EscapeUtil.urlDecode(cookie.getValue());
		}

		return TextUtil.EMPTY_STRING;
	}

    public static String getSetCookieValue(HttpServletResponse response, String setCookieName) {
        if (response == null || setCookieName == null) {
            return TextUtil.EMPTY_STRING;
        }

        String setCookie = response.getHeader("Set-Cookie");
        if (TextUtil.isEmpty(setCookie)) {
            return TextUtil.EMPTY_STRING;
        }

        String[] cookies = setCookie.split(";");
        for (String cookie : cookies) {
            if (!cookie.contains("=")) {
                continue;
            }

            String[] nameAndValue = cookie.split("=");
            if (nameAndValue.length != 2) {
                continue;
            }

            if (setCookieName.equals(nameAndValue[0].trim())) {
                return nameAndValue[1].trim();
            }
        }

        return TextUtil.EMPTY_STRING;
    }

	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue) {
		setCookie(request, response, cookieName, cookieValue, -1);
	}

	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxAge) {
		setCookie(request, response, cookieName, cookieValue, cookieMaxAge, "");
	}

	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxAge, String encoding) {
		doSetCookie(request, response, cookieName, cookieValue, cookieMaxAge, encoding);
	}

	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
		doSetCookie(request, response, cookieName, "", -1, "");
	}

	private static void doSetCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxAge, String encoding) {
		String encodedCookieValue = EscapeUtil.urlEncode(cookieValue);
		Cookie cookie = new Cookie(cookieName, encodedCookieValue);
		
		if (cookieMaxAge > 0) {
			cookie.setMaxAge(cookieMaxAge);
		}
			
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}
