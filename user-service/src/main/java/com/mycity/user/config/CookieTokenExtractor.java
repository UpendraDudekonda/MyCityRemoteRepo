package com.mycity.user.config;

import org.springframework.stereotype.Component;

@Component
public class CookieTokenExtractor {

    /**
     * Extracts the token from the provided cookie string.
     * 
     * @param cookie The cookie string from the request.
     * @return The token if found, or null if not found.
     */
    public String extractTokenFromCookie(String cookie) {
        if (cookie != null) {
            for (String cookiePair : cookie.split(";")) {
                String[] pair = cookiePair.split("=");
                if (pair.length == 2 && "token".equals(pair[0].trim())) {
                    return pair[1].trim();
                }
            }
        }
        return null;
    }
}
