package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lazuardifachri.bps.lekdarjoapp.model.Download;
import com.lazuardifachri.bps.lekdarjoapp.model.FileModel;
import com.lazuardifachri.bps.lekdarjoapp.model.StatisticalNews;
import com.lazuardifachri.bps.lekdarjoapp.model.api.FileDownloadApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;
import com.lazuardifachri.bps.lekdarjoapp.util.DetailFileDownloadListener;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;

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

public class StatisticalNewsDetailViewModel extends AndroidViewModel {

    public MutableLiveData<StatisticalNews> statisticalNewsLiveData = new MutableLiveData<>();

    public MutableLiveData<Uri> filePathUri = new MutableLiveData<>();
    public MutableLiveData<Boolean> downloadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> downloadLoading = new MutableLiveData<>();

    private final CompositeDisposable disposable = new CompositeDisposable();

    public StatisticalNewsDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetchByIdFromDatabase(int uuid) {
        disposable.add(myDatabase.getInstance(getApplication())
                .statisticalNewsDao().getStatisticalNewsByUuid(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<StatisticalNews>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull StatisticalNews statisticalNews) {
                        statisticalNewsLiveData.postValue(statisticalNews);
                        checkIfFileExistFromDatabase(statisticalNews.getId());
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void checkIfStatisticalNewsExistFromDatabase(int uuid) {
        disposable.add(myDatabase.getInstance(getApplication())
                .statisticalNewsDao().isStatisticalNewsExist(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean exist) {
                        if (exist) {
                            fetchByIdFromDatabase(uuid);
                            Log.d("statnews", "exist");
                        }
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
            public String onStartDownload(String fileName, String documentUri) {
                Log.d("listener", "onStartDownload");
                insertDownloadWaitingList(id);
                downloadLoading.setValue(true);
                try {
                    File root = new File(getApplication().getFilesDir().getAbsolutePath() + "/documents");
                    if (!root.exists()) {
                        if (root.mkdirs()) {
                            root.setReadable(true, false);
                            root.setExecutable(true, false);
                        }
                    }
                    File destinationFile = new File(getApplication().getFilesDir().getAbsolutePath() + "/documents/" + fileName);
                    if (!destinationFile.exists()) {
                        if(destinationFile.createNewFile()){
                            destinationFile.setReadable(true, false);
                            destinationFile.setExecutable(true,false);
                        }
                    }
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
                deleteDownloadWaitingList(id);
            }

            @Override
            public void onFailDownload(String errorInfo, String documentUri) {
                downloadLoading.postValue(false);
                downloadError.postValue(true);
                deleteDownloadWaitingList(id);
                Toast.makeText(getApplication(), "Download Failed", Toast.LENGTH_SHORT).show();
            }
        };

        String fileName = title.replaceAll(" ", "").concat(".pdf");
        String filePath = listener.onStartDownload(fileName, documentUri);
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
                        listener.onFailDownload(e.toString(), documentUri);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void insertPathToDatabase(int id, String documentUri, String fileName, String filePath, DetailFileDownloadListener listener) {
        String fileId = "statnews" + id;
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().insertFile(new FileModel(fileId, fileName, filePath))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Long aLong) {
                        checkIfFileExistFromDatabase(id);
                        listener.onFinishDownload(documentUri);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                })
        );
    }

    private void insertDownloadWaitingList(int id) {
        String fileId = "statnews" + id;
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

    private void isDownloading(int id) {
        String fileId = "statnews" + id;
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


    public void deleteDownloadWaitingList(int id) {
        String fileId = "statnews" + id;
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
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len;
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

    public void checkIfFileExistFromDatabase(int id) {
        String fileId = "statnews" + id;
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().isFileExist(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean exist) {
                        if (exist) {
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
        String fileId = "statnews" + id;
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
