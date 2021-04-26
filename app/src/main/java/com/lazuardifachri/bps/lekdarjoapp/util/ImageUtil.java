package com.lazuardifachri.bps.lekdarjoapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.lazuardifachri.bps.lekdarjoapp.R;


public class ImageUtil {

    @BindingAdapter("android:imageUrl")
    public static void loadImage(ImageView view, String url) {
        if (url!=null) {
            RequestOptions options = new RequestOptions()
                    .placeholder(getProgressDrawable(view.getContext()))
                    .error(R.drawable.ic_picture);

            GlideUrl glideUrl = new GlideUrl(url,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + SharedPreferencesHelper.getInstance(view.getContext()).fetchAuthToken())
                            .build());

            Glide.with(view.getContext())
                    .setDefaultRequestOptions(options)
                    .load(glideUrl)
                    .into(view);
        }
    }

    @BindingAdapter("android:infographicUrl")
    public static void loadInfographic(ImageView imageView, String url) {
        if (url!=null) {
            RequestOptions options = new RequestOptions()
                    .placeholder(getProgressDrawable(imageView.getContext()))
                    .error(R.drawable.ic_picture);

            GlideUrl glideUrl = new GlideUrl(url,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + SharedPreferencesHelper.getInstance(imageView.getContext()).fetchAuthToken())
                            .build());

            Glide.with(imageView.getContext())
                    .setDefaultRequestOptions(options)
                    .asBitmap()
                    .load(glideUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(resource);
                            imageView.buildDrawingCache();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });

            imageView.setOnTouchListener(new ImageMatrixTouchHandler(imageView.getContext()));
        }
    }

    @BindingAdapter("android:imageSVGUrl")
    public static void loadImageSVG(ImageView view, String url) {
        if (url!=null) {
            RequestBuilder<PictureDrawable> requestBuilder = GlideToVectorYou
                    .init()
                    .with(view.getContext())
                    .setPlaceHolder(R.drawable.ic_picture, R.drawable.ic_picture)
                    .getRequestBuilder();

            GlideUrl glideUrl = new GlideUrl(url,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + SharedPreferencesHelper.getInstance(view.getContext()).fetchAuthToken())
                            .build());

            requestBuilder
                    .load(glideUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view);
        }
    }

    @BindingAdapter("android:newsSubject")
    public static void loadImage(ImageView view, int subjectId) {
        switch (subjectId) {
            case 1:
                view.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.ic_social));
                break;
            case 2:
                view.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.ic_economy));
                break;
            case 3:
                view.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.ic_agriculture));
                break;
        }
    }

    public static CircularProgressDrawable getProgressDrawable(Context context) {
        CircularProgressDrawable cpd = new CircularProgressDrawable(context);
        cpd.setStrokeWidth(10f);
        cpd.setCenterRadius(50f);
        cpd.start();
        return cpd;
    }
}
