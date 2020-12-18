package com.lazuardifachri.bps.lekdarjoapp.view.list_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentIndicatorListBinding;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentPublicationListBinding;

public class IndicatorListFragment extends Fragment {

    FragmentIndicatorListBinding binding;

    public IndicatorListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentIndicatorListBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        return view;
    }
}