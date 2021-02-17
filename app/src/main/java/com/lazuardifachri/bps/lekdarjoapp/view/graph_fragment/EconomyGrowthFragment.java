package com.lazuardifachri.bps.lekdarjoapp.view.graph_fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentGraphEconomyGrowthBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Graph;
import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;
import com.lazuardifachri.bps.lekdarjoapp.util.GraphUtil;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.GraphViewModel;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.math.BigDecimal;
import java.util.ArrayList;

public class EconomyGrowthFragment extends Fragment implements OnSeekChangeListener {

    private FragmentGraphEconomyGrowthBinding binding;

    private GraphViewModel viewModel;

    private LineChart lineChart;
    private IndicatorSeekBar xSeekBar;
    private IndicatorSeekBar ySeekBar;
    private GraphData graphData;

    public EconomyGrowthFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentGraphEconomyGrowthBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(GraphViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.refresh(1);

        xSeekBar = (IndicatorSeekBar) binding.xSeekBar;
        ySeekBar = (IndicatorSeekBar) binding.ySeekBar;

        // get chart view
        lineChart = binding.chart;

        lineChart = GraphUtil.setChart(lineChart);

        viewModel.economyGrowthDataLive.observe(getViewLifecycleOwner(), graphData -> {

            if (graphData != null) {

                binding.chart.setVisibility(View.VISIBLE);
                binding.seekBarSection1.setVisibility(View.VISIBLE);
                binding.seekBarSection2.setVisibility(View.VISIBLE);
                binding.descriptionSection.setVisibility(View.VISIBLE);

                this.graphData = graphData;

                String titleGraph = graphData.getMeta().getTitle() + "\n(" + graphData.getMeta().getVerticalUnit() + ")";

                binding.description.setText(graphData.getMeta().getDescription());
                binding.title.setText(titleGraph);

                int maxYear = graphData.getData().get(graphData.getData().size() - 1).getYear();
                int minYear = graphData.getData().get(0).getYear();

                xSeekBar.setOnSeekChangeListener(this);
                xSeekBar.setTickCount(graphData.getData().size());
                xSeekBar.setMax(maxYear);
                xSeekBar.setMin(minYear);
                xSeekBar.setProgress(minYear);

                ySeekBar.setOnSeekChangeListener(this);
                ySeekBar.setMax(3);
                ySeekBar.setMin(0.25f);
                ySeekBar.setProgress(2);

                lineChart = GraphUtil.setChartData(lineChart, graphData, minYear, 2);

            }

        });

        viewModel.counter.observe(getViewLifecycleOwner(), counter -> {
            if (counter == 1) {
                binding.loadingProgressBar.setVisibility(View.GONE);
            } else {
                binding.loadingProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onSeeking(SeekParams seekParams) {
        lineChart = GraphUtil.setChartData(lineChart, this.graphData, xSeekBar.getProgress(), ySeekBar.getProgressFloat());
    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

    }
}
