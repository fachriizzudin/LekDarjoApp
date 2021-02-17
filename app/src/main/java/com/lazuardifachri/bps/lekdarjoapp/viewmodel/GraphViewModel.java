package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lazuardifachri.bps.lekdarjoapp.model.Graph;
import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;
import com.lazuardifachri.bps.lekdarjoapp.model.api.GraphDataApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;
import com.lazuardifachri.bps.lekdarjoapp.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GraphViewModel extends AndroidViewModel {

    public MutableLiveData<GraphData> economyGrowthDataLive = new MutableLiveData<GraphData>();
    public MutableLiveData<GraphData> malePopulationDataLive = new MutableLiveData<GraphData>();
    public MutableLiveData<GraphData> femalePopulationDataLive = new MutableLiveData<GraphData>();
    public MutableLiveData<GraphData> povertyDataLive = new MutableLiveData<GraphData>();
    public MutableLiveData<GraphData> unemploymentDataLive = new MutableLiveData<GraphData>();
    public MutableLiveData<GraphData> ipmDataLive = new MutableLiveData<GraphData>();
    public MutableLiveData<GraphData> riceProductionDataLive = new MutableLiveData<GraphData>();
    public MutableLiveData<GraphData> riceProductivityDataLive = new MutableLiveData<GraphData>();

    public MutableLiveData<GraphData> populationDataLive = new MutableLiveData<GraphData>();
    public MutableLiveData<List<GraphData>> mixPopulationDataLive = new MutableLiveData<List<GraphData>>();

    public MutableLiveData<Integer> counter = new MutableLiveData<Integer>();
    public MutableLiveData<Integer> populationCounter = new MutableLiveData<Integer>();

    private final GraphDataApi graphDataApi = ServiceGenerator.createService(GraphDataApi.class, getApplication());
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(getApplication());
    private final long refreshTime = 15 * 24 * 60 * 60 * 1000 * 1000 * 1000L;

    public GraphViewModel(@NonNull Application application) {
        super(application);
    }

    private GraphData mergeData(GraphData gd1, GraphData gd2) {
        List<Graph> mergeGraph = new ArrayList<>();
        for (int i = 0; i < gd1.getData().size(); i++) {
            mergeGraph.add(new Graph(gd1.getData().get(i).getId(),
                    gd1.getData().get(i).getValue() + gd2.getData().get(i).getValue(),
                    gd1.getData().get(i).getYear()));
        }
        return new GraphData(mergeGraph, gd1.getMeta(), gd1.getUuid());
    }

    private void graphDataRetrieved(GraphData graphData, int metaId) {
        switch (metaId) {
            case 1:
                economyGrowthDataLive.setValue(graphData);
                break;
            case 2:
                malePopulationDataLive.setValue(graphData);
                break;
            case 3:
                femalePopulationDataLive.setValue(graphData);
                break;
            case 4:
                povertyDataLive.setValue(graphData);
                break;
            case 5:
                unemploymentDataLive.setValue(graphData);
                break;
            case 6:
                ipmDataLive.setValue(graphData);
                break;
            case 7:
                riceProductionDataLive.setValue(graphData);
                break;
            case 8:
                riceProductivityDataLive.setValue(graphData);
                break;
        }

        if (populationCounter.getValue() == 2) {
            if (malePopulationDataLive.getValue() != null && femalePopulationDataLive.getValue() != null) {
                populationDataLive.setValue(mergeData(malePopulationDataLive.getValue(), femalePopulationDataLive.getValue()));
                List<GraphData> mix = new ArrayList<>();
                mix.add(malePopulationDataLive.getValue());
                mix.add(femalePopulationDataLive.getValue());
                mixPopulationDataLive.setValue(mix);
            }
        }

    }

    public void fetchAllFromRemote() {
        counter.setValue(0);
        populationCounter.setValue(0);
        for (int i = 1; i <= 8; i++) {
            fetchByMetaIdFromRemote(i);
        }
    }


    public void refresh() {
        long updateTime = preferencesHelper.getGraphUpdateTime();
        long currentTime = System.nanoTime();

        if (updateTime != 0 && currentTime - updateTime < refreshTime) {
            counter.setValue(0);
            populationCounter.setValue(0);
            for (int i = 1; i <= 8; i++) {
                fetchByMetaIdFromDatabase(i);
            }

        } else {
            counter.setValue(0);
            populationCounter.setValue(0);
            for (int i = 1; i <= 8; i++) {
                fetchByMetaIdFromRemote(i);
            }
        }
    }

    public void refresh(int metaId) {
        long updateTime = preferencesHelper.getGraphUpdateTime();
        long currentTime = System.nanoTime();

        if (updateTime != 0 && currentTime - updateTime < refreshTime) {
            counter.setValue(0);
            populationCounter.setValue(0);
            fetchByMetaIdFromDatabase(metaId);
        } else {
            counter.setValue(0);
            populationCounter.setValue(0);
            fetchByMetaIdFromRemote(metaId);
        }
    }

    public void fetchByMetaIdFromRemote(int metaId) {
        disposable.add(
                graphDataApi.getGraphData(metaId)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<GraphData>() {
                            @Override
                            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull GraphData graphData) {
                                deleteByMetaIdFromDatabase(graphData, metaId);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                e.printStackTrace();
                            }
                        })
        );

    }

    public void fetchByMetaIdFromDatabase(int metaId) {
        disposable.add(myDatabase.getInstance(getApplication())
                .graphDataDao().getGraphDataByMetaId(metaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<GraphData>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull GraphData graphData) {
                        counter.setValue(counter.getValue() + 1);
                        if (metaId == 2 || metaId == 3) populationCounter.setValue(populationCounter.getValue() + 1);
                        graphDataRetrieved(graphData, metaId);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
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
                        preferencesHelper.saveGraphUpdateTime(System.nanoTime());
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
