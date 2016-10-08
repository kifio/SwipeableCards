package com.cards.kifio.swipeablecards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kifio on 10/6/16.
 */

public class SwipeableCard extends CardView {

    private Rect mClippingRect;

    public SwipeableCard(Context context) {
        super(context);
    }

    public SwipeableCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipRect(mClippingRect);
        super.onDraw(canvas);
    }

    public void setClipRect(int height) {
        mClippingRect = new Rect(getLeft(), getBottom() + height, getRight(), getBottom());
    }
}
