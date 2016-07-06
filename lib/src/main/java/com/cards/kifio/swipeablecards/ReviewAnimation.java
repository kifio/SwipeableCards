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

    private int mTargetLeftMargin;
    private int mInitialLeftMargin;
    private int mInitialY;

    private int mTargetPosition;

    private View mView;
    ViewGroup.MarginLayoutParams lp;

    public ReviewAnimation(View view, int targetLeftMargin, int targetPosition) {

        mView = view;
        lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();


        mTargetLeftMargin = lp.leftMargin - targetLeftMargin;
        mInitialLeftMargin = lp.leftMargin;
        mInitialY = (int) mView.getY();
        mTargetPosition = (int) (mView.getY() - targetPosition);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        int leftMargin = (int) (mTargetLeftMargin * interpolatedTime);
        int dy = (int) (mTargetPosition * interpolatedTime);

        lp.leftMargin = mInitialLeftMargin - leftMargin;
        lp.rightMargin = mInitialLeftMargin - leftMargin;
        mView.setY(mInitialY - dy);

        mView.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}