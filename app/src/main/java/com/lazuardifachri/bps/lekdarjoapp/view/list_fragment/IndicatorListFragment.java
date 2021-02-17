package com.lazuardifachri.bps.lekdarjoapp.view.list_fragment;

import android.Manifest;
import android.app.SearchManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentIndicatorBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Indicator;
import com.lazuardifachri.bps.lekdarjoapp.view.listener.IndicatorItemListener;
import com.lazuardifachri.bps.lekdarjoapp.view.dialog_fragment.IndicatorFilterDialogFragment;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.IndicatorAdapter;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.FileModelViewModel;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.IndicatorListViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndicatorListFragment extends Fragment implements IndicatorFilterDialogFragment.OnFilterSelected  {

    private MaterialButton downloadButton;
    private ProgressBar downloadProgressBar;

    private FragmentIndicatorBinding binding;
    private IndicatorListViewModel viewModel;
    private FileModelViewModel fileModelViewModel;

    private IndicatorAdapter adapter;

    private static final int PERMISSION_REQUEST_CODE = 1;

    private int subjectId;
    private String title;
    private String documentUri;

    public static IndicatorListFragment newInstance(Integer counter) {
        IndicatorListFragment fragment = new IndicatorListFragment();
        Bundle args = new Bundle();
        args.putInt("subjectId", counter + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentIndicatorBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(IndicatorListViewModel.class);
        fileModelViewModel = new ViewModelProvider(this).get(FileModelViewModel.class);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            subjectId = bundle.getInt("subjectId");
        }

        adapter = new IndicatorAdapter(new ArrayList<>(), new IndicatorItemListener() {
            @Override
            public void onIndicatorDownloadClick(View button, View progressBar, Indicator indicator) {
                downloadButton = (MaterialButton) button;
                downloadProgressBar = (ProgressBar) progressBar;
                documentUri = indicator.getDocumentUri();
                title = indicator.getTitle();
                try {
                    if (checkPermission()) {
                        viewModel.fetchFileFromRemote(indicator.getDocumentUri(), indicator.getTitle(), downloadButton, downloadProgressBar);
                    } else {
                        requestPermission();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void checkIfFileExist(View v, Indicator indicator) {
                Log.d("checkIfFileExist", "run");
                downloadButton = (MaterialButton) v;
                viewModel.checkIfFileExistFromDatabase(indicator.getDocumentUri(), downloadButton);
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Indikator");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        viewModel.refresh(subjectId);

        binding.refreshLayout.setOnRefreshListener(() -> {
            binding.recyclerView.setVisibility(View.GONE);
            binding.error.setVisibility(View.GONE);
            binding.notFound.setVisibility(View.GONE);
            binding.loadingProgressBar.setVisibility(View.VISIBLE);
            viewModel.fetchBySubjectFromDatabase(subjectId);
            binding.refreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        observeViewModel();
    }


    private void observeViewModel() {
        Log.d("observe", "run " + subjectId);
        viewModel.indicatorLiveData.observe(getViewLifecycleOwner(), indicators -> {
            if (indicators != null) {
                adapter.updateIndicator(indicators);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
        });

        viewModel.error.observe(getViewLifecycleOwner(), isError -> {
            if (isError != null) {
                binding.error.setVisibility(isError ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.notFound.observe(getViewLifecycleOwner(), isNotFound -> {
            if (isNotFound != null) {
                binding.notFound.setVisibility(isNotFound ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
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
        inflater.inflate(R.menu.action_menu, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(getContext().SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.searchAction).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });

        MenuItem filterMenuItem = menu.findItem(R.id.filterAction);

        filterMenuItem.setOnMenuItemClickListener(item -> {
            IndicatorFilterDialogFragment dialog = new IndicatorFilterDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("code", subjectId);

            dialog.setArguments(bundle);
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

            viewModel.fetchAllFromRemote(subjectId);

            binding.refreshLayout.setRefreshing(false);
            return true;
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void sendInput(int categoryId, String monthString, int year) {

        binding.recyclerView.setVisibility(View.GONE);
        binding.error.setVisibility(View.GONE);
        binding.loadingProgressBar.setVisibility(View.VISIBLE);

        int filterCase = 0;

        // kategori semua
        if (categoryId == 999) filterCase++;

        // month = 0
        if (monthString.equals("00")) filterCase = filterCase + 10;

        switch (filterCase) {
            case 0:
                // fetch by category and month year
                viewModel.fetchByCategoryFromDatabase(categoryId, monthString, year);
                binding.refreshLayout.setRefreshing(false);
            case 1:
                // fetch by month and year
                viewModel.fetchByMonthYearFromDatabase(subjectId, monthString, year);
                binding.refreshLayout.setRefreshing(false);
                break;
            case 10:
                // fetch by category and year
                viewModel.fetchByCategoryFromDatabase(categoryId, year);
                binding.refreshLayout.setRefreshing(false);
            case 11:
                // fetch by year
                viewModel.fetchByYearFromDatabase(subjectId, year);
                binding.refreshLayout.setRefreshing(false);
        }
    }



    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (documentUri != null && title != null) {
                        try {
                            viewModel.fetchFileFromRemote(documentUri, title, downloadButton, downloadProgressBar);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
