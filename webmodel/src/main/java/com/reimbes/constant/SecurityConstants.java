package com.reimbes.constant;

public class SecurityConstants {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public static final long TOKEN_PERIOD = 7 * 24 * 60 * 60 * 1000; // 7 days in millis
}
