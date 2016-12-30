package com.cards.kifio.swipeablecards.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by kifio on 28.12.16.
 */

public class MoveToStartPosAnimation extends Animation {

    private View mView;
    private int mX, mY;
    private int mDx, mDy, mXLimit;

    public MoveToStartPosAnimation(View view, float currentX, float currentY,
                                   float startX, float startY, float xLimit) {
        mView = view;
        mX = (int) currentX;
        mY = (int) currentY;
        mDx = (int) startX;
        mDy = (int) startY;
        mXLimit = (int) xLimit;
    }

    @Override
    protected void applyTransformation(float time, Transformation t) {
        moveAndRotate((int) (mX * (1 - time)) + mDx, (int) (mY * (1 - time)) + mDy);
    }

    private void moveAndRotate(int x, int y) {
        float defaultDeegrees = 15; //Increasing this value can lead to errors in determining the x position of view.
        mView.setRotation(((defaultDeegrees * (x - mDx)) / mXLimit));
        mView.setX(x);
        mView.setY(y);
    }


}
