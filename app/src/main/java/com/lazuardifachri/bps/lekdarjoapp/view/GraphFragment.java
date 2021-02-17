package com.lazuardifachri.bps.lekdarjoapp.view;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

        setHasOptionsMenu(true);

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

        binding.totalPopulationCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToPopulation());
        });

        binding.povertyCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToPoverty());
        });

        binding.unemploymentCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToUnemployment());
        });

        binding.riceProductionCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToRiceProduction());
        });

        binding.riceProductivityCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToRiceProductivity());
        });

        binding.ipmCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToIpm());
        });
    }

    private String getLastYear(List<Graph> graphList) {
        return String.valueOf(graphList.get(graphList.size()-1).getYear());
    }

    private String getLastYearData(List<Graph> graphList) {
        return String.valueOf(graphList.get(graphList.size()-1).getValue());
    }

    private boolean isIncrease(List<Graph> graphList) {
        double lastYear = graphList.get(graphList.size()-1).getValue();
        double secondLastYear = graphList.get(graphList.size()-2).getValue();
        return lastYear > secondLastYear;

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void observeViewModel() {
        // economy growth
        viewModel.economyGrowthDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearEconomyGrowth.setText(getLastYear(graphData.getData()));
            binding.economyGrowthNewValue.setText(getLastYearData(graphData.getData()));
            if (isIncrease(graphData.getData())) {
                binding.upArrowEconomyGrowth.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowEconomyGrowth.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // population
        viewModel.populationDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearTotalPopulation.setText(getLastYear(graphData.getData()));
            int value = (int) graphData.getData().get(graphData.getData().size()-1).getValue();
            @SuppressLint("DefaultLocale") String valueString = String.format("%.2f", value/1000000.0);
            binding.totalPopulationNewValue.setText(valueString);
            if (isIncrease(graphData.getData())) {
                binding.upArrowTotalPopulation.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowTotalPopulation.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }

        });

        // poverty
        viewModel.povertyDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearPoverty.setText(getLastYear(graphData.getData()));
            binding.povertyNewValue.setText(getLastYearData(graphData.getData()));
            if (isIncrease(graphData.getData())) {
                binding.upArrowPoverty.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowPoverty.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // unemployment
        viewModel.unemploymentDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearUnemployment.setText(getLastYear(graphData.getData()));
            binding.unemploymentNewValue.setText(getLastYearData(graphData.getData()));
            if (isIncrease(graphData.getData())) {
                binding.upArrowUnemployment.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowUnemployment.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // ipm
        viewModel.ipmDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearIpm.setText(getLastYear(graphData.getData()));
            binding.ipmNewValue.setText(getLastYearData(graphData.getData()));
            if (isIncrease(graphData.getData())) {
                binding.upArrowIpm.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowIpm.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // rice production
        viewModel.riceProductionDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearRiceProduction.setText(getLastYear(graphData.getData()));
            binding.riceProductionNewValue.setText(getLastYearData(graphData.getData()));
            if (isIncrease(graphData.getData())) {
                binding.upArrowRiceProduction.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowRiceProduction.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // rice productivity
        viewModel.riceProductivityDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearRiceProductivity.setText(getLastYear(graphData.getData()));
            binding.riceProductivityNewValue.setText(getLastYearData(graphData.getData()));
            if (isIncrease(graphData.getData())) {
                binding.upArrowRiceProductivity.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowRiceProductivity.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        viewModel.counter.observe(getViewLifecycleOwner(), counter -> {
            if (counter == 8) {
                binding.horizontalProgressBar.setVisibility(View.GONE);
            } else if (counter < 8) {
                binding.horizontalProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.graph_action_menu, menu);
        MenuItem refreshMenuItem = menu.findItem(R.id.refreshAction);
        refreshMenuItem.setOnMenuItemClickListener(item -> {
            binding.horizontalProgressBar.setVisibility(View.VISIBLE);
            viewModel.fetchAllFromRemote();
            return true;
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
