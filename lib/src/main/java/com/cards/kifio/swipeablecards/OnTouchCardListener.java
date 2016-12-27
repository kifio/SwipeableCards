package com.cards.kifio.swipeablecards;

import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import java.util.Locale;

/**
 * Created by kifio on 10/29/16.
 */

public class OnTouchCardListener implements View.OnTouchListener {

    private static final String TAG = "OnTouchCardListener";

    private CardsView mCardsView;
    private ViewParent mScrollableParent;
    private VelocityTracker mVelocityTracker = null;
    private int mMinimumFlingVelocity, mMaximumFlingVelocity;
    private int mInitialTouchX, mInitialTouchY;

    OnTouchCardListener(CardsView view) {
        mCardsView = view;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(view.getContext());
        mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        // FIXME: Fix crash on multitouch and wrong x coord detecting.
        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:

                Log.d(TAG, "ACTION_DOWN " + mInitialTouchX);


                if (mCardsView.mMovable) {
                    mInitialTouchX = (int) event.getX();
                    mInitialTouchY = (int) event.getY();
                }

                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }

                break;

            case MotionEvent.ACTION_MOVE:


                    if (mScrollableParent != null) {
                        mScrollableParent.requestDisallowInterceptTouchEvent(true);
                    }

                    if (mCardsView.mMovable) {

                        float xMove = event.getX(pointerId);
                        float yMove = event.getY(pointerId);

                        Log.d(TAG, "ACTION_MOVE " + mInitialTouchX);

                        float dx = xMove - mInitialTouchX;
                        float dy = yMove - mInitialTouchY;

                        mCardsView.onMove(dx, dy);
                    } else {
                        mVelocityTracker.addMovement(event);
                    }

                break;

            case MotionEvent.ACTION_UP:

                Log.d(TAG, "ACTION_UP " + mInitialTouchX);

                if (!mCardsView.mMovable) {

                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);

                    final float velocityX = mVelocityTracker.getXVelocity(pointerId);

                    if ((Math.abs(velocityX) > mMinimumFlingVelocity)) {

                        if (velocityX < -1 && !mCardsView.mAnimLock) {
                            mCardsView.onSwipe(R.anim.slide_out_left);
                        } else if (velocityX > 1 && !mCardsView.mAnimLock) {
                            mCardsView.onSwipe(R.anim.slide_out_right);
                        }

                    } else {
                        v.performClick();
                    }
                } else {

                    if (mCardsView.mAnimation == CardsView.LEFT_SWIPE) {
                        mCardsView.onSwipe(R.anim.slide_out_left);
                    } else if (mCardsView.mAnimation == CardsView.RIGHT_SWIPE) {
                        mCardsView.onSwipe(R.anim.slide_out_right);
                    } else {
                        mCardsView.moveTopCardToStartPos();
                    }

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
