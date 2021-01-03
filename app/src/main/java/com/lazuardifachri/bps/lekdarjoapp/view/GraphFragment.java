package com.lazuardifachri.bps.lekdarjoapp.view;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentGraphBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Graph;
import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.GraphViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GraphFragment extends Fragment {

    private FragmentGraphBinding binding;

    private GraphViewModel viewModel;

    public GraphFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentGraphBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(GraphViewModel.class);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Statistik Sidoarjo");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.refresh();

        observeViewModel();

        binding.economyGrowthCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToEconomy());
        });

        binding.ipmCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToIpm());
        });

        binding.pdrbKonstanCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToPdrbKonstan());
        });

        binding.morbidityCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToMorbidity());
        });

        binding.lifeExpectancyCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToLifeExpectancy());
        });

    }

    private String getLastYearData(List<Graph> graphList) {
        return String.valueOf(graphList.get(graphList.size()-1).getValue());
    }

    private boolean isIncrease(List<Graph> graphList) {
        double lastYear = graphList.get(graphList.size()-1).getValue();
        double secondLastYear = graphList.get(graphList.size()-2).getValue();
        return lastYear > secondLastYear;

    }

    private void observeViewModel() {

        // kesehatan/morbiditas
        viewModel.economyGrowthDataLive.observe(getViewLifecycleOwner(), graphData -> {

            Log.d("lastData", String.valueOf(getLastYearData(graphData.getData())));
            // get last year data from graphData
            binding.economyGrowthNewValue.setText(getLastYearData(graphData.getData()));

            // check if last year data increase or decrease
            if (isIncrease(graphData.getData())) {
                binding.upArrowEconomyGrowth.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowEconomyGrowth.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // angka harapan hidup
        viewModel.ipmDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.ipmNewValue.setText(getLastYearData(graphData.getData()));

            // check if last year data increase or decrease
            if (isIncrease(graphData.getData())) {
                binding.upArrowIpm.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowIpm.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // pdrb harga berlaku
        viewModel.pdrbKonstanDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.pdrbKonstanNewValue.setText(getLastYearData(graphData.getData()));

            // check if last year data increase or decrease
            if (isIncrease(graphData.getData())) {
                binding.upArrowPDRBHargaKonstan.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowPDRBHargaKonstan.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // pendidikan
        viewModel.morbidityDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.morbidityNewValue.setText(getLastYearData(graphData.getData()));

            // check if last year data increase or decrease
            if (isIncrease(graphData.getData())) {
                binding.upArrowMorbidity.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowMorbidity.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // pertumbuhan ekonomi
        viewModel.lifeExpectancyDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.lifeExpectancyNewValue.setText(getLastYearData(graphData.getData()));

            // check if last year data increase or decrease
            if (isIncrease(graphData.getData())) {
                binding.upArrowLifeExpectancy.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowLifeExpectancy.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        viewModel.counter.observe(getViewLifecycleOwner(), counter -> {
            if (counter == 5) {
                binding.horizontalProgressBar.setVisibility(View.GONE);
            } else if (counter < 5) {
                binding.horizontalProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }


}
