package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;
import com.lazuardifachri.bps.lekdarjoapp.model.api.GraphDataApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;
import com.lazuardifachri.bps.lekdarjoapp.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GraphViewModel extends AndroidViewModel {

    public MutableLiveData<GraphData> economyGrowthDataLive = new MutableLiveData<GraphData>();
    public MutableLiveData<GraphData> ipmDataLive = new MutableLiveData<GraphData>();
    public MutableLiveData<GraphData> pdrbKonstanDataLive = new MutableLiveData<GraphData>();
    public MutableLiveData<GraphData> morbidityDataLive = new MutableLiveData<GraphData>();
    public MutableLiveData<GraphData> lifeExpectancyDataLive = new MutableLiveData<GraphData>();

    public MutableLiveData<Boolean> error = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> notFound = new MutableLiveData<Boolean>();

    public MutableLiveData<Integer> counter = new MutableLiveData<Integer>();

    private final GraphDataApi graphDataApi = ServiceGenerator.createService(GraphDataApi.class, getApplication());
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(getApplication());
    private final long refreshTimeMinute = 3 * 60 * 1000 * 1000 * 1000L;

    public GraphViewModel(@NonNull Application application) {
        super(application);
    }

    private void graphDataRetrieved(GraphData graphData, int metaId) {

        switch (metaId) {
            case 1:
                economyGrowthDataLive.setValue(graphData);
                break;
            case 2:
                ipmDataLive.setValue(graphData);
                break;
            case 3:
                pdrbKonstanDataLive.setValue(graphData);
                break;
            case 4:
                morbidityDataLive.setValue(graphData);
                break;
            case 5:
                lifeExpectancyDataLive.setValue(graphData);
                break;
        }

    }

    public void refresh() {
        long updateTime = preferencesHelper.getUpdateTime();
        long currentTime = System.nanoTime();

        if (updateTime != 0 && currentTime - updateTime < refreshTimeMinute) {
            counter.setValue(0);
            for (int i = 1; i <= 5; i++) {
                fetchByMetaIdFromDatabase(i);
            }

        } else {
            counter.setValue(0);
            for (int i = 1; i <= 5; i++) {
                fetchByMetaIdFromRemote(i);
            }
        }
    }

    public void refresh(int metaId) {
        long updateTime = preferencesHelper.getUpdateTime();
        long currentTime = System.nanoTime();

        if (updateTime != 0 && currentTime - updateTime < refreshTimeMinute) {
            counter.setValue(0);
            fetchByMetaIdFromDatabase(metaId);
        } else {
            counter.setValue(0);
            fetchByMetaIdFromRemote(metaId);
        }
    }

    public void fetchByMetaIdFromRemote(int metaId) {
        loading.setValue(true);
            disposable.add(
                    graphDataApi.getGraphData(metaId)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<GraphData>() {
                                @Override
                                public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull GraphData graphData) {

                                    deleteByMetaIdFromDatabase(graphData, metaId);
                                    Log.d("graphData", graphData.toString());
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

    public void fetchByMetaIdFromDatabase(int metaId) {
        loading.setValue(true);
        disposable.add(myDatabase.getInstance(getApplication())
                .graphDataDao().getGraphDataByMetaId(metaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<GraphData>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull GraphData graphData) {
                        counter.setValue(counter.getValue()+1);
                        graphDataRetrieved(graphData, metaId);
                        Log.d("graphData", graphData.toString());
                        Toast.makeText(getApplication(), "GraphData retrieved from database", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        error.setValue(true);
                        loading.setValue(false);
                        e.printStackTrace();
                    }
                }));
    }

    public void insertAllToDatabase(GraphData graphData, int metaId) {
        disposable.add(myDatabase.getInstance(getApplication())
                .graphDataDao().insertALl(graphData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Long results) {
                        preferencesHelper.saveUpdateTime(System.nanoTime());
                        fetchByMetaIdFromDatabase(metaId);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }
                }));

    }

    public void deleteByMetaIdFromDatabase(GraphData graphData, int metaid) {
        Log.d("delete", "masuk");
        disposable.add(myDatabase.getInstance(getApplication())
                .graphDataDao().deleteByMetaIdGraphData(metaid)
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        insertAllToDatabase(graphData, metaid);
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
