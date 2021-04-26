package com.lazuardifachri.bps.lekdarjoapp.view.detail_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentGraphDetailBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Graph;
import com.lazuardifachri.bps.lekdarjoapp.util.GraphUtil;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.GraphDetailViewModel;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class GraphDetailFragment extends Fragment implements OnSeekChangeListener {

    private FragmentGraphDetailBinding binding;
    private GraphDetailViewModel viewModel;

    private int uuid;
    private int graphType;
    private int dataType;

    private LineChart lineChart;
    private IndicatorSeekBar xSeekBar;
    private IndicatorSeekBar ySeekBar;
    private Graph graph;

    public GraphDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGraphDetailBinding.inflate(inflater, container, false);
        xSeekBar = (IndicatorSeekBar) binding.xSeekBar;
        ySeekBar = (IndicatorSeekBar) binding.ySeekBar;
        lineChart = binding.chart;
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(GraphDetailViewModel.class);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            uuid = InfographicDetailFragmentArgs.fromBundle(getArguments()).getUuid();
            viewModel.fetchByUuidFromDatabase(uuid);
            observeViewModel();
        }
    }

    private void observeViewModel() {
        viewModel.graphLive.observe(getViewLifecycleOwner(), graph -> {
            if (graph != null && getContext() != null) {
                binding.setGraph(graph);
                graphType = graph.getMeta().getGraphType();
                dataType = graph.getMeta().getDataType();
                lineChart = GraphUtil.setChart(lineChart,dataType);
            }
        });
        viewModel.graphLive.observe(getViewLifecycleOwner(), graph -> {

            if (graph != null) {

                binding.chart.setVisibility(View.VISIBLE);
                binding.seekBarSection1.setVisibility(View.VISIBLE);
                binding.seekBarSection2.setVisibility(View.VISIBLE);
                binding.descriptionSection.setVisibility(View.VISIBLE);

                this.graph = graph;

                String verticalUnit = "(" + graph.getMeta().getVerticalUnit() + ")";

                binding.description.setText(graph.getMeta().getDescription());
                binding.unit.setText(verticalUnit);

                int maxYear = graph.getData().get(graph.getData().size() - 1).getYear();
                int minYear = graph.getData().get(0).getYear();

                xSeekBar.setOnSeekChangeListener(this);
                xSeekBar.setTickCount(graph.getData().size());
                xSeekBar.setMax(maxYear);
                xSeekBar.setMin(minYear);
                xSeekBar.setProgress(minYear);

                ySeekBar.setOnSeekChangeListener(this);
                ySeekBar.setMax(3);
                ySeekBar.setMin(0.25f);
                ySeekBar.setProgress(2);

                lineChart = GraphUtil.setChartData(lineChart, graph, minYear, 2, dataType);
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
        lineChart = GraphUtil.setChartData(lineChart, this.graph, xSeekBar.getProgress(), ySeekBar.getProgressFloat(), dataType);
    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

    }
}

