package com.lazuardifachri.bps.lekdarjoapp.view.adapter;

import com.lazuardifachri.bps.lekdarjoapp.util.DateUtil;

public class DateObject implements ObjectList {
    private String date;

    public DateObject(String date) {
        this.date = DateUtil.getDateHeaderString(date);
    }

    public String getDate() {
        return date;
    }

    @Override
    public int getType() {
        return TYPE_DATE;
    }
}
