package com.lazuardifachri.bps.lekdarjoapp.view.list_fragment;

import android.content.Intent;
import android.net.Uri;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentStatisticalNewsListBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.StatisticalNews;
import com.lazuardifachri.bps.lekdarjoapp.view.dialog_fragment.StatisticalNewsFilterDialogFragment;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.DateObject;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.ObjectList;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.StatisticalNewsAdapter;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.StatisticalNewsObject;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.StatisticalNewsListViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class StatisticalNewsListFragment extends Fragment implements StatisticalNewsFilterDialogFragment.OnFilterSelected{

    private StatisticalNewsListViewModel viewModel;
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

        viewModel = new ViewModelProvider(this).get(StatisticalNewsListViewModel.class);

        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Berita Statistik");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

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

    private void observeViewModel() {
        viewModel.statisticalNewsLiveData.observe(getViewLifecycleOwner(), statisticalNews -> {
            if (statisticalNews instanceof List) {
                groupDataIntoHashMap(statisticalNews);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
        });

        viewModel.error.observe(getViewLifecycleOwner(), isError -> {
            if (isError instanceof Boolean) {
                binding.error.setVisibility(isError ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.notFound.observe(getViewLifecycleOwner(), isNotFound -> {
            if (isNotFound instanceof Boolean) {
                binding.notFound.setVisibility(isNotFound ? View.VISIBLE : View.GONE);
                binding.notFoundLink.setOnClickListener(v -> {
                    Intent website;
                    website = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sidoarjokab.bps.go.id"));
                    startActivity(website);
                });
            }
        });

        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading instanceof Boolean) {
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

//        MenuItem filterMenuItem = menu.findItem(R.id.filterAction);
//
//        filterMenuItem.setOnMenuItemClickListener(item -> {
//            StatisticalNewsFilterDialogFragment dialog = new StatisticalNewsFilterDialogFragment();
//            dialog.setTargetFragment(this, 0);
//            dialog.show(getParentFragmentManager(), "dialog");
//            return true;
//        });

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
    public void sendInput(int subjectId, int categoryId, String monthString, int year) {
        binding.recyclerView.setVisibility(View.GONE);
        binding.error.setVisibility(View.GONE);
        binding.loadingProgressBar.setVisibility(View.VISIBLE);

        Log.d("paramsDialog", subjectId + " " + categoryId + " " + monthString + " " + year);

        int filterCase = 0;

        // subject = semua
        if (subjectId == 999) filterCase++;

        // subject = umum
        if (subjectId == 4) filterCase = filterCase + 10;

        // kategori = semua
        if (categoryId == 999) filterCase = filterCase + 100;

        // month = 0
        if (monthString.equals("00")) filterCase = filterCase + 1000;

        Log.d("filtercase", String.valueOf(filterCase));

        switch (filterCase) {
            case 0:
                // bisa langsung kategori
                viewModel.fetchByCategoryFromDatabase(categoryId, monthString, year);
                binding.refreshLayout.setRefreshing(false);
                break;
            case 1:
                viewModel.fetchByMonthYearFromDatabase(monthString, year);
                binding.refreshLayout.setRefreshing(false);
                break;
            case 10:
                viewModel.fetchBySubjectFromDatabase(subjectId, monthString, year);
                binding.refreshLayout.setRefreshing(false);
                break;
            case 11:
                // tidak bisa subject semua dan umum sekaligus
                break;
            case 100:
                Log.d("fetchByMonthYear", "masuk");
                viewModel.fetchBySubjectFromDatabase(subjectId, monthString, year);
                binding.refreshLayout.setRefreshing(false);
                break;
            case 110:
                // tidak bisa, jika subject umum maka tidak ada pilihan kategori
            case 111:
                // tidak bisa, jika subject umum maka tidak ada pilihan kategori
            case 1000:
            case 1001:
                viewModel.fetchByMonthYearFromDatabase(year);
                binding.refreshLayout.setRefreshing(false);
                break;
            case 1010:
            case 1100:
                viewModel.fetchBySubjectFromDatabase(subjectId, year);
                binding.refreshLayout.setRefreshing(false);
                break;
            case 1110:
                // tidak bisa, jika subject umum maka tidak ada pilihan kategori
            case 1111:
                // tidak bisa, jika subject umum maka tidak ada pilihan kategori
        }
    }

    private void groupDataIntoHashMap(List<StatisticalNews> statisticalNews) {
        LinkedHashMap<String, Set<StatisticalNews>> groupedHashMap = new LinkedHashMap<>();
        Set<StatisticalNews> set = null;
        for (StatisticalNews news : statisticalNews) {
            String hashMapKey = "01-" + news.getReleaseDate().substring(3);
            Log.d("hashmapkey", hashMapKey);
            if (groupedHashMap.containsKey(hashMapKey)) {
                Objects.requireNonNull(groupedHashMap.get(hashMapKey)).add(news);
            } else {
                set = new LinkedHashSet<>();
                set.add(news);
                groupedHashMap.put(hashMapKey, set);
            }
        }
        ArrayList<String> sortedKeys = new ArrayList<>(groupedHashMap.keySet());
        LinkedHashMap<String, Set<StatisticalNews>> sortedGroupedHashMap = new LinkedHashMap<>();
        Collections.sort(sortedKeys, (o1, o2) -> {
            DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
            try {
                return Objects.requireNonNull(f.parse(o2)).compareTo(f.parse(o1));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        });

        for (String key: sortedKeys) {
            sortedGroupedHashMap.put(key, groupedHashMap.get(key));
        }

        generateListFromMap(sortedGroupedHashMap);
    }


    private void generateListFromMap(LinkedHashMap<String, Set<StatisticalNews>> groupedHashMap) {
        ArrayList<ObjectList> consolidatedList = new ArrayList<>();
        for (String date : groupedHashMap.keySet()) {
            DateObject dateItem = new DateObject(date);
            consolidatedList.add(dateItem);
            for (StatisticalNews news : Objects.requireNonNull(groupedHashMap.get(date))) {
                StatisticalNewsObject generalItem = new StatisticalNewsObject();
                generalItem.setStatisticalNewsModel(news);
                consolidatedList.add(generalItem);
            }
        }

        adapter.setDataChange(consolidatedList);

    }

}