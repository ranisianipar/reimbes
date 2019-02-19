package com.reimbes;

public class Constant {

    public static final String BASE_URL = "http://localhost:8080/api";
    public static final String ROOT_URL = "/";
    public static final String LOGIN_URL = "/login";
    public static final String LOGOUT_URL = "/logout";

    // user
    public static final String USER_PREFIX = "/users";
    public static final String MONTHLY_REPORT = "/monthly-report";

    // admin
    public static final String ADD_USER = "/_add/user";

    // transaction
    public static final String TRANSACTION_PREFIX = "/transactions";


    // RESPONSE CODE
    public static final int CODE_OK = 200;
    public static final int CODE_BAD_REQUEST = 500;
    public static final int CODE_UNAUTHORIZED = 403;
}
