package com.lazuardifachri.bps.lekdarjoapp.view.list_fragment;

import android.app.SearchManager;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentStatisticalNewsListBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.StatisticalNews;
import com.lazuardifachri.bps.lekdarjoapp.view.StatisticalNewsFilterDialogFragment;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.DateObject;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.ObjectList;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.StatisticalNewsAdapter;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.StatisticalNewsObject;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.StatisticalNewsViewModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class StatisticalNewsListFragment extends Fragment implements StatisticalNewsFilterDialogFragment.OnFilterSelected{

    private StatisticalNewsViewModel viewModel;
    StatisticalNewsAdapter adapter = new StatisticalNewsAdapter(new ArrayList<>());
    private FragmentStatisticalNewsListBinding binding;

    public StatisticalNewsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStatisticalNewsListBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Berita Statistik");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(StatisticalNewsViewModel.class);
        viewModel.refresh();

        observeViewModel();

        binding.refreshLayout.setOnRefreshListener(() -> {
            binding.recyclerView.setVisibility(View.GONE);
            binding.error.setVisibility(View.GONE);
            binding.notFound.setVisibility(View.GONE);
            binding.loadingProgressBar.setVisibility(View.VISIBLE);
            viewModel.fetchAllFromDatabase();
            binding.refreshLayout.setRefreshing(false);
        });
    }

    private void groupDataIntoHashMap(List<StatisticalNews> statisticalNews) {
        LinkedHashMap<String, Set<StatisticalNews>> groupedHashMap = new LinkedHashMap<>();
        Set<StatisticalNews> set = null;
        for (StatisticalNews news : statisticalNews) {
            String hashMapKey = news.getReleaseDate();
            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap.get(hashMapKey).add(news);
            } else {
                set = new LinkedHashSet<>();
                set.add(news);
                groupedHashMap.put(hashMapKey, set);
            }
        }
        generateListFromMap(groupedHashMap);
    }


    private ArrayList<ObjectList> generateListFromMap(LinkedHashMap<String, Set<StatisticalNews>> groupedHashMap) {
        ArrayList<ObjectList> consolidatedList = new ArrayList<>();
        for (String date : groupedHashMap.keySet()) {
            DateObject dateItem = new DateObject(date);
            consolidatedList.add(dateItem);
            for (StatisticalNews news : groupedHashMap.get(date)) {
                StatisticalNewsObject generalItem = new StatisticalNewsObject();
                generalItem.setStatisticalNewsModel(news);
                consolidatedList.add(generalItem);
            }
        }

        adapter.setDataChange(consolidatedList);

        return consolidatedList;
    }

    private void observeViewModel() {
        viewModel.statisticalNewsLiveData.observe(getViewLifecycleOwner(), statisticalNews -> {
            if (statisticalNews != null && statisticalNews instanceof List) {
                groupDataIntoHashMap(statisticalNews);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
        });

        viewModel.error.observe(getViewLifecycleOwner(), isError -> {
            if (isError != null && isError instanceof Boolean) {
                binding.error.setVisibility(isError ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.notFound.observe(getViewLifecycleOwner(), isNotFound -> {
            if (isNotFound != null && isNotFound instanceof Boolean) {
                binding.notFound.setVisibility(isNotFound ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading instanceof Boolean) {
                binding.loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (isLoading) {
                    binding.error.setVisibility(View.GONE);
                    binding.notFound.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.news_action_menu, menu);

        MenuItem filterMenuItem = menu.findItem(R.id.filterAction);

        filterMenuItem.setOnMenuItemClickListener(item -> {
            StatisticalNewsFilterDialogFragment dialog = new StatisticalNewsFilterDialogFragment();
            dialog.setTargetFragment(this, 0);
            dialog.show(getParentFragmentManager(), "dialog");
            return true;
        });

        MenuItem refreshMenuItem = menu.findItem(R.id.refreshAction);

        refreshMenuItem.setOnMenuItemClickListener(item -> {
            binding.recyclerView.setVisibility(View.GONE);
            binding.error.setVisibility(View.GONE);
            binding.notFound.setVisibility(View.GONE);
            binding.loadingProgressBar.setVisibility(View.VISIBLE);
            viewModel.fetchAllFromRemote();
            binding.refreshLayout.setRefreshing(false);
            return true;
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void sendInput(int subjectId, int categoryId, int month, int year) {
        binding.recyclerView.setVisibility(View.GONE);
        binding.error.setVisibility(View.GONE);
        binding.loadingProgressBar.setVisibility(View.VISIBLE);

        Log.d("paramsDialog", String.valueOf(subjectId) + " " + String.valueOf(categoryId) + " " +String.valueOf(month) + " " + String.valueOf(year));

        int filterCase = 0;

        if (subjectId == 999) filterCase++;
        if (subjectId == 4) filterCase = filterCase + 2;
        if (categoryId == 999) filterCase = filterCase + 3;

        switch (filterCase) {
            case 0:
            case 2:
                Log.d("fetchByCategory", "masuk");
                viewModel.fetchByCategoryFromDatabase(categoryId, month, year);
                // observeViewModel();
                binding.refreshLayout.setRefreshing(false);
                break;
            case 1:
                Log.d("fetchByMonthYear", "masuk");
                viewModel.fetchByMonthYearFromDatabase(month, year);
                // observeViewModel();
                binding.refreshLayout.setRefreshing(false);
                break;
                // observeViewModel();
            case 3:
            case 5:
                Log.d("fetchBySubject", "masuk");
                viewModel.fetchBySubjectFromDatabase(subjectId, month, year);
                binding.refreshLayout.setRefreshing(false);
                break;
        }
    }


}