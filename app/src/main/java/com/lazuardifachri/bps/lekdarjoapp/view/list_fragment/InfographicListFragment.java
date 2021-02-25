package com.lazuardifachri.bps.lekdarjoapp.view.list_fragment;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentInfographicListBinding;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.InfographicAdapter;
import com.lazuardifachri.bps.lekdarjoapp.view.dialog_fragment.InfographicFilterDialogFragment;
import com.lazuardifachri.bps.lekdarjoapp.view.dialog_fragment.PublicationFilterDialogFragment;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.FileModelViewModel;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.InfographicListViewModel;

import java.util.ArrayList;
import java.util.List;

public class InfographicListFragment extends Fragment implements InfographicFilterDialogFragment.OnFilterSelected {

    private MaterialButton downloadButton;
    private ProgressBar downloadProgressBar;

    private InfographicListViewModel viewModel;
    private FileModelViewModel fileModelViewModel;
    private InfographicAdapter adapter;
    private FragmentInfographicListBinding binding;

    public InfographicListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentInfographicListBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        viewModel = new ViewModelProvider(getActivity()).get(InfographicListViewModel.class);
        fileModelViewModel = new ViewModelProvider(getActivity()).get(FileModelViewModel.class);

        adapter = new InfographicAdapter(new ArrayList<>());

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Infografis");

        setHasOptionsMenu(true);

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
        viewModel.infographicLiveData.observe(getViewLifecycleOwner(), infographics -> {
            if (infographics instanceof List) {
                adapter.updateInfographic(infographics);
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
        super.onCreateOptionsMenu(menu, inflater);

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
            InfographicFilterDialogFragment dialog = new InfographicFilterDialogFragment();
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
    }

    @Override
    public void sendInput(int subjectId) {
        binding.recyclerView.setVisibility(View.GONE);
        binding.error.setVisibility(View.GONE);
        binding.loadingProgressBar.setVisibility(View.VISIBLE);

        if (subjectId == 999) {
            viewModel.fetchAllFromDatabase();
        } else {
            viewModel.fetchBySubjectFromDatabase(subjectId);
        }

        binding.refreshLayout.setRefreshing(false);
    }
}