package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.button.MaterialButton;
import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.model.Download;
import com.lazuardifachri.bps.lekdarjoapp.model.FileModel;
import com.lazuardifachri.bps.lekdarjoapp.model.Indicator;
import com.lazuardifachri.bps.lekdarjoapp.model.Infographic;
import com.lazuardifachri.bps.lekdarjoapp.model.api.FileDownloadApi;
import com.lazuardifachri.bps.lekdarjoapp.model.api.FileHeaderApi;
import com.lazuardifachri.bps.lekdarjoapp.model.api.IndicatorApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;
import com.lazuardifachri.bps.lekdarjoapp.model.response.IndicatorResponse;
import com.lazuardifachri.bps.lekdarjoapp.util.FileDownloadListener;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;
import com.lazuardifachri.bps.lekdarjoapp.util.SharedPreferencesHelper;
import com.lazuardifachri.bps.lekdarjoapp.util.StringUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DefaultObserver;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.lazuardifachri.bps.lekdarjoapp.util.Constant.refreshTime;

public class IndicatorListViewModel extends AndroidViewModel {

    public MutableLiveData<List<Indicator>> indicatorLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> error = new MutableLiveData<>();
    public MutableLiveData<Boolean> notFound = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    private final IndicatorApi indicatorApi = ServiceGenerator.createService(IndicatorApi.class, getApplication());
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(getApplication());

    public IndicatorListViewModel(@NonNull Application application) {
        super(application);
    }

    private void indicatorRetrieved(List<Indicator> indicators) {
        indicatorLiveData.setValue(indicators);
        error.setValue(false);
        notFound.setValue(false);
        loading.setValue(false);
    }

    public void refresh(int subjectId) {
        long updateTime = preferencesHelper.getIdxUpdateTime();
        long currentTime = System.nanoTime();

        if (updateTime != 0 && currentTime - updateTime < refreshTime) {
            fetchBySubjectFromDatabase(subjectId);
        } else {
            fetchAllFromRemote(subjectId);
        }
    }

