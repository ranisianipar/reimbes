package com.reimbes.constant;

import java.nio.file.Paths;

public class UrlConstants {

    public static final String CROSS_ORIGIN_URL = "http://localhost:5000/api";
    public static final String LOGIN_URL = "/login";
    public static final String LOGOUT_URL = "/logout";
    public static final String ISLOGIN_URL = "/isLogin";
    public static final String PROJECT_ROOT = Paths.get("").toAbsolutePath().toString();

    // folder path
    public static final String TESSERACT_TRAINNED_DATA_PATH = PROJECT_ROOT + "\\lib\\tesseract\\tessdata\\";
    public static final String IMAGE_FOLDER_PATH = PROJECT_ROOT + "\\reims-img\\";

    //prefix
    public static final String ADMIN_PREFIX = "/admin";
    public static final String API_PREFIX = "/api";
    public static final String USER_PREFIX = "/users";
    public static final String TRANSACTION_PREFIX = "/transactions";

    //admin
    public static final String REPORT = "/report";

    // others
    public static final String ID_PARAM = "/{id:.+}";
    public static final String IMAGE_PARAM = "/{image:.+}";

}
