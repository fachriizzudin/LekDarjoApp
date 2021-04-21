package com.lazuardifachri.bps.lekdarjoapp.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentGraphBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Graph;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.GraphViewModel;

import java.util.List;

public class GraphFragment extends Fragment {

    private FragmentGraphBinding binding;

    private GraphViewModel viewModel;

    Toast toast;

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

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.graph_toast, (ViewGroup) getActivity().findViewById(R.id.graph_toast));

//        TextView text = layout.findViewById(R.id.text);
//        text.setText(R.string.custom_toast_message);

        toast = new Toast(getContext());
        toast.setGravity(Gravity.TOP, 0, 190);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        viewModel.refresh(toast);

        observeViewModel();

        binding.povertyCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToPoverty());
        });

        binding.unemploymentCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToUnemployment());
        });

        binding.giniRatioCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMaintToGiniRatio());
        });

        binding.inflationCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToInflation());
        });

        binding.economyGrowthCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToEconomy());
        });

        binding.PDRBBerlakuCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToPDRB());
        });

        binding.ipmCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToIpm());
        });

        binding.totalPopulationCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToPopulation());
        });

        binding.riceProductionCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToRiceProduction());
        });

        binding.luasPanenCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToLuasPanen());
        });

        binding.industryCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(GraphFragmentDirections.actionMainToIndustry());
        });

    }

    private String getLastYear(List<Graph> graphList) {
        return String.valueOf(graphList.get(graphList.size()-1).getYear());
    }

    private String getLastYearData(List<Graph> graphList) {
        double value = graphList.get(graphList.size()-1).getValue();
        int intValue = (int) value;
        if (value - intValue > 0) {
            return String.valueOf(value);
        } else {
            return String.valueOf(intValue);
        }
    }

    private boolean isIncrease(List<Graph> graphList) {
        double lastYear = graphList.get(graphList.size()-1).getValue();
        try {
            double secondLastYear = graphList.get(graphList.size()-2).getValue();
            return lastYear > secondLastYear;
        } catch (Exception e) {
            return true;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void observeViewModel() {

        // poverty
        viewModel.povertyDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearPoverty.setText(getLastYear(graphData.getData()));
            binding.povertyNewValue.setText(getLastYearData(graphData.getData()));
            String title = graphData.getMeta().getTitle() + " (" + graphData.getMeta().getVerticalUnit() + ")";
            binding.titlePoverty.setText(title);
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
            String title = graphData.getMeta().getTitle() + " (" + graphData.getMeta().getVerticalUnit() + ")";
            binding.titleUnemployment.setText(title);
            if (isIncrease(graphData.getData())) {
                binding.upArrowUnemployment.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowUnemployment.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // gini rasio
        viewModel.giniRatioDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearGiniRatio.setText(getLastYear(graphData.getData()));
            binding.giniRatioNewValue.setText(getLastYearData(graphData.getData()));
            String title = graphData.getMeta().getTitle() + " (" + graphData.getMeta().getVerticalUnit() + ")";
            binding.titleGiniRatio.setText(title);
            if (isIncrease(graphData.getData())) {
                binding.upArrowGiniRatio.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowGiniRatio.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });


        // inflation
        viewModel.inflationDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearInflation.setText(getLastYear(graphData.getData()));
            binding.inflationNewValue.setText(getLastYearData(graphData.getData()));
            String title = graphData.getMeta().getTitle() + " (" + graphData.getMeta().getVerticalUnit() + ")";
            binding.titleInflation.setText(title);
            if (isIncrease(graphData.getData())) {
                binding.upArrowInflation.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowInflation.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // economy growth
        viewModel.economyGrowthDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearEconomyGrowth.setText(getLastYear(graphData.getData()));
            binding.economyGrowthNewValue.setText(getLastYearData(graphData.getData()));
            String title = graphData.getMeta().getTitle() + " (" + graphData.getMeta().getVerticalUnit() + ")";
            binding.titleEconomyGrowth.setText(title);
            if (isIncrease(graphData.getData())) {
                binding.upArrowEconomyGrowth.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowEconomyGrowth.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // pdrb
        viewModel.pdrbDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearPDRBBerlaku.setText(getLastYear(graphData.getData()));
            int value = (int) graphData.getData().get(graphData.getData().size()-1).getValue();
            @SuppressLint("DefaultLocale") String valueString = String.format("%.2f", value/1000000.0);
            binding.pdrbBerlakuNewValue.setText(valueString);
            // String title = graphData.getMeta().getTitle() + " (" + graphData.getMeta().getVerticalUnit() + ")";
            // binding.titlePDRBBerlaku.setText(title);
            if (isIncrease(graphData.getData())) {
                binding.upArrowPDRBBerlaku.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowPDRBBerlaku.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // ipm
        viewModel.ipmDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearIpm.setText(getLastYear(graphData.getData()));
            binding.ipmNewValue.setText(getLastYearData(graphData.getData()));
            String title = graphData.getMeta().getTitle() + " (" + graphData.getMeta().getVerticalUnit() + ")";
            binding.titleIpm.setText(title);
            if (isIncrease(graphData.getData())) {
                binding.upArrowIpm.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowIpm.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
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

        // rice production
        viewModel.riceProductionDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearRiceProduction.setText(getLastYear(graphData.getData()));
            binding.riceProductionNewValue.setText(getLastYearData(graphData.getData()));
            String title = graphData.getMeta().getTitle() + " (" + graphData.getMeta().getVerticalUnit() + ")";
            binding.titleRiceProduction.setText(title);
            if (isIncrease(graphData.getData())) {
                binding.upArrowRiceProduction.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowRiceProduction.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // luas panen
        viewModel.luasPanenDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearLuasPanen.setText(getLastYear(graphData.getData()));
            binding.luasPanenNewValue.setText(getLastYearData(graphData.getData()));
            String title = graphData.getMeta().getTitle() + " (" + graphData.getMeta().getVerticalUnit() + ")";
            binding.titleLuasPanen.setText(title);
            if (isIncrease(graphData.getData())) {
                binding.upArrowLuasPanen.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowLuasPanen.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // jumlah industri besar sedang
        viewModel.industryDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.yearIndustry.setText(getLastYear(graphData.getData()));
            binding.industryNewValue.setText(getLastYearData(graphData.getData()));
            String title = graphData.getMeta().getTitle() + " (" + graphData.getMeta().getVerticalUnit() + ")";
            binding.titleIndustry.setText(title);
            if (isIncrease(graphData.getData())) {
                binding.upArrowIndustry.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                binding.upArrowIndustry.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        });

        // luas wilayah
        viewModel.areaDataLive.observe(getViewLifecycleOwner(), graphData -> {
            binding.areaNewValue.setText(String.valueOf(graphData.getData().get(0).getValue()));
        });


        viewModel.counter.observe(getViewLifecycleOwner(), counter -> {
            if (counter == 12) {
                binding.horizontalProgressBar.setVisibility(View.GONE);
            } else if (counter < 12) {
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
            viewModel.fetchAllFromRemote(toast);
            return true;
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
