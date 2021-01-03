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
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentGraphLifeExpectancyBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;
import com.lazuardifachri.bps.lekdarjoapp.util.GraphUtil;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.GraphViewModel;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class LifeExpectancyFragment extends Fragment implements OnSeekChangeListener {

    private FragmentGraphLifeExpectancyBinding binding;

    private GraphViewModel viewModel;

    private LineChart lineChart;
    private IndicatorSeekBar xSeekBar;
    private IndicatorSeekBar ySeekBar;
    private GraphData graphData;

    public LifeExpectancyFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentGraphLifeExpectancyBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(GraphViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.refresh(5);

        xSeekBar = (IndicatorSeekBar) binding.xSeekBar;
        ySeekBar = (IndicatorSeekBar) binding.ySeekBar;

        // get chart view
        lineChart = binding.chart;

        lineChart = GraphUtil.setChart(lineChart);

        viewModel.lifeExpectancyDataLive.observe(getViewLifecycleOwner(), graphData -> {

            if (graphData != null) {

                this.graphData = graphData;
                binding.description.setText(graphData.getMeta().getDescription());
                binding.title.setText(graphData.getMeta().getTitle());

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
