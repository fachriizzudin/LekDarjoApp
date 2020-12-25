package com.lazuardifachri.bps.lekdarjoapp.view;

import android.view.View;

import com.lazuardifachri.bps.lekdarjoapp.model.Indicator;

public interface IndicatorItemListener {
    void onIndicatorDownloadClick(View button, View progressBar, Indicator indicator);
    void checkIfFileExist(View v, Indicator indicator);
}
