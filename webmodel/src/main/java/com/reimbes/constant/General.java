package com.reimbes.constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class General {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT+7");

    // a number to represent the current medicalUser and superadmin
    public static final int IDENTITY_CODE = 1;

    public static final String STORAGE_DIRECTORY = "storage";

}
