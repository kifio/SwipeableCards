package com.cards.kifio.swipeablecards;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import static com.cards.kifio.swipeablecards.AnimationTypes.LEFT_SWIPE;
import static com.cards.kifio.swipeablecards.AnimationTypes.MOVE_TO_INITIAL;
import static com.cards.kifio.swipeablecards.AnimationTypes.RIGHT_SWIPE;

/**
 * Created by kifio on 10/29/16.
 */

public class OnTouchCardListener implements View.OnTouchListener {

    private static final String TAG = "OnTouchCardListener";

    private static final int INVALIDATE_POINTER_ID = -1;

    private CardsView mCardsView;
    private ViewParent mScrollableParent;
    private VelocityTracker mVelocityTracker = null;

    private int mActivePointerId = INVALIDATE_POINTER_ID;

    private int mMinimumFlingVelocity;
    private int mMaximumFlingVelocity;

    private float mInitialTouchX;
    private float mInitialTouchY;

    private boolean mClick = false;

    OnTouchCardListener(CardsView view) {
        mCardsView = view;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(view.getContext());
        mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int action = MotionEventCompat.getActionMasked(event);

        if (!mCardsView.mTouchLock) {
            switch (action) {

                case MotionEvent.ACTION_DOWN: {

                    mClick = true;

                    final int pointerIndex = MotionEventCompat.getActionIndex(event);
                    final float x = MotionEventCompat.getX(event, pointerIndex);
                    final float y = MotionEventCompat.getY(event, pointerIndex);

                    mInitialTouchX = x;
                    mInitialTouchY = y;

                    mActivePointerId = MotionEventCompat.getPointerId(event, 0);

                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        mVelocityTracker.clear();
                    }

                    break;

                }
                case MotionEvent.ACTION_MOVE: {

                    if (mScrollableParent != null) {
                        mScrollableParent.requestDisallowInterceptTouchEvent(true);
                    }

                    // Find the index of the active pointer and fetch its position
                    final int pointerIndex =
                            MotionEventCompat.findPointerIndex(event, mActivePointerId);

                    if (pointerIndex >= 0 && pointerIndex < event.getPointerCount()) {

                        float xMove = MotionEventCompat.getX(event, pointerIndex);
                        float yMove = MotionEventCompat.getY(event, pointerIndex);

                        int dx = (int) (xMove - mInitialTouchX);
                        int dy = (int) (yMove - mInitialTouchY);

                        if (Math.abs(dx) > 4 || Math.abs(dy) > 4) {
                            mClick = false;
                            if (mCardsView.mMovable) {
                                mCardsView.onMove(dx, dy);
                            }
                        }
                    }

                    mVelocityTracker.addMovement(event);

                    break;

                }
                case MotionEvent.ACTION_UP: {

                    mActivePointerId = INVALIDATE_POINTER_ID;

                    if (mClick) {
                        v.performClick();
                    } else if (!mCardsView.mMovable) {

                        mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);

                        final float velocityX = mVelocityTracker.getXVelocity(mActivePointerId);

                        if ((Math.abs(velocityX) > mMinimumFlingVelocity)) {

                            if (velocityX < -1 && !mCardsView.mAnimLock) {
                                mCardsView.onSwipe(R.anim.slide_out_left);
                            } else if (velocityX > 1 && !mCardsView.mAnimLock) {
                                mCardsView.onSwipe(R.anim.slide_out_right);
                            }
                        }

                    } else {

                        if (mCardsView.mAnimation == LEFT_SWIPE) {
                            mCardsView.onSwipe(R.anim.slide_out_left);
                        } else if (mCardsView.mAnimation == RIGHT_SWIPE) {
                            mCardsView.onSwipe(R.anim.slide_out_right);
                        } else if (mCardsView.mAnimation == MOVE_TO_INITIAL) {
                            mCardsView.onStopMoving();
                        }
                    }

                    return false;

                }
                default:
                    return false;
            }
        }
        return true;
    }

    void setScrollableParent(ViewParent scrollableParent) {
        mScrollableParent = scrollableParent;
    }

    public void dispose() {
        mScrollableParent = null;
        mVelocityTracker = null;
    }
}
