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
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentStatisticalNewsDetailBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.ColorPalette;
import com.lazuardifachri.bps.lekdarjoapp.model.StatisticalNews;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.FileModelViewModel;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.StatisticalNewsDetailViewModel;

import java.io.IOException;

public class StatisticalNewsDetailFragment extends Fragment {

    private FragmentStatisticalNewsDetailBinding binding;
    private StatisticalNewsDetailViewModel viewModel;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private String title;
    private String documentUri;
    private int uuid;

    public StatisticalNewsDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticalNewsDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(getActivity()).get(StatisticalNewsDetailViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            uuid = StatisticalNewsDetailFragmentArgs.fromBundle(getArguments()).getUuid();
            if (uuid != 0) {
                viewModel.checkIfStatisticalNewsExistFromDatabase(uuid);
            }
        }
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.statisticalNewsLiveData.observe(getViewLifecycleOwner(), publication -> {
            if (publication != null && getContext() != null) {
                binding.setStatisticalNews(publication);
                title = publication.getTitle();
                documentUri = publication.getDocumentUri();
                binding.downloadActionFab.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_download));
                binding.downloadActionFab.setOnClickListener(v -> {
                    if (checkPermission()) {
                        try {
                            viewModel.fetchFileFromRemote(documentUri, title);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        requestPermission();
                    }
                });
            }
        });
        viewModel.filePathUri.observe(getViewLifecycleOwner(), uri -> {
            if (uri != null && getContext() != null) {
                binding.downloadActionFab.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_view));
                binding.downloadActionFab.setOnClickListener(v -> {
                    readFile(uri);
                });
            }
        });
        viewModel.downloadLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && getContext() != null) {
                Log.d("statnewsFragment", String.valueOf(isLoading));
                if (isLoading) {
                    binding.horizontalProgressBar.setVisibility(View.VISIBLE);
                    binding.downloadActionFab.setOnClickListener(v -> {
                        Toast.makeText(getContext(), "Please wait", Toast.LENGTH_SHORT).show();
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
                    binding.downloadActionFab.setOnClickListener(v -> {
                        try {
                            viewModel.fetchFileFromRemote(documentUri, title);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    private void readFile(Uri uri) {
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(uri, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            getActivity().startActivity(pdfIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "No Application available to view PDF", Toast.LENGTH_SHORT).show();
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
                            viewModel.fetchFileFromRemote(documentUri, title);
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
