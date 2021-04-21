package com.lazuardifachri.bps.lekdarjoapp.view.list_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentIndicatorListBinding;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.IndicatorPagerAdapter;

public class IndicatorPagerFragment extends Fragment {

    private FragmentIndicatorListBinding binding;
    private IndicatorPagerAdapter adapter;

    public IndicatorPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentIndicatorListBinding.inflate(inflater, container, false);

        View view = binding.getRoot();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        adapter = new IndicatorPagerAdapter(this);
        binding.viewPager2.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator  = new TabLayoutMediator(binding.indicatorTabLayout, binding.viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Sosial");
                    break;
                case 1:
                    tab.setText("Ekonomi");
                    break;
                case 2:
                    tab.setText("Pertanian");
                    break;
            }
            binding.viewPager2.setCurrentItem(tab.getPosition(),true);
        });
        tabLayoutMediator.attach();

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.indicatorTabLayout.selectTab(binding.indicatorTabLayout.getTabAt(position));
            }
        });


    }
}