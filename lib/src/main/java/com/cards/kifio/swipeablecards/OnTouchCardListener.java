package com.cards.kifio.swipeablecards;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

/**
 * Created by kifio on 10/29/16.
 */

public class OnTouchCardListener implements View.OnTouchListener {

    private static final String TAG = "OnTouchCardListener";

    private float mInitialTouchX, mInitialTouchY;
    private boolean mClick = false;
    private CardsView mCardsView;
    private ViewParent mScrollableParent;

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

                if (Math.abs(dx) > Math.abs(dy)) {
                    mClick = false;

//                    SwipeableCard card = recursiveCardSearch(v);

                    if (mScrollableParent != null) {
                        mScrollableParent.requestDisallowInterceptTouchEvent(true);
                    }

                    if (dx < -1 && !mCardsView.mAnimLock) {
                        mCardsView.onSwipe(R.anim.slide_out_left);
                    } else if (dx > 1 && !mCardsView.mAnimLock) {
                        mCardsView.onSwipe(R.anim.slide_out_right);
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

    /**
     * Recursive search ViewParent which is instance of SwipeableCard.
     * @param view child of SwipeableCard.
     * @return SwipeableCard instance.
     */
    private SwipeableCard recursiveCardSearch(View view) {
        if (view instanceof SwipeableCard) {
            return (SwipeableCard) view;
        } else {
            return recursiveCardSearch((View) view.getParent());
        }
    }

    void setScrollableParent(ViewParent scrollableParent) {
        mScrollableParent = scrollableParent;
    }
}
