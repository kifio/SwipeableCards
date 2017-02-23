package com.cards.kifio.swipeablecards.anim;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

/**
 * Created by kifio on 7/6/16.
 */

public class ResizeAnimation extends Animation {

    private static final String TAG = "RG-ResizeAnimation";

    private float mWidthDiff;
    private float mYDiff;
    private float mStartY;
    private float mStartWidth;

    private View mView;
    private FrameLayout.LayoutParams mLp;

    public ResizeAnimation(View view, float marginDiff, float yDiff) {

        mView = view;
        mLp = (FrameLayout.LayoutParams) view.getLayoutParams();

        mStartY = view.getY();
        mStartWidth = view.getWidth();

        mWidthDiff = 2 * marginDiff;
        mYDiff = yDiff;
        Log.d(TAG, "ResizeAnimation: " + yDiff + "; position: " + view.getTag() + "; view.getY: " + view.getY());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        mLp.width = (int) (mStartWidth + mWidthDiff * interpolatedTime);
        Log.d(TAG, "applyTransformation: " + (mStartY - mYDiff * interpolatedTime));
        mView.setY(mStartY - mYDiff * interpolatedTime);
        mView.requestLayout();
    }
}