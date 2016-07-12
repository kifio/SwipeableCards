package com.cards.kifio.swipeablecards;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
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

    private static final int DEFAULT_VISIBLE_VIEWS_COUNT = 3;

    private float mInitialTouchX, mInitialTouchY;
    private boolean mClick = true;

    private Adapter mAdaper;
    boolean mAnimLock;

    private final int mBaseMargin;
    private final int mMarginStep;
    private final int mVisibleViewsCount;

//    public CardsView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        this.mBaseMargin = mBaseMargin;
//        this.mMarginStep = mMarginStep;
//        this.mVisibleViewsCount = mVisibleViewsCount;
//    }
//
//    public CardsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        this.mBaseMargin = mBaseMargin;
//        this.mMarginStep = mMarginStep;
//        this.mVisibleViewsCount = mVisibleViewsCount;
//    }

    public CardsView(Context context) {
        super(context);
        this.mBaseMargin = R.dimen.default_base_margin;
        this.mMarginStep = R.dimen.default_step;
        this.mVisibleViewsCount = DEFAULT_VISIBLE_VIEWS_COUNT;
    }

    public CardsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributesValues = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardsView, 0, 0);
        try {
            mBaseMargin = (int) attributesValues.getDimension(R.styleable.CardsView_topCardMargin, R.dimen.default_base_margin);
            mMarginStep = (int) attributesValues.getDimension(R.styleable.CardsView_marginStep, R.dimen.default_step);
            mVisibleViewsCount = attributesValues.getInt(R.styleable.CardsView_count_visible, DEFAULT_VISIBLE_VIEWS_COUNT);
        } finally {
            attributesValues.recycle();
        }
    }

    public void setAdapter(Adapter adapter) throws Throwable{
        if (mVisibleViewsCount > adapter.getCount()) {
            throw new Throwable("Not enough elements in adapter. Must be at least: " + mVisibleViewsCount);
        }
        mAdaper = adapter;
    }

    public void reload() {
        mAnimLock = false;
        RelativeLayout.LayoutParams lp; View child; int topMargin, sideMargin;
        removeAllViews();
        for (int i = 0; i < mAdaper.getCount(); i++) {
            topMargin = mMarginStep * (mAdaper.getCount() - (i + 1));
            sideMargin = mBaseMargin + topMargin;
            addView(mAdaper.getView(i, null, this), i);
            child = getChildAt(i);
            child.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, child.getMeasuredHeight());
            lp.setMargins(sideMargin, topMargin, sideMargin, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            child.setOnTouchListener(this);
            child.setLayoutParams(lp);
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

//      Apply animation to srcView, for move it to destView position.
        View srcView, destView;
        int srcPos, destPos;

//      Start from first, because zero view swiped.
        for (int i = 1; i < mVisibleViewsCount; i++) {
            srcPos = (i + 1); destPos = i;

            srcView = getChildAt(childCount - srcPos);
            destView = getChildAt(childCount - destPos);

            if (srcView != null) {
                Animation anim = new ReviewAnimation(srcView, mBaseMargin + mMarginStep * (i -1),
                        (int) destView.getY());
                anim.setDuration(200);
                srcView.startAnimation(anim);
            }
        }

//        if (getChildAt(childCount - 2) != null) {
//            Animation secondReviewAnimation = new ReviewAnimation(getChildAt(childCount - 2), mBaseMargin, (int) firstReview.getY());
//            secondReviewAnimation.setDuration(200);
//            getChildAt(childCount - 2).startAnimation(secondReviewAnimation);
//        }
//
//        if (getChildAt(childCount - 3) != null && getChildAt(childCount - 2) != null) {
//            Animation thirdReviewAnimation = new ReviewAnimation(getChildAt(childCount - 3), mBaseMargin + mMarginStep, (int) getChildAt(childCount - 2).getY());
//            thirdReviewAnimation.setDuration(200);
//            getChildAt(childCount - 3).startAnimation(thirdReviewAnimation);
//        }
//
//        if (getChildAt(childCount - 4) != null && getChildAt(childCount - 3) != null) {
//            Animation thirdReviewAnimation = new ReviewAnimation(getChildAt(childCount - 4), mBaseMargin + mMarginStep + mMarginStep, (int) getChildAt(childCount - 3).getY());
//            thirdReviewAnimation.setDuration(200);
//            getChildAt(childCount - 4).startAnimation(thirdReviewAnimation);
//            getChildAt(childCount - 4).setVisibility(VISIBLE);
//        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mAnimLock = false;
        CardsView.this.removeView(getChildAt(getChildCount() - 1));
        if (getChildCount() == 0) reload();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}