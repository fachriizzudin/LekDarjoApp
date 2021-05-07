package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lazuardifachri.bps.lekdarjoapp.model.Graph;
import com.lazuardifachri.bps.lekdarjoapp.model.api.GraphApi;
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

import static com.lazuardifachri.bps.lekdarjoapp.util.Constant.refreshTime;

public class GraphListViewModel extends AndroidViewModel {

    public MutableLiveData<List<Graph>> graphLive = new MutableLiveData<>();
    public MutableLiveData<Long> counter = new MutableLiveData<>();
    public MutableLiveData<Boolean> error = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    private final GraphApi graphApi = ServiceGenerator.createService(GraphApi.class, getApplication());

    private final CompositeDisposable disposable = new CompositeDisposable();

    private final SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(getApplication());

    public GraphListViewModel(@NonNull Application application) {
        super(application);
    }

    private void graphRetrieved(Graph graph) {
        graphLive.getValue().add(graph);
        if (graphLive.getValue().size() == counter.getValue()) {
            graphLive.setValue(new ArrayList<>(graphLive.getValue()));
            loading.setValue(false);
            error.setValue(false);
        }
    }

    public void refresh() {
        Log.d("GraphList", "refresh");
        long updateTime = preferencesHelper.getGraphUpdateTime();
        long currentTime = System.nanoTime();
        if (updateTime != 0 && currentTime - updateTime < refreshTime) {
            fetchAllFromDatabase();
        } else {
            fetchAllFromRemote();
        }
    }

    public void fetchAllFromRemote() {
        loading.setValue(true);
        error.setValue(false);
        disposable.add(
                graphApi.getGraphMetaCount().subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Long>() {
                            @Override
                            public void onSuccess(@NonNull Long count) {
                                graphLive.setValue(new ArrayList<>());
                                counter.setValue(count);
                                preferencesHelper.saveGraphUpdateTime(System.nanoTime());
                                for (int i = 1; i <= count; i++) {
                                    fetchBySerialNumberFromRemote(i);
                                }
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

    public void fetchBySerialNumberFromRemote(int serialNumber) {
        disposable.add(
                graphApi.getGraph(serialNumber)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Graph>() {
                            @Override
                            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Graph graph) {
                                Log.d("fetch from remote", "success");
                                deleteBySerialNumberFromDatabase(graph, serialNumber);
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

    public void deleteBySerialNumberFromDatabase(Graph graph, int serialNumber) {
        disposable.add(myDatabase.getInstance(getApplication())
                .graphDao().deleteByMetaIdGraphData(serialNumber)
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d("delete by", "complete");
                        insertAllToDatabase(graph, serialNumber);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        error.setValue(true);
                        loading.setValue(false);
                        e.printStackTrace();
                    }
                }));
    }

    public void insertAllToDatabase(Graph graph, int serialNumber) {
        disposable.add(myDatabase.getInstance(getApplication())
                .graphDao().insertAll(graph)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Long results) {
                        Log.d("insert all", "success");
                        preferencesHelper.saveGraphUpdateTime(System.nanoTime());
                        fetchBySerialNumberFromDatabase(serialNumber);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }
                }));

    }

    public void fetchBySerialNumberFromDatabase(int serialNumber) {
        disposable.add(myDatabase.getInstance(getApplication())
                .graphDao().getGraphDataByMetaId(serialNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Graph>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Graph graph) {
                        graphRetrieved(graph);
                        Log.d("after add", String.valueOf(graphLive.getValue().size()));
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        error.setValue(true);
                        loading.setValue(false);
                        e.printStackTrace();
                    }
                }));
    }

    public void fetchAllFromDatabase() {
        Log.d("GraphList", "fetch database");
        loading.setValue(true);
        disposable.add(myDatabase.getInstance(getApplication())
                .graphDao().getCount().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Integer>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Integer count) {
                        graphLive.setValue(new ArrayList<>());
                        counter.setValue(Long.valueOf(count));
                        for (int i = 1; i <= count; i++) {
                            fetchBySerialNumberFromDatabase(i);
                        }
                        Log.d("GraphList", "fetch database success");
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        error.setValue(true);
                        loading.setValue(false);
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
