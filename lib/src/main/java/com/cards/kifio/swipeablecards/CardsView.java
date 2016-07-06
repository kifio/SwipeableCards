package com.cards.kifio.swipeablecards;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.RelativeLayout;

/**
 * Created by kifio on 7/6/16.
 */
public class CardsView extends RelativeLayout implements View.OnTouchListener, Animation.AnimationListener {

    private static final String TAG = "kifio-CardsView";

    private float mInitialTouchX, mInitialTouchY;
    private boolean mClick = true;

    private Adapter mAdaper;
    boolean mAnimLock;

    private int mBaseMargin;
    private int mMarginStep;

    public CardsView(Context context) {
        super(context);
    }

    public CardsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributesValues = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardsView, 0, 0);

        try {
            mBaseMargin = (int) attributesValues.getDimension(R.styleable.CardsView_topCardMargin, R.dimen.default_base_margin);
            mMarginStep = (int) attributesValues.getDimension(R.styleable.CardsView_marginStep, R.dimen.default_step);
        } finally {
            attributesValues.recycle();
        }
    }

    public void setAdapter(Adapter adapter) {
        mAdaper = adapter;
    }

    public void init() {

        mAnimLock = false;
        
        RelativeLayout.LayoutParams lp;
        View child;

        Resources res = getResources();
        removeAllViews();

        for (int i = 0; i < mAdaper.getCount(); i++) {

            addView(mAdaper.getView(i, null, this), i);

            child = getChildAt(i);

            child.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int height = child.getMeasuredHeight();

            lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);

            if (i == mAdaper.getCount() - 1) {
                lp.setMargins(mBaseMargin, mMarginStep, mBaseMargin, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) child.setTranslationZ((int) res.getDimension(R.dimen.value_3_dp));
            } else if (i == mAdaper.getCount() - 2) {
                lp.setMargins(mBaseMargin + mMarginStep, mMarginStep + mMarginStep, mBaseMargin + mMarginStep, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) child.setTranslationZ((int) res.getDimension(R.dimen.value_2_dp));
            } else  {
                lp.setMargins(mBaseMargin + (2 * mMarginStep), mMarginStep * 3, mBaseMargin + (2 * mMarginStep), 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) child.setTranslationZ((int) res.getDimension(R.dimen.value_1_dp));
                if (i < mAdaper.getCount() - 3) child.setVisibility(INVISIBLE);
            }

            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);

            child.setOnTouchListener(this);
            child.setLayoutParams(lp);
            child = null;
        }

    }

    // On CardTouChListener
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                v.clearAnimation();
                mInitialTouchX = event.getX();
                mInitialTouchY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                final float xMove = event.getX();
                final float yMove = event.getY();

                //calculate distance moved
                final float dx = xMove - mInitialTouchX;
                final float dy = yMove - mInitialTouchY;

                if (Math.abs(dx + dy) > 5) mClick = false;

                if (Math.abs(dx) > 5) {
                    v.getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);

                    if (dx < -10 && !mAnimLock) {
                        startAnimation(v, R.anim.slide_out_left);
                    } else if (dx > 10 && !mAnimLock) {
                        startAnimation(v, R.anim.slide_out_right);
                    }

                }
                break;

            case MotionEvent.ACTION_UP:

                if (mClick) {
                    v.performClick();
                    return false;
                }
                break;

            default:
                return false;
        }
        return true;
    }

    private void startAnimation(View firstReview, int animId) {
        mAnimLock = true;
        int childCount = getChildCount();

        Animation swipeAnimation = AnimationUtils.loadAnimation(getContext(), animId);
        swipeAnimation.setAnimationListener(this);
        firstReview.startAnimation(swipeAnimation);

        if (getChildAt(childCount - 2) != null) {
            Animation secondReviewAnimation = new ReviewAnimation(getChildAt(childCount - 2), mBaseMargin, (int) firstReview.getY());
            secondReviewAnimation.setDuration(200);
            getChildAt(childCount - 2).startAnimation(secondReviewAnimation);
        }

        if (getChildAt(childCount - 3) != null && getChildAt(childCount - 2) != null) {
            Animation thirdReviewAnimation = new ReviewAnimation(getChildAt(childCount - 3), mBaseMargin + mMarginStep, (int) getChildAt(childCount - 2).getY());
            thirdReviewAnimation.setDuration(200);
            getChildAt(childCount - 3).startAnimation(thirdReviewAnimation);
        }

        if (getChildAt(childCount - 4) != null && getChildAt(childCount - 3) != null) {
            Animation thirdReviewAnimation = new ReviewAnimation(getChildAt(childCount - 4), mBaseMargin + mMarginStep, (int) getChildAt(childCount - 3).getY());
            thirdReviewAnimation.setDuration(200);
            getChildAt(childCount - 4).startAnimation(thirdReviewAnimation);
            getChildAt(childCount - 4).setVisibility(VISIBLE);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mAnimLock = false;
        CardsView.this.removeView(getChildAt(getChildCount() - 1));
        if (getChildCount() == 0) {
            init();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}