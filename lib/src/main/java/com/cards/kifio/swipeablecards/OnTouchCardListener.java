package com.cards.kifio.swipeablecards;

import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

/**
 * Created by kifio on 10/29/16.
 */

public class OnTouchCardListener implements View.OnTouchListener {

    private static final String TAG = "OnTouchCardListener";

    private CardsView mCardsView;
    private ViewParent mScrollableParent;
    private VelocityTracker mVelocityTracker = null;
    private int mMinimumFlingVelocity, mMaximumFlingVelocity;

    OnTouchCardListener(CardsView view) {
        mCardsView = view;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(view.getContext());
        mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);


        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:

                if(mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }

                break;

            case MotionEvent.ACTION_MOVE:

                // TODO: If moveable mode enabled, set X position in thsis case.

                mVelocityTracker.addMovement(event);

                break;

            case MotionEvent.ACTION_UP:

                mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);

                final float velocityX = mVelocityTracker.getXVelocity(pointerId);

                if ((Math.abs(velocityX) > mMinimumFlingVelocity)){

                    if (mScrollableParent != null) {
                        mScrollableParent.requestDisallowInterceptTouchEvent(true);
                    }

                    if (velocityX < -1 && !mCardsView.mAnimLock) {
                        mCardsView.onSwipe(R.anim.slide_out_left);
                    } else if (velocityX > 1 && !mCardsView.mAnimLock) {
                        mCardsView.onSwipe(R.anim.slide_out_right);
                    }

                } else {
                    v.performClick();
                }

                return false;

            default:
                return false;
        }
        return true;
    }

    void setScrollableParent(ViewParent scrollableParent) {
        mScrollableParent = scrollableParent;
    }
}
