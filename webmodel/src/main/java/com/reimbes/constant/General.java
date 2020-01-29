package com.reimbes.constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class General {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final DateFormat DATE_FORMAT_CLEAN = new SimpleDateFormat("dd MM yyyy");
    public static final String DEFAULT_PLACE = "Jakarta";
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT+7");

    // a number to represent the current medicalUser and superadmin
    public static final int IDENTITY_CODE = 1;

    // reimbursement type
    public static final String PARKING_VALUE = "parking";
    public static final String FUEL_VALUE = "fuel";
    public static final String MEDICAL_VALUE = "medical";

    public static final long NULL_USER_ID_CODE = new Long(0);
    public static final long DEFAULT_LONG_VALUE = new Long(0);

    public static final String MEDIA_TYPE_JPG = "image/jpg";
    public static final String MEDIA_TYPE_JPEG = "image/jpeg";
    public static final String MEDIA_TYPE_PNG = "image/png";


}