    public void fetchAllFromRemote(int subjectId) {
        loading.setValue(true);
        disposable.add(
                indicatorApi.getIndicators()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<IndicatorResponse>() {
                            @Override
                            public void onSuccess(@NonNull IndicatorResponse response) {
                                Log.d("response indicators", String.valueOf(response.getIndicators().size()));
                                deleteAllFromDatabase(response.getIndicators(), subjectId);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                error.setValue(true);
                                loading.setValue(false);

                                e.printStackTrace();
                            }
                        })
        );
    }

    public void fetchBySubjectFromDatabase(int subjectId) {
        loading.setValue(true);
        disposable.add(myDatabase.getInstance(getApplication())
                .indicatorDao().getIndicatorBySubject(subjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Indicator>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Indicator> indicators) {
                        if (!indicators.isEmpty()) {
                            indicatorRetrieved(indicators);
                        } else {
                            notFound.setValue(true);
                            loading.setValue(false);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        error.setValue(true);
                        loading.setValue(false);
                        e.printStackTrace();
                    }
                }));
    }

    public void fetchByCategoryFromDatabase(int categoryId) {
        loading.setValue(true);
        disposable.add(myDatabase.getInstance(getApplication())
                .indicatorDao().getIndicatorByCategory(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Indicator>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Indicator> indicators) {
                        if (!indicators.isEmpty()) {
                            indicatorRetrieved(indicators);
                        } else {
                            notFound.setValue(true);
                            loading.setValue(false);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        notFound.setValue(true);
                        loading.setValue(false);
                        e.printStackTrace();
                    }
                }));
    }

    public void insertAllToDatabase(List<Indicator> indicators, int subjectId) {
        disposable.add(myDatabase.getInstance(getApplication())
                .indicatorDao().insertAll(indicators)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Long>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Long> results) {
                        int i = 0;
                        while (i < indicators.size()) {
                            indicators.get(i).setUuid(results.get(i).intValue());
                            i++;
                        }
                        preferencesHelper.saveIdxUpdateTime(System.nanoTime());
                        // fetch by subject from database
                        fetchBySubjectFromDatabase(subjectId);
                        // and then ... indicatorsRetrieved(indicators);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }
                }));

    }

    public void deleteAllFromDatabase(List<Indicator> indicators, int subjectId) {
        Log.d("delete", "masuk");
        disposable.add(myDatabase.getInstance(getApplication())
                .indicatorDao().deleteAllIndicator()
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        insertAllToDatabase(indicators, subjectId);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void fetchFileFromRemote(int id, String documentUri, String title, MaterialButton downloadButton, ProgressBar downloadProgressBar) throws IOException {
        //String fileId = StringUtil.getFileIdFromUri(documentUri);
        String fileId = "indicator" + id;

        FileDownloadListener indicatorListener = new FileDownloadListener() {
            @Override
            public String onStartDownload(String fileName) {
                //insertDownloadWaitingList(id);
                downloadProgressBar.setVisibility(View.VISIBLE);
                Log.d("indiOnStart", "run");
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
                downloadProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinishDownload() {
                Log.d("indiOnFinish", "run");
                downloadProgressBar.setVisibility(View.INVISIBLE);
                fetchFileNameFromDatabase(fileId, downloadButton);
                //deleteDownloadWaitingList(id);
            }

            @Override
            public void onFailDownload(String errorInfo) {
                Log.d("errorInfo", errorInfo);
                //deleteDownloadWaitingList(id);
                Toast.makeText(getApplication(), "Download Failed", Toast.LENGTH_SHORT).show();
            }

        };

        FileHeaderApi fileHeaderApi = ServiceGenerator.createService(FileHeaderApi.class, getApplication());

        fileHeaderApi.lookup(documentUri)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DefaultObserver<ResponseBody>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ResponseBody responseBody) {
                        Log.d("contentType", responseBody.contentType().toString());
                        String fileName = title.replaceAll(" ", "");
                        if (responseBody.contentType().equals("application/vnd.ms-excel")) {
                            fileName = fileName + ".xls";
                        } else {
                            fileName = fileName + ".xlsx";
                        }
                        Log.d("filename", fileName);
                        try {
                            String filePath = indicatorListener.onStartDownload(fileName);
                            startDownload(id, documentUri, fileName, filePath, indicatorListener);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void startDownload(int id, String documentUri, String fileName, String filePath, FileDownloadListener indicatorListener) {
        FileDownloadApi indicatorFileDownloadApi = ServiceGenerator.createDownloadService(FileDownloadApi.class, getApplication(), indicatorListener);
        indicatorFileDownloadApi.download(documentUri)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(responseBody ->  responseBody.byteStream()).observeOn(Schedulers.computation())
                .doOnNext(inputStream -> writeFile(inputStream, filePath, indicatorListener)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull InputStream inputStream) {
                        insertPathToDatabase(id, fileName, filePath, indicatorListener);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        indicatorListener.onFailDownload(e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void insertPathToDatabase(int id, String fileName, String filePath, FileDownloadListener downloadListener) {
        String fileId = "indicator" + id;
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().insertFile(new FileModel(fileId, fileName, filePath))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Long aLong) {
                        downloadListener.onFinishDownload();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                })
        );
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
                        loading.setValue(true);
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
                        loading.setValue(downloading);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                })
        );
    }


    public void deleteDownloadWaitingList(int id) {
        String fileId = "infographic" + id;
        disposable.add(myDatabase.getInstance(getApplication())
                .downloadDao().deleteWaitingFile(fileId)
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        loading.postValue(false);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    private void writeFile(InputStream inputString, String filePath, FileDownloadListener downloadListener) {
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
            downloadListener.onFailDownload("FileNotFoundException");
        } catch (IOException e) {
            downloadListener.onFailDownload("IOException");
        }

    }

    public void fetchFileNameFromDatabase(String fileId, MaterialButton button) {
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
                        button.setIcon(getApplication().getResources().getDrawable(R.drawable.ic_view));
                        button.setOnClickListener(v -> {
                            readFile(contentUri);
                        });
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void checkIfFileExistFromDatabase(int id, MaterialButton button) {
        String fileId = "indicator" + id;
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().isIndicatorExist(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Integer>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Integer exist) {
                        Log.d("filename-"+ fileId, String.valueOf(exist));
                        if (exist > 0) {
                            fetchFileNameFromDatabase(fileId, button);
                            button.setIcon(getApplication().getResources().getDrawable(R.drawable.ic_view));
                        } else {
                            button.setIcon(getApplication().getResources().getDrawable(R.drawable.ic_download));
                        }
                        isDownloading(id);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    private void readFile(Uri uri) {
        Log.d("uri", uri.getPath());
        Intent ExcelIntent = new Intent(Intent.ACTION_VIEW);

        if (getExtensionByStringHandling(uri.getPath()).equals("xls")) {
            Log.d("excel", "xls");
            ExcelIntent.setDataAndType(uri, "application/vnd.ms-excel");
        } else {
            Log.d("excel", getExtensionByStringHandling(uri.getPath()));
            ExcelIntent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }

        ExcelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ExcelIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ExcelIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        ExcelIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            getApplication().startActivity(ExcelIntent);
        } catch (Exception e) {
            Toast.makeText(getApplication(), "No Application available to view Excel", Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtensionByStringHandling(String uri) {
        return uri.substring(uri.lastIndexOf(".") + 1);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
