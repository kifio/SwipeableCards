package com.cards.kifio.swipeablecards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by kifio on 10/6/16.
 */

public class SwipeableCard extends CardView {

    private static final String TAG = "SwipeableCard";
    private Rect mRect;
    private OnTouchCardListener mListener;

    public SwipeableCard(Context context) {
        super(context);
    }

    public SwipeableCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRect != null) {
            canvas.clipRect(mRect);
        }
        super.onDraw(canvas);
    }

    /**
     * Method for avoiding overdraws.
     * @param height - height of clip rect.
     */
    public void setClipRect(int height) {
        if (height == 0) {
            mRect = null;
        } else {
            mRect = new Rect(getLeft(), getBottom() + height, getRight(), getBottom());
        }
    }

    /**
     * Method for setting on touch listeners for child view.
     */
    public void setOnTouchCardListener(OnTouchCardListener listener) {
        mListener = listener;
        setOnTouchListener(mListener);
    }

    public OnTouchCardListener getOnTouchCardListener() {
        return mListener;
    }
}
