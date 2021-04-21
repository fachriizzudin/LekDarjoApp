package com.lazuardifachri.bps.lekdarjoapp.util;

import android.content.Context;
import android.util.Log;

import com.lazuardifachri.bps.lekdarjoapp.model.FileModel;
import com.lazuardifachri.bps.lekdarjoapp.model.api.FileDownloadApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;

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

public class DownloadUtil {

    private Context context;
    private FileDownloadListener listener;

    private final FileDownloadApi fileDownloadApi;
    public CompositeDisposable disposable = new CompositeDisposable();

    private static DownloadUtil instance;

    private DownloadUtil(Context context, FileDownloadListener listener) {
        this.context = context;
        this.listener = listener;
        this.fileDownloadApi = ServiceGenerator.createDownloadService(FileDownloadApi.class, context, listener);
    }

    public static DownloadUtil getInstance(Context context, FileDownloadListener listener) {
        if (instance == null ) {
            instance = new DownloadUtil(context, listener);
        }
        return instance;
    }

    public void fetchFileFromRemote(String url, String fileName) throws IOException {

        Log.d("fileModel", fileName);

        String filePath = listener.onStartDownload(fileName);

        Log.d("filepath", filePath);

        fileDownloadApi.download(url)
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
                        insertPathToDatabase(url, fileName, filePath);
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

    private String getFileIdFromUri(String documentUri) {
        Pattern pattern = Pattern.compile("[^files/]*$");
        Matcher matcher = pattern.matcher(documentUri);
        if (matcher.find()) return matcher.group();
        return null;
    }


    public void insertPathToDatabase(String documentUri, String fileName, String filePath) {

        Log.d("insertPathToDatabase", documentUri);
        Log.d("insertPathToDatabase", fileName);
        String fileId = getFileIdFromUri(documentUri);
        Log.d("insertPathToDatabase", String.valueOf(fileId));
        disposable.add(myDatabase.getInstance(context)
                .fileModelDao().insertFile(new FileModel(fileId, fileName, filePath))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Long aLong) {
                        listener.onFinishDownload();
                        Log.d("insertSuccess", "bisa");
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
}
