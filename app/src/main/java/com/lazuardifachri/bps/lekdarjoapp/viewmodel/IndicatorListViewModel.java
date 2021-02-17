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
import com.lazuardifachri.bps.lekdarjoapp.model.FileModel;
import com.lazuardifachri.bps.lekdarjoapp.model.Indicator;
import com.lazuardifachri.bps.lekdarjoapp.model.api.FileDownloadApi;
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
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static androidx.core.content.FileProvider.getUriForFile;

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
        long refreshTime = 30 * 24 * 60 * 60 * 1000 * 1000 * 1000L;
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

    public void fetchByCategoryFromDatabase(int categoryId, String monthString, int year) {
        loading.setValue(true);
        String monthYear = "%" + monthString + "-" + year;
        disposable.add(myDatabase.getInstance(getApplication())
                .indicatorDao().getIndicatorByCategory(categoryId, monthYear)
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

    public void fetchByCategoryFromDatabase(int categoryId, int year) {
        loading.setValue(true);
        String likeYear = "%" + year;
        disposable.add(myDatabase.getInstance(getApplication())
                .indicatorDao().getIndicatorByCategory(categoryId, likeYear)
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

    public void fetchByMonthYearFromDatabase(int subjectId, String month, int year) {
        loading.setValue(true);
        String monthYear = "%" + month + "-" + year;
        disposable.add(myDatabase.getInstance(getApplication())
                .indicatorDao().getIndicatorByMonthYear(subjectId, monthYear)
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

    public void fetchByYearFromDatabase(int subjectId, int year) {
        loading.setValue(true);
        String likeYear = "%" + year;
        disposable.add(myDatabase.getInstance(getApplication())
                .indicatorDao().getIndicatorByMonthYear(subjectId, likeYear)
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

    public void fetchFileFromRemote(String documentUri, String title, MaterialButton downloadButton, ProgressBar downloadProgressBar) throws IOException {

        int fileId = StringUtil.getFileIdFromUri(documentUri);

        FileDownloadListener indicatorListener = new FileDownloadListener() {
            @Override
            public String onStartDownload(String fileName) throws IOException {
                downloadProgressBar.setVisibility(View.VISIBLE);
                Log.d("indiOnStart", "run");
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
                downloadProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinishDownload() {
                Log.d("indiOnFinish", "run");

                downloadProgressBar.setVisibility(View.INVISIBLE);
                fetchFileNameFromDatabase(fileId, downloadButton);
            }

            @Override
            public void onFailDownload(String errorInfo) {
                Log.d("errorInfo", errorInfo);
                Toast.makeText(getApplication(), "Download Failed", Toast.LENGTH_SHORT).show();
            }
        };

        FileDownloadApi indicatorFileDownloadApi = ServiceGenerator.createDownloadService(FileDownloadApi.class, getApplication(), indicatorListener);
        String fileName = title.replaceAll(" ", "").concat(".xls");;
        String filePath = indicatorListener.onStartDownload(fileName);

        indicatorFileDownloadApi.download(documentUri)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(responseBody -> responseBody.byteStream()).observeOn(Schedulers.computation())
                .doOnNext(inputStream -> writeFile(inputStream, filePath, indicatorListener)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull InputStream inputStream) {
                        insertPathToDatabase(documentUri, fileName, filePath, indicatorListener);
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

    public void insertPathToDatabase(String documentUri, String fileName, String filePath, FileDownloadListener downloadListener) {
        int fileId = StringUtil.getFileIdFromUri(documentUri);
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

    public void fetchFileNameFromDatabase(int fileId, MaterialButton button) {
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

    public void checkIfFileExistFromDatabase(String documentUri, MaterialButton button) {
        int fileId = StringUtil.getFileIdFromUri(documentUri);
        String feature = StringUtils.substringBetween(documentUri, "api/", "/");
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().isIndicatorExist(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Integer>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Integer exist) {
                        if (exist > 0) fetchFileNameFromDatabase(fileId, button);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    private void readFile(Uri uri) {
        Intent ExcelIntent = new Intent(Intent.ACTION_VIEW);
        ExcelIntent.setDataAndType(uri, "application/vnd.ms-excel");
        ExcelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ExcelIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ExcelIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        ExcelIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(getApplication(), uri.getPath(), Toast.LENGTH_SHORT).show();
        try {
            getApplication().startActivity(ExcelIntent);
        } catch (Exception e) {
            Toast.makeText(getApplication(), "No Application available to view Excel", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
