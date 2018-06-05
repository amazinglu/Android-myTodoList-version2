package com.example.amazinglu.todo_list.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm", Locale.getDefault());
    private static DateFormat dateFormatDateOnly = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private static DateFormat dateFormatTimeOnly = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static String wholeDateToString(Date date) {
        if (date == null) {
            return null;
        }
        return dateFormat.format(date);
    }
    public static String TimeToString(Date date) { return dateFormatTimeOnly.format(date); }
    public static String dateToString(Date date) { return dateFormatDateOnly.format(date); }

    public static Date stringToDate(String str) {
        try {
            if (str == null) {
                return null;
            }
            return dateFormat.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getEarliestDay() {
        return new Date(Long.MIN_VALUE);
    }
}
