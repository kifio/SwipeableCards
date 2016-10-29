package com.cards.kifio.swipeablecards;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by kifio on 10/29/16.
 */

public class OnTouchCardListener implements View.OnTouchListener {

    private static final float CLICK_OFFSET_LIMIT = 5.0f;
    private static final float SWIPE_OFFSET_LIMIT = 10;

    private float mInitialTouchX, mInitialTouchY;
    private boolean mClick = false;
    private CardsView mCardsView;

    OnTouchCardListener(CardsView view) {
        mCardsView = view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                mClick = true;
                v.clearAnimation();
                mInitialTouchX = event.getX();
                mInitialTouchY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:

                float xMove = event.getX();
                float yMove = event.getY();

                float dx = xMove - mInitialTouchX;
                float dy = yMove - mInitialTouchY;

                mClick = true;

                if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > mCardsView.mSwipeWidth) {
                    mClick = false;
                    v.getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
                    if (dx < -SWIPE_OFFSET_LIMIT && !mCardsView.mAnimLock) {
                        mCardsView.handleSwipe((View) v, R.anim.slide_out_left);
                    } else if (dx > SWIPE_OFFSET_LIMIT && !mCardsView.mAnimLock) {
                        mCardsView.handleSwipe((View) v, R.anim.slide_out_right);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mClick) {

                }
                return false;

            default:
                return false;
        }
        return true;
    }

}
