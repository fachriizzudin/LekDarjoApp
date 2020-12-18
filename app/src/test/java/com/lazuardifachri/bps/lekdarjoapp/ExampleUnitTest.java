package com.lazuardifachri.bps.lekdarjoapp;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void getFileCodeFromUrlTest() {

        String mydata = "https://lekdarjo.herokuapp.com/api/publications/files/28";
        Pattern pattern = Pattern.compile("[^files/]*$");
        Matcher matcher = pattern.matcher(mydata);
        if (matcher.find())
        {
            System.out.println(matcher.group());
        }

        assertEquals(Integer.parseInt(matcher.group()), 28);

    }

    private int getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("dd-MM-yyyy").parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        return month;
    }

    public String theMonth(int month){
        System.out.println(month);
        String[] monthNames = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Augustus", "September", "Oktober", "November", "Desember"};
        System.out.println(monthNames[0]);
        return monthNames[month];
    }

    @Test
    public void getTrimmedTitle() {

        String mydata = "Kecamatan Sidoarjo Dalam Angka 2020";
        mydata.trim();
        System.out.println(mydata.trim());
    }

    @Test
    public void getDateHeader() throws ParseException {

        String mydata = "03-06-2020";
        int monthInt = getMonth(mydata);
        System.out.println(monthInt);
        String monthString = theMonth(monthInt);
        System.out.println(monthString);
    }
}