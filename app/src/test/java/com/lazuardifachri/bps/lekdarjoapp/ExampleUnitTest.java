package com.lazuardifachri.bps.lekdarjoapp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;
import com.lazuardifachri.bps.lekdarjoapp.util.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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

    @Test
    public void getIntegerFromUrlTest() {

        String mydata = "https://lekdarjo.herokuapp.com/api/publications/files/28";
        String code = StringUtil.generateFileIdFromUri(mydata);
        System.out.println(code);

    }

    @Test
    public void getFeatureFromUrlTest() {

        String mydata = "https://lekdarjo.herokuapp.com/api/publications/files/28";
        System.out.println(StringUtils.substringBetween(mydata, "api/", "/"));


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

    @Test
    public void setTotalPopulation() {
        int population = 2266533;
        String populationString = String.format("%.2f Juta", population/ 1000000.0);
        System.out.println(populationString);

    }

    @Test
    public void getExcelFormat() {
        String filename = "/documents/PDRBKabupatenSidoarjoAtasDasarHargaKonstanMenurutLapanganUsaha(2010-2020).xls";
        System.out.println("excel filename");
        System.out.println(filename.substring(filename.lastIndexOf(".") + 1));
    }

    @Test
    public void getHighestValue() {

        String json = "[\n" +
                "    {\n" +
                "      \"id\": 7,\n" +
                "      \"value\": 77.43,\n" +
                "      \"year\": 2015\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 8,\n" +
                "      \"value\": 78.17,\n" +
                "      \"year\": 2016\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 9,\n" +
                "      \"value\": 78.7,\n" +
                "      \"year\": 2017\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 10,\n" +
                "      \"value\": 79.51,\n" +
                "      \"year\": 2018\n" +
                "    }\n" +
                "  ]";

        Gson gson = new Gson();

        ArrayList<GraphData> data = gson.fromJson(json, new TypeToken<List<GraphData>>(){}.getType());

        GraphData lastData = Collections.max(data, Comparator.comparingInt(GraphData::getYear));

        System.out.println(lastData.getYear());

    }
}