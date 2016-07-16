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

    private static final int DEFAULT_VISIBLE_VIEWS_COUNT = 3;

    private float mInitialTouchX, mInitialTouchY;
    private boolean mClick = true;

    private ContentAdapter mAdaper;
    boolean mAnimLock;

    private int mBaseMargin;
    private int mMarginStep;
    private int mVisibleViewsCount;

    public CardsView(Context context) {
        super(context);
        Resources res = getResources();
        init((int) res.getDimension(R.dimen.default_base_margin),
                (int) res.getDimension(R.dimen.default_step), DEFAULT_VISIBLE_VIEWS_COUNT);
    }

    public CardsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributesValues = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardsView, 0, 0);
        Resources res = getResources();
        try {
            init((int) attributesValues.getDimension(R.styleable.CardsView_topCardMargin, (int) res.getDimension(R.dimen.default_base_margin)),
                    (int) attributesValues.getDimension(R.styleable.CardsView_marginStep, (int) res.getDimension(R.dimen.default_step)),
                    attributesValues.getInt(R.styleable.CardsView_count_visible, DEFAULT_VISIBLE_VIEWS_COUNT));
        } finally {
            attributesValues.recycle();
        }
    }

    private void init(int baseMargin, int marginStep, int visibleViewsCount) {
        mBaseMargin = baseMargin;
        mMarginStep = marginStep;
        mVisibleViewsCount = visibleViewsCount;
    }

    public void setAdapter(ContentAdapter adapter) throws Throwable {
        if (mVisibleViewsCount > adapter.getCount()) {
            throw new Throwable("Not enough elements in adapter. Must be at least: " + mVisibleViewsCount);
        }
        mAdaper = adapter;
    }

    public void reload() {
        mAnimLock = false;
        RelativeLayout.LayoutParams lp;
        View child;
        int topMargin, sideMargin, position;
        removeAllViews();
        for (int i = 0; i < mAdaper.getCount(); i++) {
            position = mAdaper.getCount() - (i + 1);
            topMargin = mMarginStep * ((position < mVisibleViewsCount)
                    ? position : (mVisibleViewsCount - 1));
            sideMargin = mBaseMargin + topMargin;
            addView(mAdaper.getView(i, null, this), i);
            child = getChildAt(i);
            Log.d(TAG, position + " < " + mVisibleViewsCount);
//            TODO: init view on animation start.
            if (position < mVisibleViewsCount) mAdaper.initView(child, position);
            child.setVisibility((position < mVisibleViewsCount) ? VISIBLE : INVISIBLE);
            child.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, child.getMeasuredHeight());
            lp.setMargins(sideMargin, topMargin, sideMargin, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) child.setTranslationZ(i);
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

                float xMove = event.getX();
                float yMove = event.getY();

                float dx = xMove - mInitialTouchX;
                float dy = yMove - mInitialTouchY;

                if (Math.abs(dx + dy) > 5)
                    mClick = false;

                if (Math.abs(dx) > 5) {
                    v.getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
                    if (dx < -10 && !mAnimLock)
                        startAnimation(v, R.anim.slide_out_left);
                    else if (dx > 10 && !mAnimLock)
                        startAnimation(v, R.anim.slide_out_right);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mClick)
                    v.performClick();
                return false;

            default:
                return false;
        }
        return true;
    }

    private void startAnimation(View firstReview, int animId) {
        mAnimLock = true;
        int childCount = getChildCount();
//        TODO: move this check in anims loop.
        int firstInvisiblePosition = childCount - 1 - mVisibleViewsCount;
        if (getChildAt(firstInvisiblePosition) != null) {
            mAdaper.initView(getChildAt(firstInvisiblePosition), firstInvisiblePosition);
            getChildAt(firstInvisiblePosition).setVisibility(VISIBLE);
        }

        Animation swipeAnimation = AnimationUtils.loadAnimation(getContext(), animId);
        swipeAnimation.setAnimationListener(this);
        firstReview.startAnimation(swipeAnimation);

//      Apply animation to srcView, for move it to destView position.
        View srcView, destView;
        int srcPos, destPos;

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