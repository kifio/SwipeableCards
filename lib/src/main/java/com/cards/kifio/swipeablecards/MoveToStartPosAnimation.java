package com.cards.kifio.swipeablecards;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import static com.cards.kifio.swipeablecards.CardsView.DEFAULT_DEGREES_VALUE;

/**
 * Created by kifio on 28.12.16.
 */

public class MoveToStartPosAnimation extends Animation {

    private View mView;
    private float mX, mY;
    private int mDx, mDy, mXLimit;

    public MoveToStartPosAnimation(View view, int x, int y, int dx, int dy, int xLimit) {
        mView = view;
        mX = x;
        mY = y;
        mDx = dx;
        mDy = dy;
        mXLimit = xLimit;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if (interpolatedTime != 1.0 && mView != null) {
            moveAndRotate((mX * (1 - interpolatedTime)) + mDx, (mY * (1 - interpolatedTime)) + mDy);
        } else {
            mView = null;
        }
    }

    private void moveAndRotate(float newX, float newY) {
        mView.setRotation((int) ((DEFAULT_DEGREES_VALUE * (newX - mDx)) / mXLimit));
        mView.setX(newX);
        mView.setY(newY);
    }


}
