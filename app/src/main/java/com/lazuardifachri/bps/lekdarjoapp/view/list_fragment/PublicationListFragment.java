package com.lazuardifachri.bps.lekdarjoapp.view.list_fragment;

import android.app.SearchManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentPublicationListBinding;
import com.lazuardifachri.bps.lekdarjoapp.view.dialog_fragment.PublicationFilterDialogFragment;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.PublicationAdapter;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.PublicationListViewModel;

import java.util.ArrayList;
import java.util.List;

public class PublicationListFragment extends Fragment implements PublicationFilterDialogFragment.OnFilterSelected {

    private PublicationListViewModel viewModel;
    private PublicationAdapter adapter = new PublicationAdapter(new ArrayList<>());
    private FragmentPublicationListBinding binding;

    public PublicationListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPublicationListBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Publikasi");

        viewModel = new ViewModelProvider(getActivity()).get(PublicationListViewModel.class);

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
        viewModel.publicationLiveData.observe(getViewLifecycleOwner(), publications -> {
            if (publications instanceof List) {
                adapter.updatePublication(publications);
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
            PublicationFilterDialogFragment dialog = new PublicationFilterDialogFragment();
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
    public void sendInput(int subjectId, String districtCode, int year) {
        binding.recyclerView.setVisibility(View.GONE);
        binding.error.setVisibility(View.GONE);
        binding.loadingProgressBar.setVisibility(View.VISIBLE);

        int filterCase=0;

        if (subjectId == 999) filterCase++;
        if (districtCode.equals("0")) filterCase = filterCase + 2;

        switch (filterCase) {
            case 0:
                // by subject, district, year
                viewModel.fetchByFilterFromDatabase(subjectId, districtCode, year);
                binding.refreshLayout.setRefreshing(false);
                break;
            case 1:
                // by district
                viewModel.fetchByDistrictFromDatabase(districtCode, year);
                binding.refreshLayout.setRefreshing(false);
                break;
            case 2:
                // by subject
                viewModel.fetchBySubjectFromDatabase(subjectId, year);
                binding.refreshLayout.setRefreshing(false);
                break;
            case 3:
                // by year
                viewModel.fetchByYearOnlyFromDatabase(year);
                binding.refreshLayout.setRefreshing(false);
                break;
        }

    }

}