package com.lazuardifachri.bps.lekdarjoapp.view.adapter;

import com.lazuardifachri.bps.lekdarjoapp.model.StatisticalNews;

public class StatisticalNewsObject implements ObjectList {

    private StatisticalNews statisticalNews;

    public StatisticalNews getStatisticalNewsModel() {
        return statisticalNews;
    }

    public void setStatisticalNewsModel(StatisticalNews statisticalNews) {
        this.statisticalNews = statisticalNews;
    }

    @Override
    public int getType() {
        return TYPE_OBJECT;
    }
}
