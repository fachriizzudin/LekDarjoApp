package com.lazuardifachri.bps.lekdarjoapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentAboutBinding;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentMainBinding;

public class AboutFragment extends Fragment {

    FragmentAboutBinding binding;

    public AboutFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentAboutBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        return view;
    }
}
