package com.lazuardifachri.bps.lekdarjoapp.view;

import android.util.Log;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

public abstract class FabOffsetter implements AppBarLayout.OnOffsetChangedListener {

    private final CoordinatorLayout parent;
    private final ProgressFloatingActionButton fab;

    public FabOffsetter(CoordinatorLayout parent, ProgressFloatingActionButton fab) {
        this.parent = parent;
        this.fab = fab;
    }

    private <T> T coalesce(T ...items) {
        for(T i : items) if(i != null) return i;
        return null;
    }

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE,
        HIDE,
        SHOW
    }

    private State mCurrentState = State.IDLE;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        Log.d("minHeight", String.valueOf(appBarLayout.getMinimumHeightForVisibleOverlappingContent()));

        Log.d("verticalOffset", String.valueOf(verticalOffset));
        Log.d("appbarheight", String.valueOf(appBarLayout.getHeight()));

//        if (Math.abs(verticalOffset)>400) {
//            fab.animate().setDuration(25).scaleY(0.0f);
//            fab.animate().setDuration(25).scaleX(0.0f);
//        } else {
//            fab.animate().setDuration(25).scaleY(1.0f);
//            fab.animate().setDuration(25).scaleX(1.0f);
//        }
        if (verticalOffset == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;

        } else if (Math.abs(verticalOffset) >= appBarLayout.getHeight()-appBarLayout.getMinimumHeight()-50) {
            if (mCurrentState != State.HIDE) {
                onStateChanged(appBarLayout, State.HIDE);
            }
            mCurrentState = State.HIDE;
        } else if (Math.abs(verticalOffset) < appBarLayout.getHeight()-appBarLayout.getMinimumHeightForVisibleOverlappingContent()+50) {
            if (mCurrentState != State.SHOW) {
                onStateChanged(appBarLayout, State.SHOW);
            }
            mCurrentState = State.SHOW;
        } else {
            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE);
            }
            mCurrentState = State.IDLE;
        }





//        float displacementFraction = verticalOffset / (float) appBarLayout.getHeight();
//
//        Log.d("displacementFraction", String.valueOf(displacementFraction));
//
//        float translationYFromThis = coalesce((Float) fab.getTag(R.id.downloadActionFab),0f);
//
//        Log.d("translationYFromThis", String.valueOf(translationYFromThis));
//
//        float topUntranslatedFromThis = fab.getTop() + fab.getTranslationY() - translationYFromThis;
//
//        float fullDisplacement = parent.getBottom() - topUntranslatedFromThis;
//
//        float newTranslationYFromThis = fullDisplacement * displacementFraction;
//
//        fab.setTag(R.id.downloadActionFab, newTranslationYFromThis);
//
//        fab.setScaleY((newTranslationYFromThis - translationYFromThis + fab.getTranslationY())/100);
//        fab.setScaleX((newTranslationYFromThis - translationYFromThis + fab.getTranslationY())/100);
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
}
