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
    private FileModelViewModel fileModelViewModel;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private String title;
    private String documentUri;
    private Uri filePathUri;
    private int uuid;

    public StatisticalNewsDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticalNewsDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(StatisticalNewsDetailViewModel.class);
        fileModelViewModel = new ViewModelProvider(getActivity()).get(FileModelViewModel.class);

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

        observeFileModelViewModel();

        if (filePathUri == null) {
            binding.downloadActionFab.setOnClickListener(v -> {
                if (checkPermission()) {
                    try {
                        if (documentUri != null && title != null)
                            fileModelViewModel.fetchFileFromRemote(documentUri, title);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    requestPermission();
                }
            });
        }

    }

    private void observeViewModel() {
        viewModel.statisticalNewsExist.observe(getViewLifecycleOwner(), isExist -> {
            if (isExist instanceof Boolean && getContext() != null) {
                if (isExist) {
                    viewModel.fetchByIdFromDatabase(uuid);
                }
            }
        });
        viewModel.statisticalNewsLiveData.observe(getViewLifecycleOwner(), statisticalNews -> {
            if (statisticalNews instanceof StatisticalNews && getContext() != null) {
                binding.setStatisticalNews(statisticalNews);
                viewModel.checkIfStatisticalNewsExistFromDatabase(statisticalNews.getUuid());
                title = statisticalNews.getTitle();
                documentUri = statisticalNews.getDocumentUri();
                fileModelViewModel.checkIfFileExistFromDatabase(documentUri);
            }
        });
    }
    private void observeFileModelViewModel() {
        fileModelViewModel.downloadLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading instanceof Boolean) {
                Log.d("downloadLoading", "inside");
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
        fileModelViewModel.downloadError.observe(getViewLifecycleOwner(), isError -> {
            if (isError instanceof Boolean) {
                Log.d("observe", "downloadError");
                if (isError) {
                    binding.horizontalProgressBar.setVisibility(View.GONE);
                    binding.downloadActionFab.setOnClickListener(v -> {
                        try {
                            fileModelViewModel.fetchFileFromRemote(documentUri, title);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
        fileModelViewModel.fileExist.observe(getViewLifecycleOwner(), isExist -> {
            if (isExist instanceof Boolean && getContext() != null) {
                if (isExist) {
                    if (documentUri != null)
                        fileModelViewModel.fetchFileNameFromDatabase(documentUri);
                }
            }
        });
        fileModelViewModel.filePathUri.observe(getViewLifecycleOwner(), uri -> {
            if (uri instanceof Uri && getContext() != null) {
                filePathUri = uri;
                binding.downloadActionFab.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_view));
                binding.downloadActionFab.setOnClickListener(v -> {
                    readFile(uri);
                });
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
                            fileModelViewModel.fetchFileFromRemote(documentUri, title);
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
