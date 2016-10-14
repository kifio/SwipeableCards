package com.cards.kifio.swipeablecards;

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

    private int mInitialLeftMargin, mTargetLeftMargin, mInitialY, mTargetY;

    private View mView;

    public ReviewAnimation(View view, int margin, int targetPosition) {
        mView = view;
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        mInitialLeftMargin = lp.leftMargin;
        mTargetLeftMargin = margin;
        mInitialY = (int) mView.getY();
        mTargetY = mInitialY - targetPosition;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        int leftMargin = (int) (mTargetLeftMargin * interpolatedTime);
        int dy = (int) (mTargetY * interpolatedTime);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mView.getLayoutParams();

        lp.leftMargin = mInitialLeftMargin - leftMargin;
        lp.rightMargin = mInitialLeftMargin - leftMargin;
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