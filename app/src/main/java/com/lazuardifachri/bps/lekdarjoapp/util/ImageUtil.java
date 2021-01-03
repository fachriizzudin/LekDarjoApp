package com.lazuardifachri.bps.lekdarjoapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.palette.graphics.Palette;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.model.ColorPalette;

public class ImageUtil {
    public static void loadImage(ImageView imageView, String url, CircularProgressDrawable progressDrawable, Context context) {

        RequestOptions options = new RequestOptions()
                .placeholder(progressDrawable)
                .error(R.mipmap.ic_launcher);

        GlideUrl glideUrl = new GlideUrl(url,
                new LazyHeaders.Builder()
                        .addHeader("Authorization", "Bearer " + SharedPreferencesHelper.getInstance(context).fetchAuthToken())
                        .build());

        Glide.with(imageView.getContext())
                .setDefaultRequestOptions(options)
                .load(glideUrl)
                .into(imageView);
    }

    public static CircularProgressDrawable getProgressDrawable(Context context) {
        CircularProgressDrawable cpd = new CircularProgressDrawable(context);
        cpd.setStrokeWidth(10f);
        cpd.setCenterRadius(50f);
        cpd.start();
        return cpd;
    }

    @BindingAdapter("android:imageUrl")
    public static void loadImage(ImageView view, String url) {
        if (url!=null) {
            loadImage(view, url, getProgressDrawable(view.getContext()), view.getContext());
        }
    }

    @BindingAdapter("android:infographicUrl")
    public static void loadInfographic(ImageView imageView, String url) {
        if (url!=null) {
            loadImage(imageView, url, getProgressDrawable(imageView.getContext()), imageView.getContext());
            RequestOptions options = new RequestOptions()
                    .placeholder(getProgressDrawable(imageView.getContext()))
                    .error(R.mipmap.ic_launcher);

            GlideUrl glideUrl = new GlideUrl(url,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + SharedPreferencesHelper.getInstance(imageView.getContext()).fetchAuthToken())
                            .build());

            Glide.with(imageView.getContext())
                    .setDefaultRequestOptions(options)
                    .load(glideUrl)
                    .into(imageView);

            imageView.setOnTouchListener(new ImageMatrixTouchHandler(imageView.getContext()));
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

}
