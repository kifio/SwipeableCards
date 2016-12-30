package com.cards.kifio.swipeablecards.anim;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by kifio on 7/6/16.
 */

public class ResizeAnimation extends Animation {

    private static final String TAG = "RG-ResizeAnimation";

    private float mInitialHorizontalMargin, mInitialVerticalMargin, mMarginDiff, mYDiff;

    private View mView;
    private ViewGroup.MarginLayoutParams mLp;

    public ResizeAnimation(View view, float marginDiff, float yDiff) {

        mView = view;
        mLp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        mInitialHorizontalMargin = mLp.leftMargin;
        mInitialVerticalMargin = mLp.topMargin;

        mMarginDiff = marginDiff;
        mYDiff = yDiff;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        float leftMargin = mMarginDiff * interpolatedTime;
        float dy = mYDiff * interpolatedTime;

        mLp.leftMargin = (int) (mInitialHorizontalMargin - leftMargin);
        mLp.rightMargin = (int) (mInitialHorizontalMargin - leftMargin);
        mLp.topMargin = (int) (mInitialVerticalMargin - dy);

        mView.requestLayout();
    }
}