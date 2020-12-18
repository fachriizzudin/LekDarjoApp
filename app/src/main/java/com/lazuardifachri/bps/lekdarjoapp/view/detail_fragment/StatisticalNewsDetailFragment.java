package com.lazuardifachri.bps.lekdarjoapp.view.detail_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentStatisticalNewsDetailBinding;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.FileModelViewModel;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.StatisticalNewsDetailViewModel;

public class StatisticalNewsDetailFragment extends Fragment {

    private FragmentStatisticalNewsDetailBinding binding;

    private StatisticalNewsDetailViewModel viewModel;
    private FileModelViewModel fileModelViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticalNewsDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(StatisticalNewsDetailViewModel.class);
        fileModelViewModel = new ViewModelProvider(this).get(FileModelViewModel.class);



    }
}
