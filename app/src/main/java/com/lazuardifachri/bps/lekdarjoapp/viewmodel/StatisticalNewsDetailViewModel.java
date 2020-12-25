package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lazuardifachri.bps.lekdarjoapp.model.ColorPalette;
import com.lazuardifachri.bps.lekdarjoapp.model.StatisticalNews;
import com.lazuardifachri.bps.lekdarjoapp.model.StatisticalNews;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class StatisticalNewsDetailViewModel extends AndroidViewModel {

    public MutableLiveData<StatisticalNews> statisticalNewsLiveData = new MutableLiveData<StatisticalNews>();
    public MutableLiveData<Boolean> statisticalNewsExist = new MutableLiveData<>();

    private final CompositeDisposable disposable = new CompositeDisposable();

    public StatisticalNewsDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetchByIdFromDatabase(int uuid) {
        disposable.add(myDatabase.getInstance(getApplication())
                .statisticalNewsDao().getStatisticalNewsByUuid(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<StatisticalNews>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull StatisticalNews statisticalNews) {
                        if (statisticalNews != null) {
                            statisticalNewsLiveData.postValue(statisticalNews);
                            Toast.makeText(getApplication(), "StatisticalNews retrieved from database", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void checkIfStatisticalNewsExistFromDatabase(int uuid) {
        disposable.add(myDatabase.getInstance(getApplication())
                .statisticalNewsDao().isStatisticalNewsExist(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean exist) {
                        if (exist) {
                            statisticalNewsExist.setValue(true);
                        } else {
                            statisticalNewsExist.setValue(false);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

}
