package com.reimbes.constant;

public class UrlConstants {

    public static final String BASE_URL = "http://localhost:8080/api";
    public static final String LOGIN_URL = "/login";
    public static final String LOGOUT_URL = "/logout";
    public static final String DUMMY_URL = "/dummy";

    //prefix
    public static final String ADMIN_PREFIX = "/admin";
    public static final String USER_PREFIX = "/users";
    public static final String TRANSACTION_PREFIX = "/transactions";

    //admin
    public static final String MONTHLY_REPORT = "/monthly-report";
    public static final String ADD_USER = "/_add_user";


    // RESPONSE CODE
    public static final int CODE_OK = 200;
    public static final int CODE_BAD_REQUEST = 500;
    public static final int CODE_UNAUTHORIZED = 403;
}
