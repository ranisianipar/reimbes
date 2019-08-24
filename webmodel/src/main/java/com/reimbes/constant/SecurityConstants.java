package com.reimbes.constant;

public class SecurityConstants {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    // in seconds (1 hour)
    public static final long TOKEN_PERIOD = 3600 * 24 * 7; // 7 days
}
