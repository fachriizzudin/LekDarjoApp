package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.lazuardifachri.bps.lekdarjoapp.model.Publication;
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

import static androidx.core.content.FileProvider.getUriForFile;

public class PublicationDetailViewModel extends AndroidViewModel {

    public MutableLiveData<Publication> publicationLiveData = new MutableLiveData<>();
    public MutableLiveData<ColorPalette> pubPaletteLiveData = new MutableLiveData<>();

    public MutableLiveData<Uri> filePathUri = new MutableLiveData<>();
    public MutableLiveData<Boolean> downloadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> downloadLoading = new MutableLiveData<>();

    private final CompositeDisposable disposable = new CompositeDisposable();

    public PublicationDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setupBackgroundColor(String url) {
        Glide.with(getApplication())
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Palette.from(resource).generate(palette -> {
                            if (palette != null) {
                                int intColor = palette.getDominantColor(ContextCompat.getColor(getApplication(), R.color.blue));
                                ColorPalette pubPalette = new ColorPalette(intColor);
                                Log.d("palette", "setValue");
                                pubPaletteLiveData.setValue(pubPalette);
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public void fetchByIdFromDatabase(int uuid) {
        disposable.add(myDatabase.getInstance(getApplication())
                .publicationDao().getPublicationByUuid(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Publication>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Publication publication) {
                        Log.d("publication", "setValue");
                        publicationLiveData.setValue(publication);
                        setupBackgroundColor(publication.getImageUri());
                        checkIfFileExistFromDatabase(publication.getDocumentUri());
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }


    // harus pakai check dulu, jika langsung fetch dapat terjadi bug detail kosong
    public void checkIfPublicationExistFromDatabase(int uuid) {
        disposable.add(myDatabase.getInstance(getApplication())
                .publicationDao().isPublicationExist(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean exist) {
                        if (exist) fetchByIdFromDatabase(uuid);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void fetchFileFromRemote(String documentUri, String title) throws IOException {

        DetailFileDownloadListener listener = new DetailFileDownloadListener() {
            @Override
            public String onStartDownload(String fileName, String documentUri) throws IOException {
                Log.d("listener", "onStartDownload");
                insertDownloadWaitingList(documentUri);
                downloadLoading.setValue(true);
                try {
                    File root = new File(getApplication().getFilesDir().getAbsolutePath() + "/documents");
                    if (!root.exists()) root.mkdirs();
                    File destinationFile = new File(getApplication().getFilesDir().getAbsolutePath() + "/documents/" + fileName);
                    if (!destinationFile.exists()) destinationFile.createNewFile();
                    return destinationFile.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onProgressDownload(int progress) {
                downloadLoading.postValue(true);
            }

            @Override
            public void onFinishDownload(String documentUri) {
                Log.d("listener", "onFinishDownload");
                downloadLoading.postValue(false);
                deleteDownloadWaitingList(documentUri);
            }

            @Override
            public void onFailDownload(String errorInfo, String documentUri) {
                downloadLoading.postValue(false);
                downloadError.postValue(true);
                deleteDownloadWaitingList(documentUri);
                Log.d("fragment loading", "yes");
                Toast.makeText(getApplication(), "Download Failed", Toast.LENGTH_SHORT).show();
            }
        };

        String fileName = title.replaceAll(" ", "").concat(".pdf");
        String filePath = listener.onStartDownload(fileName, documentUri);
        FileDownloadApi fileDownloadApi = ServiceGenerator.createDetailDownloadService(FileDownloadApi.class, getApplication(), listener);

        fileDownloadApi.download(documentUri)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(responseBody -> responseBody.byteStream()).observeOn(Schedulers.computation())
                .doOnNext(inputStream -> writeFile(inputStream, filePath, documentUri, listener)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull InputStream inputStream) {
                        insertPathToDatabase(documentUri, fileName, filePath, listener);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        listener.onFailDownload(e.toString(), documentUri);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void insertPathToDatabase(String documentUri, String fileName, String filePath, DetailFileDownloadListener listener) {
        int fileId = StringUtil.getFileIdFromUri(documentUri);
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().insertFile(new FileModel(fileId, fileName, filePath))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Long aLong) {
                        checkIfFileExistFromDatabase(documentUri);
                        listener.onFinishDownload(documentUri);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                })
        );
    }

    private void insertDownloadWaitingList(String documentUri) {
        int fileId = StringUtil.getFileIdFromUri(documentUri);
        disposable.add(myDatabase.getInstance(getApplication())
                .downloadDao().insertWaitingFile(new Download(fileId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Long aLong) {
                        downloadLoading.setValue(true);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                })
        );
    }

    private void isDownloading(String documentUri) {
        int fileId = StringUtil.getFileIdFromUri(documentUri);
        Log.d("fileIdDownloading", String.valueOf(fileId));
        disposable.add(myDatabase.getInstance(getApplication())
                .downloadDao().isWaitingFileExist(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean downloading) {
                        downloadLoading.setValue(downloading);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                })
        );
    }


    public void deleteDownloadWaitingList(String documentUri) {
        int fileId = StringUtil.getFileIdFromUri(documentUri);
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
            listener.onFailDownload("FileNotFoundException", documentUri);
        } catch (IOException e) {
            listener.onFailDownload("IOException", documentUri);
        }
    }

    public void checkIfFileExistFromDatabase(String documentUri) {
        int fileId = StringUtil.getFileIdFromUri(documentUri);
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().isFileExist(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean exist) {
                        if (exist) {
                            fetchFileNameFromDatabase(documentUri);
                        }
                        // harus tetap dijalankan untuk kasus berganti ke detail yang sudah didownload
                        isDownloading(documentUri);

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void fetchFileNameFromDatabase(String documentUri) {
        int fileId = StringUtil.getFileIdFromUri(documentUri);
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().getFileName(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull String fileName) {
                        File fileDir = new File(getApplication().getFilesDir(), "documents");
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


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}

