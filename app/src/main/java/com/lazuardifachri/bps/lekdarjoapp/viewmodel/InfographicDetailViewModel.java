package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.model.ColorPalette;
import com.lazuardifachri.bps.lekdarjoapp.model.Download;
import com.lazuardifachri.bps.lekdarjoapp.model.FileModel;
import com.lazuardifachri.bps.lekdarjoapp.model.Infographic;
import com.lazuardifachri.bps.lekdarjoapp.model.api.FileDownloadApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;
import com.lazuardifachri.bps.lekdarjoapp.util.DetailFileDownloadListener;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;
import com.lazuardifachri.bps.lekdarjoapp.util.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static androidx.core.content.FileProvider.getUriForFile;

public class InfographicDetailViewModel extends AndroidViewModel {

    public MutableLiveData<Infographic> infographicLiveData = new MutableLiveData<>();
    public MutableLiveData<ColorPalette> pubPaletteLiveData = new MutableLiveData<>();

    public MutableLiveData<Uri> filePathUri = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();
    public MutableLiveData<Boolean> downloadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> downloadLoading = new MutableLiveData<>();

    private final CompositeDisposable disposable = new CompositeDisposable();

    public InfographicDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setupBackgroundColor(String url) {
        loading.setValue(true);
        Glide.with(getApplication())
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Palette.from(resource).generate(palette -> {
                            if (palette != null) {
                                int intColor = palette.getLightVibrantColor(ContextCompat.getColor(getApplication(), R.color.blue));
                                ColorPalette pubPalette = new ColorPalette(intColor);
                                pubPaletteLiveData.setValue(pubPalette);
                                loading.setValue(false);
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    public void fetchByIdFromDatabase(int uuid) {
        Log.d("fetch by id database", "run");
        disposable.add(myDatabase.getInstance(getApplication())
                .infographicDao().getInfographicByUuid(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Infographic>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Infographic infographic) {
                        Log.d("fetch by id database", "success");
                        infographicLiveData.setValue(infographic);
                        setupBackgroundColor(infographic.getImageUri());
                        checkIfFileExistFromDatabase(infographic.getId());
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void checkIfInfographicExistFromDatabase(int uuid) {
        disposable.add(myDatabase.getInstance(getApplication())
                .infographicDao().isInfographicExist(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean exist) {
                        Log.d("check if exist", "success");
                        fetchByIdFromDatabase(uuid);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));

    }

    public void fetchFileFromRemote(int id, String documentUri, String title) throws IOException {

        DetailFileDownloadListener listener = new DetailFileDownloadListener() {
            @Override
            public String onStartDownload(String fileName) {
                Log.d("listener", "onStartDownload");
                insertDownloadWaitingList(id);
                downloadLoading.setValue(true);
                try {
                    File root = new File(getApplication().getFilesDir().getAbsolutePath() + "/images");
                    if (!root.exists()) {
                        if (root.mkdirs()) {
                            root.setReadable(true, false);
                            root.setExecutable(true, false);
                        }
                    }
                    File destinationFile = new File(getApplication().getFilesDir().getAbsolutePath() + "/images/" + fileName);
                    if (!destinationFile.exists()) {
                        if(destinationFile.createNewFile()){
                            destinationFile.setReadable(true, false);
                            destinationFile.setExecutable(true,false);
                        }
                    }
                    Log.d("listenerOnStart", destinationFile.getAbsolutePath());
                    return destinationFile.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onProgressDownload(int progress) {
                Log.d("listenerOnProgress", "run");
                downloadLoading.postValue(true);
            }

            @Override
            public void onFinishDownload() {
                Log.d("listener", "onFinishDownload");
                downloadLoading.postValue(false);
                deleteDownloadWaitingList(id);
                Toast.makeText(getApplication(), "Download Finished", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailDownload(String errorInfo) {
                Log.d("listenerOnFail", "run");
                Log.d("error", errorInfo);
                downloadLoading.postValue(false);
                downloadError.postValue(true);
                deleteDownloadWaitingList(id);
                Toast.makeText(getApplication(), "Download Failed", Toast.LENGTH_SHORT).show();
            }
        };

        String fileName = title.replaceAll(" ", "").concat(".png");
        String filePath = listener.onStartDownload(fileName);
        FileDownloadApi fileDownloadApi = ServiceGenerator.createDetailDownloadService(FileDownloadApi.class, getApplication(), listener);

        fileDownloadApi.download(documentUri)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(ResponseBody::byteStream).observeOn(Schedulers.computation())
                .doOnNext(inputStream -> writeFile(inputStream, filePath, documentUri, listener)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull InputStream inputStream) {
                        insertPathToDatabase(id, documentUri, fileName, filePath, listener);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        listener.onFailDownload(e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void insertPathToDatabase(int id, String documentUri, String fileName, String filePath, DetailFileDownloadListener listener) {
        String fileId = "infographic" + id;
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().insertFile(new FileModel(fileId, fileName, filePath))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Long aLong) {
                        checkIfFileExistFromDatabase(id);
                        listener.onFinishDownload();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                })
        );
    }

    public void checkIfFileExistFromDatabase(int id) {
        String fileId = "infographic" + id;
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().isFileExist(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean exist) {
                        if (exist) {
                            Log.d("check file in database", "exist");
                            fetchFileNameFromDatabase(id);
                        }
                        // harus tetap dijalankan untuk kasus berganti ke detail yang sudah didownload
                        isDownloading(id);

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void fetchFileNameFromDatabase(int id) {
        String fileId = "infographic" + id;
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().getFileName(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull String fileName) {
                        File fileDir = new File(getApplication().getFilesDir(), "images");
                        File filePath = new File(fileDir, fileName);
                        Uri contentUri = getUriForFile(getApplication(), "com.lazuardifachri.bps.fileprovider", filePath);
                        filePathUri.setValue(contentUri);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    private void insertDownloadWaitingList(int id) {
        String fileId = "infographic" + id;
        disposable.add(myDatabase.getInstance(getApplication())
                .downloadDao().insertWaitingFile(new Download(fileId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Long aLong) {
                        Log.d("waiting list", "success");
                        downloadLoading.setValue(true);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                })
        );
    }

    private void isDownloading(int id) {
        String fileId = "infographic" + id;
        disposable.add(myDatabase.getInstance(getApplication())
                .downloadDao().isWaitingFileExist(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean downloading) {
                        Log.d("is downloading", downloading.toString());
                        downloadLoading.setValue(downloading);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                })
        );
    }


    private void deleteDownloadWaitingList(int id) {
        String fileId = "infographic" + id;
        disposable.add(myDatabase.getInstance(getApplication())
                .downloadDao().deleteWaitingFile(fileId)
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        downloadLoading.postValue(false);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    private void writeFile(InputStream inputString, String filePath, String documentUri, DetailFileDownloadListener listener) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b,0,len);
            }
            fos.flush();
            fos.close();
            inputString.close();
        } catch (FileNotFoundException e) {
            listener.onFailDownload("FileNotFoundException");
        } catch (IOException e) {
            listener.onFailDownload("IOException");
        }
    }

}
