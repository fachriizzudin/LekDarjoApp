package com.lazuardifachri.bps.lekdarjoapp.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lazuardifachri.bps.lekdarjoapp.R;

import java.util.List;

public class ProgressFloatingActionButton extends FrameLayout implements CoordinatorLayout.AttachedBehavior {

    private ProgressBar mProgressBar;
    private FloatingActionButton mFab;
    private Animation zoomIn = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);;
    private Animation zoomOut = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);;

    public ProgressFloatingActionButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (getChildCount() == 0 || getChildCount() > 2) {
            throw new IllegalStateException("Specify only 2 views.");
        }

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof ProgressBar) {
                mProgressBar = (ProgressBar) view;
            } else if (view instanceof FloatingActionButton) {
                mFab = (FloatingActionButton) view;
            } else {
                throw new IllegalStateException("Specify FAB and Progress Bar" +
                        "as view's children in your layout.");
            }
        }

        if (mFab == null) {
            throw new IllegalStateException("Floating Action Button not specified");
        } else if (mProgressBar == null) {
            throw new IllegalStateException("Progress Bar not specified");
        }

        resize();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mFab != null && mProgressBar != null) {
            resize();
        }
    }

    private void resize() {
        float translationZpx = getResources().getDisplayMetrics().density * 6; // 6 is needed for progress bar to be visible, 5 doesn't work
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP)
            mProgressBar.setTranslationZ(translationZpx);

        LayoutParams mFabParams = ((LayoutParams) mFab.getLayoutParams());
        LayoutParams mProgressParams = ((LayoutParams) mProgressBar.getLayoutParams());

        int additionSize = getResources().getDimensionPixelSize(R.dimen.progress_bar_size);
        mProgressBar.getLayoutParams().height = mFab.getHeight() + additionSize;
        mProgressBar.getLayoutParams().width = mFab.getWidth() + additionSize;

        mFabParams.gravity = Gravity.CENTER;
        mProgressParams.gravity = Gravity.CENTER;
    }

    private void hide() {
//        zoomIn = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
//        this.setAnimation(zoomIn);
//        View view = this;
        this.animate().scaleX(0.0f).scaleY(0.0f);
        Log.d("hide", "dihidekok");
        this.setVisibility(INVISIBLE);

//        View view = this;
//        view.animate()
//                .scaleX(0.0f).scaleY(0.0f)
//                .setDuration(300)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        view.setVisibility(View.INVISIBLE);
//                    }
//                });

    }

    private void show() {
        this.clearAnimation();
//        zoomOut = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
//        this.setAnimation(zoomOut);
        this.animate().scaleX(1.0f).scaleY(1.0f);
        this.setVisibility(VISIBLE);
    }

    @NonNull
    @Override
    public CoordinatorLayout.Behavior getBehavior() {
        return new Behavior();
    }


    /**
     * Created by: Dmitry Malkovich
     * Thanks to https://lab.getbase.com/introduction-to-coordinator-layout-on-android/
     */
    public static class Behavior extends CoordinatorLayout.Behavior<ProgressFloatingActionButton> {
        public Behavior() {
            super();
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
//
        @Override
        public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull ProgressFloatingActionButton child, @NonNull View dependency) {

            if (dependency instanceof  AppBarLayout) {
                ((AppBarLayout) dependency).addOnOffsetChangedListener(new FabOffsetter(parent, child) {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        if(child.getVisibility() == View.VISIBLE && state == State.HIDE) {
                            child.hide();
                        }else if(child.getVisibility() == View.INVISIBLE && state == State.SHOW) {
                            Log.d("show", "showing");
                            child.show();
                        }
                    }
                });
            }


            return dependency instanceof AppBarLayout || super.layoutDependsOn(parent,child, dependency);
        }

        @Override
        public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull ProgressFloatingActionButton child, @NonNull View dependency) {

            // float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
            float distanceY = getViewOffsetForAppBar(parent, child);
            float complete = distanceY / dependency.getHeight();
            float scaleFactor = 1 - complete;

//            Log.d("distanceY", String.valueOf(distanceY));
//            Log.d("onDependentViewChanged", String.valueOf(complete));
//            Log.d("scaleFactor", String.valueOf(scaleFactor));


//            child.setScaleX(scaleFactor);
//            child.setScaleY(scaleFactor);

            return super.onDependentViewChanged(parent, child, dependency);
        }

        private float getViewOffsetForAppBar(CoordinatorLayout parent, View view) {
            float maxOffset = 0;
            List<View> dependencies = parent.getDependencies(view);

            for (View dependency : dependencies) {
//                Log.d("dependency", String.valueOf(dependency.getTranslationY()));
                if (dependency instanceof AppBarLayout && parent.doViewsOverlap(view, dependency)){
                    maxOffset = Math.max(maxOffset, (dependency.getTranslationY() - dependency.getHeight()) * -1);
                }
            }
//            Log.d("maxOffset", String.valueOf(maxOffset));
            return maxOffset;
        }

//        @Override
//        public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ProgressFloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
//            Log.d("onStartNestedScroll", "start");
//            return axes == ViewCompat.SCROLL_AXIS_VERTICAL ||
//                    super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
//                            axes, type);
//        }
//
//
//        @Override
//        public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ProgressFloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
//            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
//            Log.d("onNestedScroll", "start");
//            Log.d("dyConsumed", String.valueOf(dyConsumed));
//            Log.d("dyUnconsumed", String.valueOf(dyUnconsumed));
//            Log.d("target high", String.valueOf(target.getHeight()));
//            Log.d("target translation", String.valueOf(target.getScrollY()));
//            Log.d("child", String.valueOf(child.getScrollY()));
//
//
//            if (dyUnconsumed > 0 && child.getVisibility() == View.VISIBLE) {
//                Log.d("child", "visible");
//                child.hide();
//            } else if (dyUnconsumed < 0 && child.getVisibility() != View.VISIBLE) {
//                Log.d("child", "invisible");
//                child.show();
//            }
//        }

    }

}
