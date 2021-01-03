package com.lazuardifachri.bps.lekdarjoapp.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.model.ColorPalette;
import com.lazuardifachri.bps.lekdarjoapp.model.Infographic;
import com.lazuardifachri.bps.lekdarjoapp.model.myDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InfographicDetailViewModel extends AndroidViewModel {

    public MutableLiveData<Infographic> infographicLiveData = new MutableLiveData<Infographic>();
    public MutableLiveData<ColorPalette> pubPaletteLiveData = new MutableLiveData<ColorPalette>();

    public MutableLiveData<Boolean> infographicExist = new MutableLiveData<>();

    private final CompositeDisposable disposable = new CompositeDisposable();

    public InfographicDetailViewModel(@NonNull Application application) {
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
                            int intColor = palette.getLightVibrantColor(ContextCompat.getColor(getApplication(), R.color.blue));
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
                .infographicDao().getInfographicByUuid(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Infographic>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Infographic infographic) {
                        if (infographic != null) {
                            infographicLiveData.postValue(infographic);
                            Toast.makeText(getApplication(), "Infographic retrieved from database", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void checkIfInfographicExistFromDatabase(int uuid) {
        disposable.add(myDatabase.getInstance(getApplication())
                .infographicDao().isInfographicExist(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean exist) {
                        if (exist) {
                            infographicExist.setValue(true);
                        } else {
                            infographicExist.setValue(false);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

}
