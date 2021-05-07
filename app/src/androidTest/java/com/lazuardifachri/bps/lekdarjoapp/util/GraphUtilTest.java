package com.lazuardifachri.bps.lekdarjoapp.util;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;

public class GraphUtilTest {

    @Test
    public void testSetChartData() {
        int bd = 2040215513;
        String numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH).format(bd);
        StringTokenizer token = new StringTokenizer(numberFormat,".");
        numberFormat = token.nextToken();
        System.out.println(numberFormat);
    }
}