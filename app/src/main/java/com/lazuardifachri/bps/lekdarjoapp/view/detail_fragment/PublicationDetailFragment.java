package com.lazuardifachri.bps.lekdarjoapp.view.detail_fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentPublicationDetailBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.ColorPalette;
import com.lazuardifachri.bps.lekdarjoapp.model.Publication;
import com.lazuardifachri.bps.lekdarjoapp.util.BackgroundNotificationService;
import com.lazuardifachri.bps.lekdarjoapp.util.DownloadUtil;
import com.lazuardifachri.bps.lekdarjoapp.util.FileDownloadListener;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.FileModelViewModel;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.PublicationDetailViewModel;

import java.io.File;
import java.io.IOException;

import static com.lazuardifachri.bps.lekdarjoapp.util.BackgroundNotificationService.PROGRESS_UPDATE;

public class PublicationDetailFragment extends Fragment {

    private FragmentPublicationDetailBinding binding;
    private PublicationDetailViewModel viewModel;
    private FileModelViewModel fileModelViewModel;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private String title;
    private String documentUri;
    private Uri filePathUri;
    private int uuid;
    private Boolean downloading =false;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PROGRESS_UPDATE)) {
                boolean downloadComplete = intent.getBooleanExtra("downloadComplete", false);
                if (downloadComplete) {
                    if (documentUri != null) {
                        fileModelViewModel.fetchFileNameFromDatabase(documentUri);
                        binding.downloadActionFab.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_view));
                        binding.downloadActionFab.setOnClickListener(v -> {
                            if (filePathUri != null) readFile(filePathUri);
                        });
                    }
                    Toast.makeText(context, "File download completed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public PublicationDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPublicationDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        registerReceiver();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PublicationDetailViewModel.class);
        fileModelViewModel = new ViewModelProvider(this).get(FileModelViewModel.class);

        observeViewModel();

        if (filePathUri == null) {
            if (downloading) {
                binding.downloadActionFab.setClickable(false);
            } else {
                binding.downloadActionFab.setOnClickListener(v -> {
                    if (checkPermission()) {
                        startFileDownload(documentUri, title);
                    } else {
                        requestPermission();
                    }
                });
            }
        }

        if (getArguments() != null) {
            uuid = PublicationDetailFragmentArgs.fromBundle(getArguments()).getUuid();
            Log.d("uuid sebenarnya", String.valueOf(uuid));
            if (uuid != 0) {
                viewModel.fetchByIdFromDatabase(uuid);
            }
        }
    }

    private void observeViewModel() {
        viewModel.publicationLiveData.observe(getViewLifecycleOwner(), publication -> {
            if (publication != null && publication instanceof Publication && getContext() != null) {
                binding.setPublication(publication);
                viewModel.setupBackgroundColor(publication.getImageUri());
                title = publication.getTitle();
                documentUri = publication.getDocumentUri();
                fileModelViewModel.checkIfFileExistFromDatabase(documentUri);
            }
        });
        viewModel.pubPaletteLiveData.observe(getViewLifecycleOwner(), colorPalette -> {
            if (colorPalette != null && colorPalette instanceof ColorPalette && getContext() !=null)
                binding.setPubPalette(colorPalette);
        });
        fileModelViewModel.fileExist.observe(getViewLifecycleOwner(), exist -> {
            if (exist != null && exist instanceof  Boolean && getContext() != null) {
                if (exist) {
                    if (documentUri != null) fileModelViewModel.fetchFileNameFromDatabase(documentUri);
                }
            }
        });
        fileModelViewModel.filePathUri.observe(getViewLifecycleOwner(), uri -> {
            if (uri != null && uri instanceof Uri && getContext() != null) {
                filePathUri = uri;
                binding.downloadActionFab.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_view));
                binding.downloadActionFab.setOnClickListener(v -> {
                    readFile(uri);
                });
            }
        });
    }

    private void startFileDownload(String url, String title) {

        Log.d("startDownload", "clicked");
        String fileName = title.replaceAll(" ", "").concat(".pdf");

        Bundle bundle = new Bundle();
        bundle.putStringArray("params", new String[]{url, fileName});

        Intent intent = new Intent(getActivity(), BackgroundNotificationService.class);
        intent.putExtras(bundle);

        BackgroundNotificationService.enqueueWork(getContext(), intent);

        getActivity().startService(intent);
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

    public void registerReceiver() {
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PROGRESS_UPDATE);
        bManager.registerReceiver(mBroadcastReceiver, intentFilter);
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
                    startFileDownload(documentUri, title);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}