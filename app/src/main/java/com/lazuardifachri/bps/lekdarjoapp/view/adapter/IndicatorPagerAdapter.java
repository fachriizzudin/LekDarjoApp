package com.lazuardifachri.bps.lekdarjoapp.view.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.lazuardifachri.bps.lekdarjoapp.view.list_fragment.IndicatorCategoryFragment;
import com.lazuardifachri.bps.lekdarjoapp.view.list_fragment.IndicatorListFragment;

public class IndicatorPagerAdapter extends FragmentStateAdapter {


    public IndicatorPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position<3) {
            Log.d("createFragment", String.valueOf(position));
            return IndicatorCategoryFragment.newInstance(position);
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
