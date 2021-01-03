package com.lazuardifachri.bps.lekdarjoapp.util;

import android.annotation.SuppressLint;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.lazuardifachri.bps.lekdarjoapp.model.Graph;
import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GraphUtil {

    public static LineChart setChartData(LineChart lineChart, GraphData graphData, int seekBarX, double seekBarY) {

        ArrayList<Entry> values = new ArrayList<>();

        double sum = 0;
        int counter = 0;

        for (Graph data : graphData.getData()) {
            if (data.getYear() >= seekBarX) {
                counter++;
                values.add(new Entry(data.getYear(), (float) data.getValue(), 2));
                sum = (sum + data.getValue());
            }
        }

        lineChart.getAxisLeft().setAxisMaximum((float) (sum / counter * seekBarY));

        int maxYear = (int) values.get(values.size() - 1).getX();
        int minYear = (int) values.get(0).getX();

        lineChart.getXAxis().setAxisMaximum(maxYear+1);
        lineChart.getXAxis().setAxisMinimum(minYear-1);

        LineDataSet dataSet;

        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            dataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            dataSet.setValues(values);
            dataSet.notifyDataSetChanged();
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            dataSet = new LineDataSet(values, "DataSet");
            dataSet.setDrawIcons(false);
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setLineWidth(2.5f);
            dataSet.setColor(Color.BLACK);
            dataSet.setCircleColor(Color.BLACK);
            dataSet.setDrawCircleHole(true);

            // create a data object with the data sets
            LineData data = new LineData(dataSet);
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(15f);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    BigDecimal bd = new BigDecimal(Float.toString(value));
                    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                    return String.valueOf(bd.floatValue());
                }
            });

            // set data
            lineChart.setData(data);

        }

        lineChart.animateX(1000);

        lineChart.invalidate();

        return lineChart;

    }

    public static LineChart setChart(LineChart lineChart) {
        lineChart.setBackgroundColor(Color.WHITE);

        // no description text
        lineChart.getDescription().setEnabled(false);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        lineChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);

        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();
        l.setEnabled(false);

        // x Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @SuppressLint("DefaultLocale")
            @Override
            public String getFormattedValue(float value) {
                BigDecimal bd = new BigDecimal(Float.toString(value));
                bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
                return String.format("%.0f", bd.floatValue());
            }
        });

        // left y Axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);

        leftAxis.setTextColor(Color.BLACK);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        return lineChart;
    }

    public static BarChart setChartData(BarChart barChart, GraphData graphData, int seekBarX, double seekBarY) {

        ArrayList<BarEntry> values = new ArrayList<>();

        double sum = 0;
        int counter = 0;

        for (Graph data : graphData.getData()) {
            if (data.getYear() >= seekBarX) {
                counter++;
                values.add(new BarEntry(data.getYear(), (float) data.getValue(), 2));
                sum = (sum + data.getValue());
            }
        }

        barChart.getAxisLeft().setAxisMaximum((float) (sum / counter * seekBarY));

        int maxYear = (int) values.get(values.size() - 1).getX();
        int minYear = (int) values.get(0).getX();

        barChart.getXAxis().setAxisMaximum(maxYear+1);
        barChart.getXAxis().setAxisMinimum(minYear-1);

        BarDataSet dataSet;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            dataSet = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            dataSet.setValues(values);
            dataSet.notifyDataSetChanged();
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            dataSet = new BarDataSet(values, "DataSet");
            dataSet.setDrawIcons(false);
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setColor(Color.BLACK);

            // create a data object with the data sets
            BarData data = new BarData(dataSet);
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(15f);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    BigDecimal bd = new BigDecimal(Float.toString(value));
                    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                    return String.valueOf(bd.floatValue());
                }
            });

            // set data
            barChart.setData(data);

        }

        barChart.animateX(1000);

        barChart.invalidate();

        return barChart;

    }

    public static BarChart setChart(BarChart barChart) {
        barChart.setBackgroundColor(Color.WHITE);

        // no description text
        barChart.getDescription().setEnabled(false);

        // enable touch gestures
        barChart.setTouchEnabled(true);

        barChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setDrawGridBackground(false);
        barChart.setHighlightPerDragEnabled(true);

        // get the legend (only possible after setting data)
        Legend l = barChart.getLegend();
        l.setEnabled(false);

        // x Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @SuppressLint("DefaultLocale")
            @Override
            public String getFormattedValue(float value) {
                BigDecimal bd = new BigDecimal(Float.toString(value));
                bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
                return String.format("%.0f", bd.floatValue());
            }
        });

        // left y Axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);

        leftAxis.setTextColor(Color.BLACK);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        return barChart;
    }
}
