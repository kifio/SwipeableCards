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
                    if (dx < -SWIPE_OFFSET_LIMIT && !mCardsView.mAnimLock) {
                        if (v instanceof SwipeableCard) {
                            mCardsView.handleSwipe((SwipeableCard) v, R.anim.slide_out_left);
                        } else {
                            mCardsView.handleSwipe(recursiveCardSearch(v), R.anim.slide_out_left);
                        }
                    } else if (dx > SWIPE_OFFSET_LIMIT && !mCardsView.mAnimLock) {
                        if (v instanceof SwipeableCard) {
                            mCardsView.handleSwipe((SwipeableCard) v, R.anim.slide_out_right);
                        } else {
                            mCardsView.handleSwipe(recursiveCardSearch(v),  R.anim.slide_out_right);
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mClick) {
                    v.performClick();
                }
                return false;

            default:
                return false;
        }
        return true;
    }

    private SwipeableCard recursiveCardSearch(View view) {
        View parent = (View) view.getParent();
        if (parent instanceof SwipeableCard) {
            return (SwipeableCard) parent;
        } else {
            return recursiveCardSearch(parent);
        }
    }

}
