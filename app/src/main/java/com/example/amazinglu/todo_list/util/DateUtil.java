package com.example.amazinglu.todo_list.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public static String dateToString(Date date) {
        return dateFormat.format(date);
    }

    public static Date stringToDate(String str) {
        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {
            return Calendar.getInstance().getTime();
        }
    }
}
