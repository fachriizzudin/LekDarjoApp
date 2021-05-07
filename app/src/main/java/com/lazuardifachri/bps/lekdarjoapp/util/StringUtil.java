package com.lazuardifachri.bps.lekdarjoapp.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String capitalizeWord(String str){
        String[] words =str.split("\\s");
        StringBuilder capitalizeWord= new StringBuilder();
        for(String w:words){
            String first=w.substring(0,1);
            String afterfirst=w.substring(1);
            capitalizeWord.append(first.toUpperCase()).append(afterfirst).append(" ");
        }
        return capitalizeWord.toString().trim();
    }

    public static String getFileIdFromUri(String documentUri) {
        // Pattern pattern = Pattern.compile("[^files/]*$");
        // Matcher matcher = pattern.matcher(documentUri);
        // if (matcher.find()) return matcher.group();
        // return null;

        String str = documentUri.replaceAll("[^\\d]", " ");
        str = str.trim();
        str = str.replaceAll(" +", "");

        if (str.equals(""))
            return null;

        return str;

    }

    public static String generateFileIdFromUri(String documentUri) {
        // Replacing every non-digit number
        // with a space(" ")
        String str = documentUri.replaceAll("[^\\d]", " ");

        // Remove extra spaces from the beginning
        // and the ending of the string
        str = str.trim();

        // Replace all the consecutive white
        // spaces with a single space
        str = str.replaceAll(" +", "");

        if (str.equals(""))
            return null;

        return str;
    }

    public static String formatGraphInt(int value) {
        String numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH).format(value);
        StringTokenizer token = new StringTokenizer(numberFormat,".");
        numberFormat = token.nextToken();
        return numberFormat.replace(",", ".");
    }

    public static String formatGraphDouble(double value) {
        String decimalFormat = DecimalFormat.getNumberInstance(Locale.ENGLISH).format(value);
        StringTokenizer tokenDecimal = new StringTokenizer(decimalFormat,",");
        decimalFormat = tokenDecimal.nextToken();
        return decimalFormat.replace(".", ",");
    }
}
