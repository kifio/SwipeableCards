package com.cards.kifio.swipeablecards;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import java.util.Locale;

import static com.cards.kifio.swipeablecards.CardsView.DEFAULT_DEGREES_VALUE;

/**
 * Created by kifio on 28.12.16.
 */

public class MoveToStartPosAnimation extends Animation {

    private View mView;
    private int mX, mY;
    private int mDx, mDy, mXLimit;

    public MoveToStartPosAnimation(View view, float currentX, float currentY, float startX, float startY, float xLimit) {
        mView = view;
        mX = (int) currentX;
        mY = (int) currentY;
        mDx = (int) startX;
        mDy = (int) startY;
        mXLimit = (int) xLimit;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        moveAndRotate((int) (mX * (1 - interpolatedTime)) + mDx, (int) (mY * (1 - interpolatedTime)) + mDy);
    }

    private void moveAndRotate(int x, int y) {
        Log.d("moveAndRotate", String.format(Locale.getDefault(), "x: %d, y: %d", x, y));
        mView.setRotation(((DEFAULT_DEGREES_VALUE * (x - mDx)) / mXLimit));
        mView.setX(x);
        mView.setY(y);
    }


}
