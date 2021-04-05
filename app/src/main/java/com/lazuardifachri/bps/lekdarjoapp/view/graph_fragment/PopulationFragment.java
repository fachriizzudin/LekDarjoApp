package com.lazuardifachri.bps.lekdarjoapp.view.graph_fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieDataSet;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentGraphPopulationBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;
import com.lazuardifachri.bps.lekdarjoapp.util.GraphUtil;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.GraphViewModel;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class PopulationFragment extends Fragment implements OnSeekChangeListener {

    private FragmentGraphPopulationBinding binding;

    private GraphViewModel viewModel;

    private PieChart pieChart;
    private IndicatorSeekBar xSeekBar;
    private IndicatorSeekBar ySeekBar;

    private GraphData maleGraphData;
    private GraphData femaleGraphData;

    public PopulationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentGraphPopulationBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(GraphViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.refresh(7);
        viewModel.refresh(8);

        xSeekBar = (IndicatorSeekBar) binding.xSeekBar;

        // get chart view
        pieChart = binding.chart;

        pieChart = GraphUtil.setChart(pieChart);

        viewModel.mixPopulationDataLive.observe(getViewLifecycleOwner(), graphDataList -> {
            if (!graphDataList.isEmpty()) {

                binding.chart.setVisibility(View.VISIBLE);
                binding.seekBarSection.setVisibility(View.VISIBLE);
                binding.descriptionSection.setVisibility(View.VISIBLE);

                Log.d("graph", graphDataList.get(1).getMeta().getVerticalUnit());

                String titleGraph = graphDataList.get(0).getMeta().getTitle() + " (" + graphDataList.get(1).getMeta().getVerticalUnit() + ")";

                binding.title.setText(titleGraph);

                binding.description.setText(graphDataList.get(0).getMeta().getDescription());

                GraphData maleGraphData = graphDataList.get(0);
                GraphData femaleGraphData = graphDataList.get(1);
                this.maleGraphData = maleGraphData;
                this.femaleGraphData = femaleGraphData;

                int maxYear = maleGraphData.getData().get(maleGraphData.getData().size() - 1).getYear();
                int minYear = maleGraphData.getData().get(0).getYear();

                xSeekBar.setOnSeekChangeListener(this);
                xSeekBar.setTickCount(maleGraphData.getData().size());
                xSeekBar.setMax(maxYear);
                xSeekBar.setMin(minYear);
                xSeekBar.setProgress(maxYear);

                pieChart = GraphUtil.setChartData(pieChart, maleGraphData, femaleGraphData, maxYear);
            }
        });

        viewModel.counter.observe(getViewLifecycleOwner(), counter -> {
            if (counter == 2) {
                binding.loadingProgressBar.setVisibility(View.GONE);
            } else {
                binding.loadingProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onSeeking(SeekParams seekParams) {
        pieChart = GraphUtil.setChartData(pieChart, this.maleGraphData, this.femaleGraphData, xSeekBar.getProgress());
    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

    }
}

