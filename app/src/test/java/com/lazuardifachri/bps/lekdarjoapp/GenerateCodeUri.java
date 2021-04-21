package com.lazuardifachri.bps.lekdarjoapp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazuardifachri.bps.lekdarjoapp.model.Graph;
import com.lazuardifachri.bps.lekdarjoapp.util.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GenerateCodeUri {

    @Test
    public void getIntegerFromUrlTest() {

        String mydata = "https://lekdarjo.herokuapp.com/api/publications/files/28";
        String newUri = "https://sidoarjokab.bps.go.id/publication/download.html?nrbvfeve=OTgxZTI5NjkzOTQ4MjkxMjhhYmRmNmRh&xzmn=aHR0cHM6Ly9zaWRvYXJqb2thYi5icHMuZ28uaWQvcHVibGljYXRpb24vMjAyMS8wNC8wNS85ODFlMjk2OTM5NDgyOTEyOGFiZGY2ZGEvcHJvZHVrLWRvbWVzdGlrLXJlZ2lvbmFsLWJydXRvLWthYnVwYXRlbi1zaWRvYXJqby1tZW51cnV0LWxhcGFuZ2FuLXVzYWhhLTIwMTYtMjAyMC5odG1s&twoadfnoarfeauf=MjAyMS0wNC0xNiAxMTowNzowNQ%3D%3D";

        String uri2 = "https://sidoarjokab.bps.go.id/pressrelease/download.html?nrbvfeve=MjU%3D&sdfs=ldjfdifsdjkfahi&twoadfnoarfeauf=MjAyMS0wNC0xNiAyMTo0NjoxMA%3D%3D";
        String uri3 = "https://sidoarjokab.bps.go.id/publication/download.html?nrbvfeve=ZTNlMTBkODE5ZTBiYmZlZTUzNTNkYmJk&xzmn=aHR0cHM6Ly9zaWRvYXJqb2thYi5icHMuZ28uaWQvcHVibGljYXRpb24vMjAyMS8wMi8yNi9lM2UxMGQ4MTllMGJiZmVlNTM1M2RiYmQva2FidXBhdGVuLXNpZG9hcmpvLWRhbGFtLWFuZ2thLTIwMjEuaHRtbA%3D%3D&twoadfnoarfeauf=MjAyMS0wNC0xNiAyMTo1MTo1NA%3D%3D";
        String uri4 = "https://sidoarjokab.bps.go.id/publication/download.html?nrbvfeve=ZjY5ZGE4ZjIyN2MzYjI5OWJiNTEwYjk0&xzmn=aHR0cHM6Ly9zaWRvYXJqb2thYi5icHMuZ28uaWQvcHVibGljYXRpb24vMjAxOC8wOC8yMC9mNjlkYThmMjI3YzNiMjk5YmI1MTBiOTQva2FidXBhdGVuLXNpZG9hcmpvLWRhbGFtLWFuZ2thLTIwMTguaHRtbA%3D%3D&twoadfnoarfeauf=MjAyMS0wNC0xNiAyMjoxNjoyNA%3D%3D";
        String uri5 = "https://sidoarjokab.bps.go.id/publication/downloadapi.html?data=OA0%2FZ1yhyh64b808NTN5eDg5H3QVzECRgosAKQ%2FNOnP2eA1JE2rLbeGoGf3LXL%2BYAbalasYqV0deMyn8Tmwl9BVOUGuUdSUmhK%2BVkBWqgfE%3D&tokenuser=";

        String tarik = "https://sidoarjokab.bps.go.id/publication/download.html?nrbvfeve=ZjM3MzI4YzRlNWMwMmM3ODllZGRhMTA4&xzmn=aHR0cHM6Ly9zaWRvYXJqb2thYi5icHMuZ28uaWQvcHVibGljYXRpb24vMjAyMC8wOS8yOC9mMzczMjhjNGU1YzAyYzc4OWVkZGExMDgva2VjYW1hdGFuLXRhcmlrLWRhbGFtLWFuZ2thLTIwMjAuaHRtbA%3D%3D&twoadfnoarfeauf=";
        String tarik2 = "https://sidoarjokab.bps.go.id/publication/download.html?nrbvfeve=ZjM3MzI4YzRlNWMwMmM3ODllZGRhMTA4&xzmn=aHR0cHM6Ly9zaWRvYXJqb2thYi5icHMuZ28uaWQvcHVibGljYXRpb24vMjAyMC8wOS8yOC9mMzczMjhjNGU1YzAyYzc4OWVkZGExMDgva2VjYW1hdGFuLXRhcmlrLWRhbGFtLWFuZ2thLTIwMjAuaHRtbA%3D%3D&twoadfnoarfeauf=";

        String[] uriArray = uri3.split("=");
        String[] uriArrayCleaned = Arrays.copyOf(uriArray, uriArray.length -1);

        String code = StringUtil.generateFileIdFromUri(uri5);

        System.out.println(code);

        assertEquals(tarik, tarik2);

    }

}