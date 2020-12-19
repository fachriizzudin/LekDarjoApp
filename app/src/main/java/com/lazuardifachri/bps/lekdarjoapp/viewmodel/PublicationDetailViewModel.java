package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;
import androidx.room.rxjava3.EmptyResultSetException;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.model.ColorPalette;
import com.lazuardifachri.bps.lekdarjoapp.model.Publication;
import com.lazuardifachri.bps.lekdarjoapp.model.api.PublicationApi;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PublicationDetailViewModel extends AndroidViewModel {

    public MutableLiveData<Publication> publicationLiveData = new MutableLiveData<Publication>();
    public MutableLiveData<ColorPalette> pubPaletteLiveData = new MutableLiveData<ColorPalette>();

    public MutableLiveData<Boolean> publicationExist = new MutableLiveData<>();

    private final CompositeDisposable disposable = new CompositeDisposable();

    public PublicationDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setupBackgroundColor(String url) {
        Glide.with(getApplication())
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        Palette.from(resource).generate(palette -> {
                            int intColor = palette.getDominantColor(ContextCompat.getColor(getApplication(), R.color.blue));
                            ColorPalette pubPalette = new ColorPalette(intColor);
                            pubPaletteLiveData.setValue(pubPalette);
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public void fetchByIdFromDatabase(int uuid) {
        disposable.add(myDatabase.getInstance(getApplication())
                .publicationDao().getPublicationByUuid(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Publication>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Publication publication) {
                        if (publication != null) {
                            publicationLiveData.postValue(publication);
                            Toast.makeText(getApplication(), "Publication retrieved from database", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void checkIfPublicationExistFromDatabase(int uuid) {
        disposable.add(myDatabase.getInstance(getApplication())
                .publicationDao().isPublicationExist(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean exist) {
                        if (exist) {
                            publicationExist.setValue(true);
                        } else {
                            publicationExist.setValue(false);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

}
