package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lazuardifachri.bps.lekdarjoapp.model.Publication;
import com.lazuardifachri.bps.lekdarjoapp.model.api.PublicationApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;
import com.lazuardifachri.bps.lekdarjoapp.model.response.PublicationResponse;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;
import com.lazuardifachri.bps.lekdarjoapp.util.SharedPreferencesHelper;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PublicationListViewModel extends AndroidViewModel {

    public MutableLiveData<List<Publication>> publicationLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> error = new MutableLiveData<>();
    public MutableLiveData<Boolean> notFound = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    private final PublicationApi publicationApi = ServiceGenerator.createService(PublicationApi.class, getApplication());
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(getApplication());

    public PublicationListViewModel(@NonNull Application application) {
        super(application);
    }

    private void publicationRetrieved(List<Publication> publications) {
        publicationLiveData.setValue(publications);
        error.setValue(false);
        notFound.setValue(false);
        loading.setValue(false);
    }

    public void refresh() {
        long updateTime = preferencesHelper.getPubUpdateTime();
        long currentTime = System.nanoTime();
        long refreshTime = 15 * 24 * 60 * 60 * 1000 * 1000 * 1000L;
        if (updateTime != 0 && currentTime - updateTime < refreshTime) {
            fetchAllFromDatabase();
        } else {
            fetchAllFromRemote();
        }
    }

    public void fetchAllFromRemote() {
        loading.setValue(true);
        disposable.add(
                publicationApi.getPublications()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PublicationResponse>() {
                            @Override
                            public void onSuccess(@NonNull PublicationResponse response) {
                                deleteAllFromDatabase(response.getPublications());
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
                .publicationDao().getAllPublication()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Publication>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Publication> publications) {
                        if (!publications.isEmpty()) {
                            publicationRetrieved(publications);
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

    public void fetchByFilterFromDatabase(int subjectId, String districtCode, int year) {
        loading.setValue(true);
        String likeYear = "%" + year;
        disposable.add(myDatabase.getInstance(getApplication())
                .publicationDao().getPublicationByFilter(subjectId, districtCode, likeYear)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Publication>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Publication> publications) {
                        if (!publications.isEmpty()) {
                            publicationRetrieved(publications);
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
        String likeYear = "%" + year;
        disposable.add(myDatabase.getInstance(getApplication())
                .publicationDao().getPublicationBySubject(subjectId, likeYear)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Publication>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Publication> publications) {
                        if (!publications.isEmpty()) {
                            publicationRetrieved(publications);
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

    public void fetchByDistrictFromDatabase(String districtCode, int year) {
        loading.setValue(true);
        String likeYear = "%" + year;
        disposable.add(myDatabase.getInstance(getApplication())
                .publicationDao().getPublicationByDistrict(districtCode, likeYear)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Publication>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Publication> publications) {
                        if (!publications.isEmpty()) {
                            publicationRetrieved(publications);
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

    public void fetchByYearOnlyFromDatabase(int year) {
        loading.setValue(true);
        String likeYear = "%" + year;
        disposable.add(myDatabase.getInstance(getApplication())
                .publicationDao().getPublicationByYear(likeYear)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Publication>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Publication> publications) {
                        if (!publications.isEmpty()) {
                            publicationRetrieved(publications);
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

    public void insertAllToDatabase(List<Publication> publications) {
        disposable.add(myDatabase.getInstance(getApplication())
                .publicationDao().insertAll(publications)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Long>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Long> results) {
                        if (!results.isEmpty()) {
                            int i = 0;
                            while (i < publications.size()) {
                                publications.get(i).setUuid(results.get(i).intValue());
                                i++;
                            }
                            preferencesHelper.savePubUpdateTime(System.nanoTime());
                            fetchAllFromDatabase();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));

    }

    public void deleteAllFromDatabase(List<Publication> publications) {
        disposable.add(myDatabase.getInstance(getApplication())
                .publicationDao().deleteAllPublications()
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        insertAllToDatabase(publications);
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
