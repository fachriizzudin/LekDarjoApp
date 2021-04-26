package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lazuardifachri.bps.lekdarjoapp.model.Graph;
import com.lazuardifachri.bps.lekdarjoapp.model.api.GraphApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GraphDetailViewModel extends AndroidViewModel {

    public MutableLiveData<Graph> graphLive = new MutableLiveData<>();
    public MutableLiveData<Integer> counter = new MutableLiveData<>();
    public MutableLiveData<Boolean> error = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    private final CompositeDisposable disposable = new CompositeDisposable();

    private final GraphApi graphApi = ServiceGenerator.createService(GraphApi.class, getApplication());

    public GraphDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetchByUuidFromDatabase(int uuid) {
        loading.setValue(true);
        disposable.add(myDatabase.getInstance(getApplication())
                .graphDao().getGraphDataByUuid(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Graph>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Graph graph) {
                        graphLive.setValue(graph);
                        counter.setValue(1);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        error.setValue(true);
                        loading.setValue(false);
                        e.printStackTrace();
                    }
                }));
    }


}
