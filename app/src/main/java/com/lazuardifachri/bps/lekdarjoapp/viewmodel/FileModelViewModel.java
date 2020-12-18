package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lazuardifachri.bps.lekdarjoapp.model.FileModel;
import com.lazuardifachri.bps.lekdarjoapp.model.api.FileDownloadApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;
import com.lazuardifachri.bps.lekdarjoapp.util.BackgroundNotificationService;
import com.lazuardifachri.bps.lekdarjoapp.util.FileDownloadListener;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.lazuardifachri.bps.lekdarjoapp.util.BackgroundNotificationService.PROGRESS_UPDATE;

public class FileModelViewModel extends AndroidViewModel {

    public MutableLiveData<Uri> filePathUri = new MutableLiveData<>();
    public MutableLiveData<Boolean> fileExist = new MutableLiveData<>();

    private final CompositeDisposable disposable = new CompositeDisposable();

    private static final String TAG = "DownloadUtils";

    public FileModelViewModel(@NonNull Application application) {
        super(application);
    }

    private Integer getFileIdFromUri(String documentUri) {
        Pattern pattern = Pattern.compile("[^files/]*$");
        Matcher matcher = pattern.matcher(documentUri);
        if (matcher.find()) return Integer.parseInt(matcher.group());
        return null;
    }

    public void fetchFileNameFromDatabase(String documentUri) {
        int fileId = getFileIdFromUri(documentUri);
        Log.d("fileId", String.valueOf(fileId));
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().getFileName(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull String fileName) {
                        File fileDir = new File(getApplication().getFilesDir(), "document");
                        File filePath = new File(fileDir, fileName);

                        Log.d("file", "filePath :"+ filePath);

                        Uri contentUri = getUriForFile(getApplication(), "com.lazuardifachri.bps.fileprovider", filePath);

                        filePathUri.setValue(contentUri);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d("fetchFileName", "error");
                        e.printStackTrace();
                    }
                }));
    }

    public void checkIfFileExistFromDatabase(String documentUri) {
        int fileId = getFileIdFromUri(documentUri);
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().isFileExist(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean exist) {
                        if (exist) {
                            fileExist.setValue(true);
                        } else {
                            fileExist.setValue(false);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }
                }));
    }

}
