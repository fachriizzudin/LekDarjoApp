package com.lazuardifachri.bps.lekdarjoapp.view.list_fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.util.Parameters;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.CategoryListAdapter;

import java.util.ArrayList;

public class IndicatorCategoryFragment extends Fragment {

    private int subjectId;
    private RecyclerView recyclerView;
    private CategoryListAdapter adapter;

    public IndicatorCategoryFragment() {
        // Required empty public constructor
    }

    public static IndicatorCategoryFragment newInstance(Integer counter) {
        IndicatorCategoryFragment fragment = new IndicatorCategoryFragment();
        Bundle args = new Bundle();
        args.putInt("subjectId", counter + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indicator_category, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Indikator");
        adapter = new CategoryListAdapter(new ArrayList<>());
        if (getArguments()!=null) {
            Bundle bundle = getArguments();
            subjectId = bundle.getInt("subjectId");
        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.categoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        switch (subjectId) {
            case 1:
                adapter.setDataChange(Parameters.getInstance().getSocialCategoriesIdx());
                break;
            case 2:
                adapter.setDataChange(Parameters.getInstance().getEconomyCategoriesIdx());
                break;
            case 3:
                adapter.setDataChange(Parameters.getInstance().getAgricultureCategoriesIdx());
                break;
        }

    }
}