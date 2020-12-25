package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lazuardifachri.bps.lekdarjoapp.model.Indicator;
import com.lazuardifachri.bps.lekdarjoapp.model.api.IndicatorApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;
import com.lazuardifachri.bps.lekdarjoapp.model.response.IndicatorResponse;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;
import com.lazuardifachri.bps.lekdarjoapp.util.SharedPreferencesHelper;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class IndicatorListViewModel extends AndroidViewModel {

    public MutableLiveData<List<Indicator>> indicatorLiveData = new MutableLiveData<List<Indicator>>();
    public MutableLiveData<Boolean> error = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> notFound = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();

    private final IndicatorApi indicatorApi = ServiceGenerator.createService(IndicatorApi.class, getApplication());
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(getApplication());
    private final long refreshTimeMonth = 30 * 24 * 60 * 60 * 1000 * 1000 * 1000L;

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
        long updateTime = preferencesHelper.getUpdateTime();
        long currentTime = System.nanoTime();
        if (updateTime != 0 && currentTime - updateTime < refreshTimeMonth) {
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
                                Toast.makeText(getApplication(), "Statistical news retrieved from endpoint", Toast.LENGTH_SHORT).show();
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
                            Log.d("indicators", indicators.toString());
                            Toast.makeText(getApplication(), "Indicator retrieved from database with id = " +subjectId, Toast.LENGTH_SHORT).show();
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
        String monthYear = "%" + monthString + "-" + year;
        loading.setValue(true);
        disposable.add(myDatabase.getInstance(getApplication())
                .indicatorDao().getIndicatorBySubject(subjectId, monthYear)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Indicator>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Indicator> indicators) {
                        if (!indicators.isEmpty()) {
                            indicatorRetrieved(indicators);
                            Toast.makeText(getApplication(), "Indicator retrieved from database", Toast.LENGTH_SHORT).show();
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

    public void fetchBySubjectFromDatabase(int subjectId, int year) {
        String monthYear = "%" + year;
        loading.setValue(true);
        disposable.add(myDatabase.getInstance(getApplication())
                .indicatorDao().getIndicatorBySubject(subjectId, monthYear)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Indicator>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Indicator> indicators) {
                        if (!indicators.isEmpty()) {
                            indicatorRetrieved(indicators);
                            Toast.makeText(getApplication(), "Indicator retrieved from database", Toast.LENGTH_SHORT).show();
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
                            Log.d("indicators", indicators.toString());
                            Toast.makeText(getApplication(), "Indicator retrieved from database", Toast.LENGTH_SHORT).show();
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
                            Log.d("indicators", indicators.toString());
                            Toast.makeText(getApplication(), "Indicator retrieved from database", Toast.LENGTH_SHORT).show();
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
                            Log.d("indicators", indicators.toString());
                            Toast.makeText(getApplication(), "Indicator retrieved from database", Toast.LENGTH_SHORT).show();
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
                            Log.d("indicators", indicators.toString());
                            Toast.makeText(getApplication(), "Indicator retrieved from database", Toast.LENGTH_SHORT).show();
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
                        Log.d("insertAll", "success");
                        int i = 0;
                        while (i < indicators.size()) {
                            indicators.get(i).setUuid(results.get(i).intValue());
                            i++;
                        }
                        preferencesHelper.saveUpdateTime(System.nanoTime());
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

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
