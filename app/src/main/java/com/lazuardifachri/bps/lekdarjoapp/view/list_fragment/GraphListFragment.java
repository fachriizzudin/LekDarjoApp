package com.lazuardifachri.bps.lekdarjoapp.view.list_fragment;

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
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentGraphListBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Graph;
import com.lazuardifachri.bps.lekdarjoapp.view.adapter.GraphAdapter;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.GraphListViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class GraphListFragment extends Fragment {

    private GraphListViewModel viewModel;
    private GraphAdapter adapter = new GraphAdapter(new ArrayList<>());
    private FragmentGraphListBinding binding;

    public GraphListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("GraphList", "onCreateView");
        binding = FragmentGraphListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(getActivity()).get(GraphListViewModel.class);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Statistik Sidoarjo");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("GraphList", "onViewCreated");
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        viewModel.refresh();
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.graphLive.observe(getViewLifecycleOwner(), graph -> {
            if (!graph.isEmpty()) {
                Log.d("GraphList", "observe data live");
                HashSet<Graph> graphSet = new HashSet<>(graph);
                adapter.updatePublication(graphSet);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
        });

        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.horizontalProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (isLoading) {
                    binding.error.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.GONE);
                }
            }
        });
        viewModel.error.observe(getViewLifecycleOwner(), isError -> {
            if (isError != null) {
                binding.error.setVisibility(isError ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.graph_action_menu, menu);
        MenuItem refreshMenuItem = menu.findItem(R.id.refreshAction);
        refreshMenuItem.setOnMenuItemClickListener(item -> {
            binding.horizontalProgressBar.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
            binding.error.setVisibility(View.GONE);
            viewModel.fetchAllFromRemote();
            observeViewModel();
            return true;
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}