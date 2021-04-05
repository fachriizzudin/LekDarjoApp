package com.lazuardifachri.bps.lekdarjoapp.viewmodel;


import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lazuardifachri.bps.lekdarjoapp.model.StatisticalNews;
import com.lazuardifachri.bps.lekdarjoapp.model.api.StatisticalNewsApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;
import com.lazuardifachri.bps.lekdarjoapp.model.response.StatisticalNewsResponse;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;
import com.lazuardifachri.bps.lekdarjoapp.util.SharedPreferencesHelper;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class StatisticalNewsListViewModel extends AndroidViewModel {

    public MutableLiveData<List<StatisticalNews>> statisticalNewsLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> error = new MutableLiveData<>();
    public MutableLiveData<Boolean> notFound = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    private final StatisticalNewsApi statisticalNewsApi = ServiceGenerator.createService(StatisticalNewsApi.class, getApplication());
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(getApplication());

    public StatisticalNewsListViewModel(@NonNull Application application) {
        super(application);
    }

    private void statisticalNewsRetrieved(List<StatisticalNews> statisticalNews) {
        statisticalNewsLiveData.setValue(statisticalNews);
        error.setValue(false);
        notFound.setValue(false);
        loading.setValue(false);
    }

    public void refresh() {
        long updateTime = preferencesHelper.getNewsUpdateTime();
        long currentTime = System.nanoTime();
        long refreshTimeMonth = 30 * 24 * 60 * 60 * 1000 * 1000 * 1000L;
        if (updateTime != 0 && currentTime - updateTime < refreshTimeMonth) {
            Log.d("prefStat", "database");
            fetchAllFromDatabase();
        } else {
            Log.d("prefStat", "remote");
            fetchAllFromRemote();
        }
    }

    public void fetchAllFromRemote() {
        loading.setValue(true);
        disposable.add(
                statisticalNewsApi.getStatisticalNews()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<StatisticalNewsResponse>() {
                            @Override
                            public void onSuccess(@NonNull StatisticalNewsResponse response) {
                                deleteAllFromDatabase(response.getStatisticalNews());
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

    public void fetchAllFromDatabase() {
        loading.setValue(true);
        disposable.add(myDatabase.getInstance(getApplication())
                .statisticalNewsDao().getAllStatisticalNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<StatisticalNews>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<StatisticalNews> statisticalNews) {
                        if (!statisticalNews.isEmpty()) {
                            statisticalNewsRetrieved(statisticalNews);
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

    public void fetchBySubjectFromDatabase(int subjectId, String monthString, int year) {
        loading.setValue(true);
        String monthYear = "%" + monthString + "-" + year;
        disposable.add(myDatabase.getInstance(getApplication())
                .statisticalNewsDao().getStatisticalNewsBySubject(subjectId, monthYear)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<StatisticalNews>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<StatisticalNews> statisticalNews) {
                        if (!statisticalNews.isEmpty()) {
                            statisticalNewsRetrieved(statisticalNews);
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

    public void fetchBySubjectFromDatabase(int subjectId, int year) {
        loading.setValue(true);
        String monthYear = "%" + year;
        disposable.add(myDatabase.getInstance(getApplication())
                .statisticalNewsDao().getStatisticalNewsBySubject(subjectId, monthYear)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<StatisticalNews>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<StatisticalNews> statisticalNews) {
                        if (!statisticalNews.isEmpty()) {
                            statisticalNewsRetrieved(statisticalNews);
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

    public void fetchByCategoryFromDatabase(int categoryId, String monthString, int year) {
        loading.setValue(true);
        String monthYear = "%" + monthString + "-" + year;
        disposable.add(myDatabase.getInstance(getApplication())
                .statisticalNewsDao().getStatisticalNewsByCategory(categoryId, monthYear)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<StatisticalNews>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<StatisticalNews> statisticalNews) {
                        if (!statisticalNews.isEmpty()) {
                            statisticalNewsRetrieved(statisticalNews);
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

    public void fetchByMonthYearFromDatabase(String monthString, int year) {
        loading.setValue(true);
        String monthYear = "%" + monthString + "-" + year;
        disposable.add(myDatabase.getInstance(getApplication())
                .statisticalNewsDao().getStatisticalNewsByMonthYear(monthYear)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<StatisticalNews>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<StatisticalNews> statisticalNews) {
                        if (!statisticalNews.isEmpty()) {
                            statisticalNewsRetrieved(statisticalNews);
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

    public void fetchByMonthYearFromDatabase(int year) {
        loading.setValue(true);
        String monthYear = "%" + year;
        disposable.add(myDatabase.getInstance(getApplication())
                .statisticalNewsDao().getStatisticalNewsByMonthYear(monthYear)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<StatisticalNews>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<StatisticalNews> statisticalNews) {
                        if (!statisticalNews.isEmpty()) {
                            statisticalNewsRetrieved(statisticalNews);
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

    public void insertAllToDatabase(List<StatisticalNews> statisticalNews) {
        disposable.add(myDatabase.getInstance(getApplication())
                .statisticalNewsDao().insertAll(statisticalNews)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Long>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Long> results) {
                        int i = 0;
                        while (i < statisticalNews.size()) {
                            statisticalNews.get(i).setUuid(results.get(i).intValue());
                            i++;
                        }
                        preferencesHelper.saveNewsUpdateTime(System.nanoTime());
                        fetchAllFromDatabase();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }
                }));

    }

    public void deleteAllFromDatabase(List<StatisticalNews> statisticalNews) {
        disposable.add(myDatabase.getInstance(getApplication())
                .statisticalNewsDao().deleteAllStatisticalNews()
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        insertAllToDatabase(statisticalNews);
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
