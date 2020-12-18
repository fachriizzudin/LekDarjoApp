package com.lazuardifachri.bps.lekdarjoapp.util;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.view.detail_fragment.PublicationDetailFragment;
import com.lazuardifachri.bps.lekdarjoapp.view.list_fragment.PublicationListFragment;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.FileModelViewModel;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.PublicationDetailViewModel;
import com.lazuardifachri.bps.lekdarjoapp.viewmodel.PublicationListViewModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public class BackgroundNotificationService extends JobIntentService {

    private NotificationManagerCompat notificationManager;

    public static final String PROGRESS_UPDATE = "progress_update";

    static final int JOB_ID = 1000;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, BackgroundNotificationService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        Bundle bundle = intent.getExtras();
        String[] arrayParams = bundle.getStringArray("params");

        String documentUri = arrayParams[0];
        String fileName = arrayParams[1];

        notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription("no sound");
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "id")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setDefaults(0)
                .setAutoCancel(true);

        notificationManager.notify(0, notificationBuilder.build());

        FileDownloadListener listener = new FileDownloadListener() {
            @Override
            public String onStartDownload(String fileName) {
                Log.d("listener", "onStart");
                try {
                    File root = new File(getApplication().getFilesDir().getAbsolutePath() + "/document");
                    if (!root.exists()) root.mkdirs();
                    File destinationFile = new File(getApplication().getFilesDir().getAbsolutePath() + "/document/" + fileName);
                    if (!destinationFile.exists()) destinationFile.createNewFile();
                    return destinationFile.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onProgress(int progress) {
                Log.d("listener", "onProgress");
                notificationBuilder
                        .setSmallIcon(android.R.drawable.stat_sys_download)
                        .setProgress(100, progress, false)
                        .setContentText("Downloaded: " + progress + "%");

                notificationManager.notify(0, notificationBuilder.build());
            }

            @Override
            public void onFinishDownload() {
                Log.d("listener", "onFinish");
                Intent intent = new Intent(PROGRESS_UPDATE);
                intent.putExtra("downloadComplete", true);
                LocalBroadcastManager.getInstance(BackgroundNotificationService.this).sendBroadcast(intent);

                notificationManager.cancel(0);

                notificationBuilder
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setProgress(0, 0, false)
                        .setContentText(fileName + " Downloaded");

                notificationManager.notify(0, notificationBuilder.build());
            }

            @Override
            public void onFail(String errorInfo) {
                Toast.makeText(getApplication(), "Download failed", Toast.LENGTH_SHORT).show();
            }
        };

        DownloadUtil downloadUtil = DownloadUtil.getInstance(getApplicationContext(), listener);

        try {
            downloadUtil.fetchFileFromRemote(documentUri, fileName);
            // downloadUtil.disposable.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}
