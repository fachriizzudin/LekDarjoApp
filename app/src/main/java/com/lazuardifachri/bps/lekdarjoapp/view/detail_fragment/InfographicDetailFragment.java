package com.lazuardifachri.bps.lekdarjoapp.view.detail_fragment;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentInfographicDetailBinding;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.FileModelViewModel;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.InfographicDetailViewModel;

import java.io.IOException;

public class InfographicDetailFragment extends Fragment {

    private FragmentInfographicDetailBinding binding;
    private InfographicDetailViewModel viewModel;

    private MenuItem shareAction;

    private int id;
    private String title;
    private String imageUri;
    private int uuid;

    public InfographicDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInfographicDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(InfographicDetailViewModel.class);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            uuid = InfographicDetailFragmentArgs.fromBundle(getArguments()).getUuid();
        }
    }

    private void observeViewModel() {
        viewModel.infographicLiveData.observe(getViewLifecycleOwner(), infographic -> {
            if (infographic != null && getContext() != null) {
                binding.setInfographic(infographic);
                id = infographic.getId();
                title = infographic.getTitle();
                imageUri = infographic.getImageUri();
            }
        });
        viewModel.pubPaletteLiveData.observe(getViewLifecycleOwner(), colorPalette -> {
            if (colorPalette != null && getContext() != null)
                binding.setPalette(colorPalette);
        });
        viewModel.filePathUri.observe(getViewLifecycleOwner(), uri -> {
            if (uri != null && getContext() != null) {
                shareAction.setOnMenuItemClickListener(v -> {
                    shareImage(uri);
                    return true;
                });

            }
        });
        viewModel.downloadLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && getContext() != null) {
                if (isLoading) {
                    binding.horizontalProgressBar.setVisibility(View.VISIBLE);
                    shareAction.setOnMenuItemClickListener(v -> {
                        Toast.makeText(getContext(), "Please Wait", Toast.LENGTH_SHORT).show();
                        return true;
                    });
                } else {
                    binding.horizontalProgressBar.setVisibility(View.GONE);
                }
            }
        });
        viewModel.downloadError.observe(getViewLifecycleOwner(), isError -> {
            if (isError != null && getContext() != null) {
                if (isError) {
                    binding.horizontalProgressBar.setVisibility(View.GONE);
                    shareAction.setOnMenuItemClickListener(v -> {
                        try {
                            viewModel.fetchFileFromRemote(id, imageUri, title);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    });
                }
            }
        });
        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void shareImage(Uri uri) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        //startActivity(Intent.createChooser(share, "Share Image"));
        startActivity(share);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        if (uuid != 0) {
            Log.d("check if exist", "run");
            viewModel.checkIfInfographicExistFromDatabase(uuid);
        }

        inflater.inflate(R.menu.infographic_detail_action_menu, menu);

        shareAction = menu.findItem(R.id.shareAction);

        shareAction.setOnMenuItemClickListener(v -> {
            try {
                viewModel.fetchFileFromRemote(id, imageUri, title);
                viewModel.filePathUri.observe(getViewLifecycleOwner(), uri -> {
                    shareImage(uri);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        });

        observeViewModel();

        super.onCreateOptionsMenu(menu, inflater);
    }
}

