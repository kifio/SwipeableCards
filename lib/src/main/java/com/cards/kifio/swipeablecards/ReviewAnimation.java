package com.cards.kifio.swipeablecards;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by kifio on 7/6/16.
 */
// TODO: refactoring
public class ReviewAnimation extends Animation {

    private static final String TAG = "RG-ReviewAnimation";

    private float mInitialMargin, mMarginDiff, mInitialY, mYDiff;

    private View mView;
    private ViewGroup.MarginLayoutParams mLp;

    ReviewAnimation(View view, float marginDiff) {

        mView = view;
        mLp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        mInitialMargin = mLp.leftMargin;
        mInitialY = mView.getY();

        Log.d(TAG, "marginDiff: " + marginDiff);

        mMarginDiff = marginDiff;
        mYDiff = marginDiff;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        float leftMargin = mMarginDiff * interpolatedTime;
        float dy = mYDiff * interpolatedTime;

        mLp.leftMargin = (int) (mInitialMargin - leftMargin);
        mLp.rightMargin = (int) (mInitialMargin - leftMargin);

        mView.setY(mInitialY - dy);
        mView.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}