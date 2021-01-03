package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lazuardifachri.bps.lekdarjoapp.model.Infographic;
import com.lazuardifachri.bps.lekdarjoapp.model.api.InfographicApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;
import com.lazuardifachri.bps.lekdarjoapp.util.SharedPreferencesHelper;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InfographicListViewModel extends AndroidViewModel {

    public MutableLiveData<List<Infographic>> infographicLiveData = new MutableLiveData<List<Infographic>>();
    public MutableLiveData<Boolean> error = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> notFound = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();

    private final InfographicApi infographicApi = ServiceGenerator.createService(InfographicApi.class, getApplication());
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(getApplication());
    private final long refreshTimeMonth = 30 * 24 * 60 * 60 * 1000 * 1000 * 1000L;

    public InfographicListViewModel(@NonNull Application application) {
        super(application);
    }

    private void infographicRetrieved(List<Infographic> infographics) {
        infographicLiveData.setValue(infographics);
        error.setValue(false);
        notFound.setValue(false);
        loading.setValue(false);
    }

    public void refresh() {
        long updateTime = preferencesHelper.getUpdateTime();
        long currentTime = System.nanoTime();
        if (updateTime != 0 && currentTime - updateTime < refreshTimeMonth) {
            fetchAllFromDatabase();
        } else {
            fetchAllFromRemote();
        }
    }

    public void fetchAllFromRemote() {
        loading.setValue(true);
        disposable.add(
                infographicApi.getInfographic()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Infographic>>() {
                            @Override
                            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Infographic> infographics) {
                                deleteAllFromDatabase(infographics);
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


    public void fetchAllFromDatabase() {
        loading.setValue(true);
        disposable.add(myDatabase.getInstance(getApplication())
                .infographicDao().getAllInfographic()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Infographic>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Infographic> infographics) {
                        if (!infographics.isEmpty()) {
                            infographicRetrieved(infographics);
                            Toast.makeText(getApplication(), "Publication retrieved from database", Toast.LENGTH_SHORT).show();
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

    public void insertAllToDatabase(List<Infographic> infographics) {
        disposable.add(myDatabase.getInstance(getApplication())
                .infographicDao().insertAll(infographics)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Long>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Long> results) {
                        preferencesHelper.saveUpdateTime(System.nanoTime());
                        fetchAllFromDatabase();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }
                }));

    }

    public void deleteAllFromDatabase(List<Infographic> infographics) {
        Log.d("delete", "masuk");
        disposable.add(myDatabase.getInstance(getApplication())
                .infographicDao().deleteAllInfographics()
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        insertAllToDatabase(infographics);
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
