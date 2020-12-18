package com.lazuardifachri.bps.lekdarjoapp.view.list_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentIndicatorListBinding;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentInfographicListBinding;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentPublicationListBinding;

public class InfographicListFragment extends Fragment {



    FragmentInfographicListBinding binding;

    public InfographicListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentInfographicListBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        return view;
    }
}