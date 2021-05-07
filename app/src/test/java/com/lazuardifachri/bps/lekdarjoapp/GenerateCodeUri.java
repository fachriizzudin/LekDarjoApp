package com.lazuardifachri.bps.lekdarjoapp;

import com.lazuardifachri.bps.lekdarjoapp.util.StringUtil;

import org.junit.Test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GenerateCodeUri {

    @Test
    public void getIntegerFromUrlTest() {
        String uri1 = "https://sidoarjokab.bps.go.id/publication/getImageCover.html?url=MjAyMS0wNS0wNCMjaHR0cHM6Ly9wb3J0YWxwdWJsaWthc2kuYnBzLmdvLmlkL2FwaS9nZXRLb3Zlci5waHA%2Fc2VsZWN0b3I9YWUzNDdjNGUyMTQ1NjZmYWM3Nzk3OTE1";
        String uri2 = "https://sidoarjokab.bps.go.id/publication/getImageCover.html?url=MjAyMS0wNC0yNSMjaHR0cHM6Ly9wb3J0YWxwdWJsaWthc2kuYnBzLmdvLmlkL2FwaS9nZXRLb3Zlci5waHA%2Fc2VsZWN0b3I9YWUzNDdjNGUyMTQ1NjZmYWM3Nzk3OTE1";

        assertEquals(uri1, uri2);
    }

}