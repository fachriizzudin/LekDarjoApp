
package com.lazuardifachri.bps.lekdarjoapp.util;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.lazuardifachri.bps.lekdarjoapp.model.Graph;
import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;
import android.graphics.Typeface;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

public class GraphUtil {

    public static LineChart setChartData(LineChart lineChart, GraphData graphData, int seekBarX, double seekBarY, int type) {
        Log.d("setchartdata", "masuk");
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

            if (type == 1) {
                Log.d("setchartdata type", String.valueOf(type));
                data.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        BigDecimal bd = new BigDecimal(Float.toString(value));
                        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        return String.valueOf(bd.floatValue());
                    }
                });
            } else if (type == 3) {
                Log.d("setchartdata type", String.valueOf(type));
                Log.d("type", String.valueOf(type));
                data.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.format("%.2f",value/1000000.0);
                        //return String.valueOf(bd.floatValue());
                    }
                });
            }

            // set data
            lineChart.setData(data);

        }

        lineChart.animateX(1000);

        lineChart.invalidate();

        return lineChart;

    }

    public static LineChart setChart(LineChart lineChart, int type) {
        Log.d("setchart", "masuk");
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

        if (type == 1) {
            Log.d("setchart type", String.valueOf(type));
            xAxis.setValueFormatter(new ValueFormatter() {
                @SuppressLint("DefaultLocale")
                @Override
                public String getFormattedValue(float value) {
                    BigDecimal bd = new BigDecimal(Float.toString(value));
                    bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
                    return String.format("%.0f", bd.floatValue());
                    }
            });
        } else if (type == 3) {
            Log.d("setchart type", String.valueOf(type));
            xAxis.setValueFormatter(new ValueFormatter() {
                @SuppressLint("DefaultLocale")
                @Override
                public String getFormattedValue(float value) {
                    return String.format("%.2f",value/1000000.0);
                }
            });
        }

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

    public static BarChart setChartDataDouble(BarChart barChart, GraphData graphData1, GraphData graphData2, int seekBarX, double seekBarY) {

        float groupSpace = 0.12f;
        float barSpace = 0.4f; // x4 DataSet
        float barWidth = 0.06f; // x4 DataSet
        // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"
        int groupCount = seekBarX + 1;

        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();

        double sum1 = 0;
        int counter1 = 0;

        for (Graph data : graphData1.getData()) {
            if (data.getYear() >= seekBarX) {
                counter1++;
                values1.add(new BarEntry(data.getYear(), (float) data.getValue(), 2));
                sum1 = (sum1 + data.getValue());
            }
        }

        double sum2 = 0;

        for (Graph data : graphData2.getData()) {
            if (data.getYear() >= seekBarX) {
                values2.add(new BarEntry(data.getYear(), (float) data.getValue(), 2));
                sum2 = (sum2 + data.getValue());
            }
        }

        barChart.getAxisLeft().setAxisMaximum((float) ((sum1 + sum2) * seekBarY/ counter1 / 2 ));

        int minYear = (int) values1.get(0).getX();

        barChart.getXAxis().setAxisMaximum(groupCount);

        barChart.getXAxis().setAxisMinimum(minYear);

        BarDataSet dataSet1;
        BarDataSet dataSet2;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            dataSet1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            dataSet1.setValues(values1);
            dataSet1.notifyDataSetChanged();

            dataSet2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
            dataSet2.setValues(values2);
            dataSet2.notifyDataSetChanged();

            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            dataSet1 = new BarDataSet(values1, "Laki-laki");
            dataSet1.setDrawIcons(false);
            dataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet1.setColor(Color.rgb(164, 228, 251));

            dataSet2 = new BarDataSet(values2, "Perempuan");
            dataSet2.setDrawIcons(false);
            dataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet2.setColor(Color.rgb(104, 241, 175));

            // create a data object with the data sets
            BarData data1 = new BarData(dataSet1, dataSet2);
            data1.setValueTextColor(Color.BLACK);
            data1.setValueTextSize(10f);
            data1.setBarWidth(barWidth);
            data1.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    BigDecimal bd = new BigDecimal(Float.toString(value));
                    bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
                    return String.valueOf(bd.intValue());
                }
            });

            // set data
            barChart.setData(data1);

        }

        barChart.groupBars(minYear, groupSpace, barSpace);
        barChart.getXAxis().setAxisMinimum(minYear-1);
        barChart.getXAxis().setAxisMaximum(groupCount +1);

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
        l.setEnabled(true);

        // x Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setCenterAxisLabels(true);
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

    public static PieChart setChartData(PieChart pieChart, GraphData graphData1, GraphData graphData2, int seekBarX) {
        ArrayList<PieEntry> values = new ArrayList<>();

        double male = 0;
        double female = 0;

        for (Graph data : graphData1.getData()) {
            if (data.getYear() == seekBarX) {
                values.add(new PieEntry((int) data.getValue(), "Laki-laki", 2));
                male = data.getValue();
            }
        }

        for (Graph data : graphData2.getData()) {
            if (data.getYear() == seekBarX) {
                values.add(new PieEntry((int) data.getValue(), "Perempuan", 2));
                female = data.getValue();
            }
        }

        float sexRatioFloat = (float) (male * 100 /female);
        String sexRatio = String.valueOf(roundValue(sexRatioFloat, 2));

        PieDataSet dataSet;

        if (pieChart.getData() != null && pieChart.getData().getDataSetCount() > 0) {
            dataSet = (PieDataSet) pieChart.getData().getDataSetByIndex(0);
            dataSet.setValues(values);
            dataSet.notifyDataSetChanged();

            pieChart.getData().notifyDataChanged();
            pieChart.notifyDataSetChanged();
        } else {
            dataSet = new PieDataSet(values, "Jumlah Penduduk");

            dataSet.setDrawIcons(false);
            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);

            ArrayList<Integer> colors = new ArrayList<>();

            colors.add(Color.rgb(0,191,255));
            colors.add(Color.rgb(255,105,180));

            dataSet.setColors(colors);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new ValueFormatter() {
                @SuppressLint("DefaultLocale")
                @Override
                public String getPieLabel(float value, PieEntry pieEntry) {
                    return String.format(Locale.US, "%,d",roundValue(value,0).intValue()).replace(",",".");
                }
            });
            data.setValueTextSize(13f);
            data.setValueTextColor(Color.BLACK);
            pieChart.setData(data);
        }

        pieChart.setCenterText(generateCenterSpannableText(sexRatio));

        pieChart.invalidate();

        return pieChart;
    }

    public static PieChart setChart(PieChart pieChart) {

        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setCenterText("Center Text");

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(60);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(13f);

        return pieChart;
    }

    private static SpannableString generateCenterSpannableText(String sexRatio) {
        SpannableString s = new SpannableString("Rasio Jenis Kelamin\n" + sexRatio);
        s.setSpan(new RelativeSizeSpan(1.2f), 0, 19, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, 19, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 19, 0);
        s.setSpan(new RelativeSizeSpan(2.1f), 20, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 20, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 20, s.length(), 0);
        return s;
    }

    private static BigDecimal roundValue(float value, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }
}
