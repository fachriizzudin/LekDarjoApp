package com.lazuardifachri.bps.lekdarjoapp.view.listener;

import android.view.View;

import com.lazuardifachri.bps.lekdarjoapp.model.Indicator;
import com.lazuardifachri.bps.lekdarjoapp.model.Infographic;

public interface InfographicItemListener {
    void onInfographicDownloadClick(View button, View progressBar, Infographic infographic);
    void checkIfFileExist(View v, Infographic infographic);
}
