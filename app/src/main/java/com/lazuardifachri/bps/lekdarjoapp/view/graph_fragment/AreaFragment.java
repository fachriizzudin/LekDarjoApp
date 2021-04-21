package com.lazuardifachri.bps.lekdarjoapp.view.graph_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentGraphPovertyBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;
import com.lazuardifachri.bps.lekdarjoapp.util.GraphUtil;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.GraphViewModel;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class AreaFragment extends Fragment implements OnSeekChangeListener {

    private FragmentGraphPovertyBinding binding;

    private GraphViewModel viewModel;

    private LineChart lineChart;
    private IndicatorSeekBar xSeekBar;
    private IndicatorSeekBar ySeekBar;
    private GraphData graphData;

    public AreaFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentGraphPovertyBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(GraphViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.refresh(4);

        xSeekBar = (IndicatorSeekBar) binding.xSeekBar;
        ySeekBar = (IndicatorSeekBar) binding.ySeekBar;

        // get chart view
        lineChart = binding.chart;

        lineChart = GraphUtil.setChart(lineChart, 2);

        viewModel.povertyDataLive.observe(getViewLifecycleOwner(), graphData -> {

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

                lineChart = GraphUtil.setChartData(lineChart, graphData, minYear, 2, 1);

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
        lineChart = GraphUtil.setChartData(lineChart, this.graphData, xSeekBar.getProgress(), ySeekBar.getProgressFloat(), 1);
    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

    }
}