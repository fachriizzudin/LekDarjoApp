package com.lazuardifachri.bps.lekdarjoapp.util;

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

    public static Integer getFileIdFromUri(String documentUri) {
        Pattern pattern = Pattern.compile("[^files/]*$");
        Matcher matcher = pattern.matcher(documentUri);
        if (matcher.find()) return Integer.parseInt(matcher.group());
        return null;
    }
}
