package com.lazuardifachri.bps.lekdarjoapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static int getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("dd-MM-yyyy").parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int month = cal.get(Calendar.MONTH);
        return month;
    }

    public static String theMonth(int month){
        String[] monthNames = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Augustus", "September", "Oktober", "November", "Desember"};
        return monthNames[month];
    }

    public static int getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("dd-MM-yyyy").parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int year = cal.get(Calendar.YEAR);
        return year;
    }

    public static String getDateHeaderString(String date) {
        String month = null;
        try {
            month = theMonth(getMonth(date));
            return month + ", " + getYear(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
