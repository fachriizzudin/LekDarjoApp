package com.lazuardifachri.bps.lekdarjoapp.view.list_fragment;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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

//    private int subjectId;
    private int categoryId;
    private int id;
    private String title;
    private String documentUri;

//    public static IndicatorListFragment newInstance(Integer counter) {
//        IndicatorListFragment fragment = new IndicatorListFragment();
//        Bundle args = new Bundle();
//        args.putInt("subjectId", counter + 1);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentIndicatorBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(IndicatorListViewModel.class);
        fileModelViewModel = new ViewModelProvider(this).get(FileModelViewModel.class);

        if (getArguments() != null) {
            categoryId = IndicatorListFragmentArgs.fromBundle(getArguments()).getId();
        }

        adapter = new IndicatorAdapter(new ArrayList<>(), new IndicatorItemListener() {
            @Override
            public void onIndicatorDownloadClick(View button, View progressBar, Indicator indicator) {
                downloadButton = (MaterialButton) button;
                downloadProgressBar = (ProgressBar) progressBar;
                id = indicator.getId();
                documentUri = indicator.getDocumentUri();
                title = indicator.getTitle();
                Log.d("click indicator", indicator.getDocumentUri());
                try {
                    if (checkPermission()) {
                        viewModel.fetchFileFromRemote(id, documentUri, indicator.getTitle(), downloadButton, downloadProgressBar);
                    } else {
                        requestPermission();
                    }
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Download Failed", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void checkIfFileExist(View v, Indicator indicator) {
                downloadButton = (MaterialButton) v;
                viewModel.checkIfFileExistFromDatabase(indicator.getId(), downloadButton);
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

        binding.error.setVisibility(View.GONE);
        binding.notFound.setVisibility(View.GONE);

        viewModel.refresh(categoryId);
        observeViewModel();

        binding.refreshLayout.setOnRefreshListener(() -> {
            binding.recyclerView.setVisibility(View.GONE);
            binding.error.setVisibility(View.GONE);
            binding.notFound.setVisibility(View.GONE);
            binding.loadingProgressBar.setVisibility(View.VISIBLE);
            viewModel.fetchByCategoryFromDatabase(categoryId);
            binding.refreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        binding.error.setVisibility(View.GONE);
        binding.notFound.setVisibility(View.GONE);
    }

    private void observeViewModel() {
        viewModel.indicatorLiveData.observe(getViewLifecycleOwner(), indicators -> {
            if (indicators instanceof List) {
                adapter.updateIndicator(indicators);
                Log.d("indicator length", String.valueOf(indicators.size()));
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
        inflater.inflate(R.menu.indicator_action_menu, menu);

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

        MenuItem refreshMenuItem = menu.findItem(R.id.refreshAction);

        refreshMenuItem.setOnMenuItemClickListener(item -> {
            binding.recyclerView.setVisibility(View.GONE);
            binding.error.setVisibility(View.GONE);
            binding.notFound.setVisibility(View.GONE);
            binding.loadingProgressBar.setVisibility(View.VISIBLE);

            viewModel.fetchAllFromRemote(categoryId);

            binding.refreshLayout.setRefreshing(false);
            return true;
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void sendInput(int categoryId) {

        binding.recyclerView.setVisibility(View.GONE);
        binding.error.setVisibility(View.GONE);
        binding.loadingProgressBar.setVisibility(View.VISIBLE);

        viewModel.fetchByCategoryFromDatabase(categoryId);
        binding.refreshLayout.setRefreshing(false);
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
                            viewModel.fetchFileFromRemote(id, documentUri, title, downloadButton, downloadProgressBar);
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
