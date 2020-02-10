package com.reimbes.constant;

import java.nio.file.Paths;

public class UrlConstants {

    public static final String CROSS_ORIGIN_URL = "http://localhost:5000";
    public static final String LOGIN_URL = "/login";
    public static final String LOGOUT_URL = "/logout";
    public static final String ISLOGIN_URL = "/isLogin";;
    public static final String PROJECT_ROOT = Paths.get("").toAbsolutePath().toString();

    public static final String STORAGE_FOLDERNAME = "storage";

    //prefix
    public static final String ADMIN_PREFIX = "/admin";
    public static final String API_PREFIX = "/api";
    public static final String MEDICAL_PREFIX = "/medicals";
    public static final String CHANGE_PASSWORD_PREFIX = "/changepassword";
    public static final String FAMILY_MEMBER_PREFIX = "/family-members";
    public static final String USER_PREFIX = "/users";
    public static final String TRANSACTION_PREFIX = "/transactions";
    public static final String REPORT_PREFIX = "/report";
    public static final String IMAGE_PREFIX = "/image";

    //Sub-folder name
    public static final String SUB_FOLDER_REPORT = "report";
    public static final String SUB_FOLDER_TRANSACTION = "transaction";

    // others
    public static final String ID_PARAM = "/{id:.+}";
    public static final String GDN_LOGO_PATH = "/image/gdn-logo.jpg";

    // other service
    public static final String URL_RECEIPT_MAPPER = "http://receipt-mapper.herokuapp.com/image";

}
