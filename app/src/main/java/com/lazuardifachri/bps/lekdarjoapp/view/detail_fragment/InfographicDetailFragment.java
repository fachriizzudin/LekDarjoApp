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
    private FileModelViewModel fileModelViewModel;

    private MenuItem saveAction;
    private MenuItem shareAction;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private int uuid;
    private String imageUri;
    private String title;

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
                imageUri = infographic.getImageUri();
                title = infographic.getTitle();
            }
        });
        viewModel.pubPaletteLiveData.observe(getViewLifecycleOwner(), colorPalette -> {
            if (colorPalette != null && getContext() != null)
                binding.setPalette(colorPalette);
        });
        viewModel.filePathUri.observe(getViewLifecycleOwner(), uri -> {
            if (uri != null && getContext() != null) {
                saveAction.setIcon(getContext().getResources().getDrawable(R.drawable.ic_saved));
                saveAction.setOnMenuItemClickListener(v -> {
                    Toast.makeText(getContext(), "Image Downloaded", Toast.LENGTH_SHORT).show();
                    return true;
                });
                shareAction.setOnMenuItemClickListener(v -> {
                    shareImage(uri);
                    return true;
                });

            }
        });
        viewModel.downloadLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && getContext() != null) {
                if (isLoading) {
                    Log.d("fragment loading", "yes");
                    binding.horizontalProgressBar.setVisibility(View.VISIBLE);
                    saveAction.setOnMenuItemClickListener(v -> {
                        Toast.makeText(getContext(), "Please Wait", Toast.LENGTH_SHORT).show();
                        return true;
                    });
                    shareAction.setOnMenuItemClickListener(v -> {
                        Toast.makeText(getContext(), "Please Wait", Toast.LENGTH_SHORT).show();
                        return true;
                    });
                } else {
                    Log.d("fragment loading", "no");
                    binding.horizontalProgressBar.setVisibility(View.GONE);
                }
            }
        });
        viewModel.downloadError.observe(getViewLifecycleOwner(), isError -> {
            if (isError != null && getContext() != null) {
                if (isError) {
                    binding.horizontalProgressBar.setVisibility(View.GONE);
                    saveAction.setOnMenuItemClickListener(v -> {
                        try {
                            viewModel.fetchFileFromRemote(imageUri, title);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    });
                }
            }
        });
    }

    private void shareImage(Uri uri) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image"));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        if (uuid != 0) {
            viewModel.checkIfInfographicExistFromDatabase(uuid);
        }

        inflater.inflate(R.menu.infographic_detail_action_menu, menu);

        saveAction = menu.findItem(R.id.saveAction);

        saveAction.setOnMenuItemClickListener(item -> {
            try {
                viewModel.fetchFileFromRemote(imageUri, title);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        });

        shareAction = menu.findItem(R.id.shareAction);

        shareAction.setOnMenuItemClickListener(v -> {
            Toast.makeText(getContext(), "Download Image First", Toast.LENGTH_SHORT).show();
            return true;
        });

        observeViewModel();

        super.onCreateOptionsMenu(menu, inflater);
    }
}

