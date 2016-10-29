package com.cards.kifio.swipeablecards;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

/**
 * Created by kifio on 7/6/16.
 */
public class CardsView extends RelativeLayout implements Animation.AnimationListener {

    private static final String TAG = "kifio-CardsView";

    private float mBaseMargin;
    private float mMarginStep;
    private int mVisibleViewsCount = 3;
    private int mNext = 0;
    private Activity mContext;
    private ContentAdapter mAdapter;
    private OnTouchCardListener mListener;

    @Override
    protected int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
    }

    public int mSwipeWidth;
    public boolean mAnimLock = false;

    public CardsView(Context context) {
        super(context);
        Resources res = getResources();
        mContext = (Activity) context;
        mBaseMargin = (int) res.getDimension(R.dimen.default_base_margin);
        mMarginStep = (int) res.getDimension(R.dimen.default_step);
        setDefaultSwipeWidth();
    }

    public CardsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributesValues = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardsView, 0, 0);
        Resources res = getResources();
        mContext = (Activity) context;
        try {
            mBaseMargin = attributesValues.getDimension(R.styleable.CardsView_topCardMargin, res.getDimension(R.dimen.default_base_margin));
            mMarginStep = attributesValues.getDimension(R.styleable.CardsView_marginStep, res.getDimension(R.dimen.default_step));
            mVisibleViewsCount = attributesValues.getInt(R.styleable.CardsView_count_visible, mVisibleViewsCount);
        } finally {
            attributesValues.recycle();
        }
    }

    private void setDefaultSwipeWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mSwipeWidth = (metrics.widthPixels / 100) * 5;
    }

    private void setSwipeWidth(int swipeWidth) {
        mSwipeWidth = swipeWidth;
    }

    public void setDataSet(ContentAdapter adapter) {
        mAdapter = adapter;
        if (mVisibleViewsCount > adapter.getCount()) {
            mVisibleViewsCount = adapter.getCount();
        }
    }

    public void reload() {
        mListener = new OnTouchCardListener(this);
        int position, count = mAdapter.getCount();
        SwipeableCard child = null;
        for (int i = 0; i < count; i++) {
            child = (SwipeableCard) mAdapter.getView(i, null, this);
            addView(child, i);
            position = count - (i + 1);
            if (position < mVisibleViewsCount) {
                initView(child, position, i);
                if (position == 0) {
                    mAdapter.initCard(child);
                    child.setOnTouchListener(mListener);
                }
            } else {
                child.setVisibility(INVISIBLE);
            }
        }
        mNext++;
    }

    // On CardTouChListener
    private void initView(View view, int relativePosition, int translation) {
        float topMargin, sideMargin;
        topMargin = mMarginStep * (relativePosition + 1);
        sideMargin = mBaseMargin + (mMarginStep * relativePosition);
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, view.getMeasuredHeight());
        lp.setMargins((int) sideMargin, (int) topMargin, (int) sideMargin, (int) mMarginStep);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        view.setLayoutParams(lp);
        setTranslationZ(view, translation);
        view.setVisibility(VISIBLE);
    }

    private void setTranslationZ(View view, int translation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            view.setTranslationZ(translation);
    }

    private void startAnimation(View firstReview, int animId) {
        mAnimLock = true;

        int count = getChildCount();
        int firstInvisiblePosition = count - (mVisibleViewsCount + 1);

        if (firstInvisiblePosition >= 0) {
            View invisibleView = getChildAt(firstInvisiblePosition);
            initView(invisibleView, mVisibleViewsCount - 1, firstInvisiblePosition);
        }

        Animation swipeAnimation = AnimationUtils.loadAnimation(getContext(), animId);
        swipeAnimation.setAnimationListener(this);
        firstReview.startAnimation(swipeAnimation);

        View view;
        int starPosition = count - 2;

        for (int i = starPosition; i >= 0; i--) {
            view = getChildAt(i);

            if (i == starPosition) {
                mAdapter.initCard((SwipeableCard) view);
                view.setOnTouchListener(mListener);
            }

            Animation anim = new ReviewAnimation(view, mMarginStep);
            anim.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
            view.startAnimation(anim);
        }
        mNext++;
    }

    public void handleSwipe(View v, int animationId) {
        startAnimation(v, animationId);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mAnimLock = false;
        int pos = getChildCount() - 1;
        View view = getChildAt(pos);
        view.setOnTouchListener(null);
        view.setVisibility(GONE);
        removeView(view);
        if (pos == 0) reload();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

}