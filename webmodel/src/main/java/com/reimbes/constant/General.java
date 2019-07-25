package com.reimbes.constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class General {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX");
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT+7");

}
