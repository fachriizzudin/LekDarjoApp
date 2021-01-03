package com.lazuardifachri.bps.lekdarjoapp.view.detail_fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentInfographicDetailBinding;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentInfographicDetailBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.ColorPalette;
import com.lazuardifachri.bps.lekdarjoapp.model.Infographic;
import com.lazuardifachri.bps.lekdarjoapp.model.Infographic;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.FileModelViewModel;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.InfographicDetailViewModel;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.InfographicDetailViewModel;

import java.io.IOException;

public class InfographicDetailFragment extends Fragment {

    private FragmentInfographicDetailBinding binding;
    private InfographicDetailViewModel viewModel;
    private FileModelViewModel fileModelViewModel;

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

        viewModel = new ViewModelProvider(getActivity()).get(InfographicDetailViewModel.class);
        fileModelViewModel = new ViewModelProvider(getActivity()).get(FileModelViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            uuid = InfographicDetailFragmentArgs.fromBundle(getArguments()).getUuid();
            if (uuid != 0) {
                viewModel.checkIfInfographicExistFromDatabase(uuid);
            }
        }


        observeViewModel();
        observeFileModelViewModel();

    }

    private void observeViewModel() {
        viewModel.infographicExist.observe(getViewLifecycleOwner(), isExist -> {
            if (isExist instanceof Boolean && getContext() != null) {
                if (isExist) {
                    viewModel.fetchByIdFromDatabase(uuid);
                }
            }
        });
        viewModel.infographicLiveData.observe(getViewLifecycleOwner(), infographic -> {
            if (infographic instanceof Infographic && getContext() != null) {
                binding.setInfographic(infographic);
                viewModel.setupBackgroundColor(infographic.getImageUri());
                imageUri = infographic.getImageUri();
                title = infographic.getTitle();
                fileModelViewModel.checkIfFileExistFromDatabase(infographic.getImageUri());
            }
        });
        viewModel.pubPaletteLiveData.observe(getViewLifecycleOwner(), colorPalette -> {
            if (colorPalette instanceof ColorPalette && getContext() != null)
                binding.setPalette(colorPalette);
        });
    }

    private void observeFileModelViewModel() {
        fileModelViewModel.fileExist.observe(getViewLifecycleOwner(), isExist -> {
            if (isExist != null) {
                if (!isExist) {
                    Log.d("infographic", "ga ada");
                    if (imageUri != null) {
                        try {
                            Log.d("infographic", "fetch from remote");
                            fileModelViewModel.fetchFileFromRemote(imageUri, title);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.d("infographic", "dah ada");
                }

            }
        });
    }
}
