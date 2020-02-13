package com.reimbes.constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class General {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final DateFormat DATE_FORMAT_2 = new SimpleDateFormat("dd/MM/yyyy");
    public static final String DEFAULT_PLACE = "Jakarta";
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT+7");

    // a number to represent the current medicalUser and superadmin
    public static final int IDENTITY_CODE = 1;

    // reimbursement type
    public static final String PARKING_VALUE = "parking";
    public static final String FUEL_VALUE = "fuel";
    public static final String MEDICAL_VALUE = "medical";

    public static final long NULL_USER_ID_CODE = 0;
    public static final long DEFAULT_LONG_VALUE = 0;
    public static final long FAMILY_MEMBER_LIMIT = 4;

    public static final String NULL_NAME = "NULL_NAME";
    public static final String NULL_ROLE = "NULL_ROLE";
    public static final String NULL_GENDER = "NULL_GENDER";
    public static final String NULL_DATE_OF_BIRTH = "NULL_DATE_OF_BIRTH";
    public static final String NULL_RELATIONSHIP = "NULL_RELATIONSHIP";
    public static final String UNIQUENESS_NAME = "UNIQUENESS_NAME";
    public static final String FORBIDDEN_DUPLICATE_IMAGE = "FORBIDDEN_DUPLICATE_IMAGE";
    public static final String INVALID_IMAGE_PATH = "INVALID_IMAGE_PATH";
    public static final String NULL_CATEGORY = "NULL_CATEGORY";
    public static final String ZERO_FUEL_LITERS = "ZERO_FUEL_LITERS";
    public static final String ZERO_AMOUNT = "ZERO_AMOUNT";
    public static final String NULL_DATE = "NULL_DATE";


}
