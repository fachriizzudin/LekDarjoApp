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
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.model.FileModel;
import com.lazuardifachri.bps.lekdarjoapp.model.Infographic;
import com.lazuardifachri.bps.lekdarjoapp.model.api.FileDownloadApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;
import com.lazuardifachri.bps.lekdarjoapp.util.FileDownloadListener;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;
import com.lazuardifachri.bps.lekdarjoapp.view.list_fragment.InfographicListFragmentDirections;
import com.lazuardifachri.bps.lekdarjoapp.view.list_fragment.PublicationListFragmentDirections;

import org.apache.commons.lang3.StringUtils;

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

public class FileModelViewModel extends AndroidViewModel {

    //

    public MutableLiveData<Uri> filePathUri = new MutableLiveData<>();
    public MutableLiveData<Boolean> fileExist = new MutableLiveData<>();
    public MutableLiveData<Boolean> downloadError = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> downloadLoading = new MutableLiveData<Boolean>();
    public MutableLiveData<Integer> downloadProgress = new MutableLiveData<Integer>();
    public MutableLiveData<Boolean> downloadComplete = new MutableLiveData<Boolean>();

    FileDownloadListener listener = new FileDownloadListener() {
        @Override
        public String onStartDownload(String fileName) throws IOException {
            Log.d("listener", "onStartDownload");
            downloadLoading.setValue(true);
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
        public void onProgressDownload(int progress) {
            Log.d("listener", "onProgressDownload");
            downloadLoading.postValue(true);
            downloadProgress.postValue(progress);
        }

        @Override
        public void onFinishDownload() {
            Log.d("listener", "onFinishDownload");
            downloadLoading.setValue(false);
            // downloadComplete.postValue(true);
        }

        @Override
        public void onFailDownload(String errorInfo) {
            Log.d("listener", "onFailDownload");
            Log.d("errorInfo", errorInfo);
            downloadLoading.postValue(false);
            Log.d("errorInfo", errorInfo);
            downloadError.setValue(true);
        }
    };

    private final FileDownloadApi fileDownloadApi = ServiceGenerator.createDownloadService(FileDownloadApi.class, getApplication(), listener);
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

    public void fetchFileFromRemote(String documentUri, String title) throws IOException {

        String feature = StringUtils.substringBetween(documentUri, "api/", "/");
        String fileName;
        if (feature.equals("publications") || feature.equals("statnews")) {
            fileName = title.replaceAll(" ", "").concat(".pdf");
        } else if (feature.equals("infographics")) {
            fileName = title.replaceAll(" ", "").concat(".png");
        } else {
            fileName = "file.tmp";
        }

        String filePath = listener.onStartDownload(fileName);

        Log.d("fetchFileFromRemote", filePath);
        Log.d("fetchFileFromRemote", fileName);

        fileDownloadApi.download(documentUri)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(ResponseBody responseBody) throws Throwable {
                        return responseBody.byteStream();
                    }
                }).observeOn(Schedulers.computation())
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Throwable {
                        writeFile(inputStream, filePath);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull InputStream inputStream) {
                        insertPathToDatabase(documentUri, fileName, filePath);
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


    public void fetchFileFromRemote(String documentUri, String title, MaterialButton downloadButton, ProgressBar downloadProgressBar) throws IOException {

        Log.d("fetchExcelFromRemote", "run");
        int fileId = getFileIdFromUri(documentUri);
        String feature = StringUtils.substringBetween(documentUri, "api/", "/");
        FileDownloadListener indicatorListener = new FileDownloadListener() {
            @Override
            public String onStartDownload(String fileName) throws IOException {
                downloadProgressBar.setVisibility(View.VISIBLE);
                Log.d("indiOnStart", "run");
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
            public void onProgressDownload(int progress) {
                Log.d("indiOnProgress", "run");
                downloadProgressBar.setVisibility(View.VISIBLE);
                downloadProgressBar.setProgress(progress);
            }

            @Override
            public void onFinishDownload() {
                downloadProgressBar.setVisibility(View.INVISIBLE);
                fetchFileNameFromDatabase(fileId, downloadButton, feature);
            }

            @Override
            public void onFailDownload(String errorInfo) {
                Toast.makeText(getApplication(), "Download Error", Toast.LENGTH_SHORT).show();
            }
        };

        FileDownloadApi indicatorFileDownloadApi = ServiceGenerator.createDownloadService(FileDownloadApi.class, getApplication(), indicatorListener);



        String fileName;

        if (feature.equals("indicators")) {
            fileName = title.replaceAll(" ", "").concat(".xls");
        } else {
            fileName = "file.tmp";
        }

        String filePath = indicatorListener.onStartDownload(fileName);

        indicatorFileDownloadApi.download(documentUri)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(ResponseBody responseBody) throws Throwable {
                        return responseBody.byteStream();
                    }
                }).observeOn(Schedulers.computation())
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Throwable {
                        writeFile(inputStream, filePath, indicatorListener, feature);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull InputStream inputStream) {
                        insertPathToDatabase(documentUri, fileName, filePath, indicatorListener, feature);
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

    public void insertPathToDatabase(String documentUri, String fileName, String filePath) {

        int fileId = getFileIdFromUri(documentUri);

        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().insertFile(new FileModel(fileId, fileName, filePath))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Long aLong) {
                        fetchFileNameFromDatabase(documentUri);
                        listener.onFinishDownload();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                })
        );
    }

    public void insertPathToDatabase(String documentUri, String fileName, String filePath, FileDownloadListener downloadListener, String feature) {

        int fileId = getFileIdFromUri(documentUri);

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

    private void writeFile(InputStream inputString, String filePath) {
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

    private void writeFile(InputStream inputString, String filePath, FileDownloadListener downloadListener, String feature) {
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

    public void fetchFileNameFromDatabase(String documentUri) {
        int fileId = getFileIdFromUri(documentUri);
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
                        e.printStackTrace();
                    }
                }));
    }

    public void fetchFileNameFromDatabase(int fileId, MaterialButton button, String feature) {
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().getFileName(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull String fileName) {
                        File fileDir = new File(getApplication().getFilesDir(), "document");
                        File filePath = new File(fileDir, fileName);
                        Uri contentUri = getUriForFile(getApplication(), "com.lazuardifachri.bps.fileprovider", filePath);

                        Log.d("contentUri", contentUri.getPath());
                        Log.d("fileName", fileName);

                        button.setIcon(getApplication().getResources().getDrawable(R.drawable.ic_view));
                        button.setOnClickListener(v -> {
                            if (feature.equals("publications")) {
                                readFile(contentUri);
                            }
                        });

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
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
                            Log.d("fileExist", "true");
                            fileExist.setValue(true);
                        } else {
                            Log.d("fileExist", "false");
                            fileExist.setValue(false);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }


    public void checkIfFileExistFromDatabase(String documentUri, MaterialButton button) {
        int fileId = getFileIdFromUri(documentUri);
        String feature = StringUtils.substringBetween(documentUri, "api/", "/");
        disposable.add(myDatabase.getInstance(getApplication())
                .fileModelDao().isIndicatorExist(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Integer>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Integer exist) {
                        if (exist > 0) fetchFileNameFromDatabase(fileId, button, feature);
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
        try {
            getApplication().startActivity(ExcelIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplication(), "No Application available to view Excel", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
