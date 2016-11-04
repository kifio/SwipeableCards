package com.cards.kifio.swipeablecards;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
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

    private float mTopMargin;
    private float mMarginStep;
    private int mVisibleViewsCount = 3;
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
        mTopMargin = (int) res.getDimension(R.dimen.default_base_margin);
        mMarginStep = (int) res.getDimension(R.dimen.default_step);
        setDefaultSwipeWidth();
    }

    public CardsView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        TypedArray attrs =
                context.getTheme().obtainStyledAttributes(attrSet, R.styleable.CardsView, 0, 0);
        Resources res = getResources();
        mContext = (Activity) context;
        try {
            mTopMargin = attrs.getDimension(R.styleable.CardsView_topCardMargin, res.getDimension(R.dimen.default_base_margin));
            mMarginStep = attrs.getDimension(R.styleable.CardsView_marginStep, res.getDimension(R.dimen.default_step));
            mVisibleViewsCount =
                    attrs.getInt(R.styleable.CardsView_visibleViewsCount, mVisibleViewsCount);
        } finally {
            attrs.recycle();
        }
    }

    private void setDefaultSwipeWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mSwipeWidth = (metrics.widthPixels / 100) * 5;
    }

    public void setDataSet(ContentAdapter adapter) {
        mAdapter = adapter;
        if (mVisibleViewsCount > adapter.getCount()) {
            mVisibleViewsCount = adapter.getCount();
        }
    }

    public void reload() {
        mListener = new OnTouchCardListener(this);
        int count = mAdapter.getCount();
        SwipeableCard child = null;
        for (int i = 0; i < count; i++) {
            child = (SwipeableCard) mAdapter.getView(i, this);
            addView(child, 0);
            if (i < mVisibleViewsCount) {
                initView(child, i);
                if (i == 0) {
                    mAdapter.initCard(child);
                    child.setOnTouchListener(mListener);
                }
            } else {
                child.setVisibility(INVISIBLE);
            }
        }
    }

    // On CardTouChListener
    private void initView(SwipeableCard view, int position) {
        float topMargin, sideMargin;

        topMargin = mTopMargin * (position + 1);
        sideMargin = mMarginStep * (position + 1);

        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.setMargins((int) sideMargin, (int) topMargin, (int) sideMargin, 0);

        view.setLayoutParams(lp);
        view.setClipRect(position > 0 ? (int) mTopMargin : 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setTranslationZ(mAdapter.getCount() - position - 1);
        }

        view.setVisibility(VISIBLE);
    }

    private void startAnimation(SwipeableCard firstReview, int animId) {
        mAnimLock = true;

        int count = getChildCount();
        int firstInvisiblePosition = count - (mVisibleViewsCount + 1);

        if (firstInvisiblePosition >= 0) {
            SwipeableCard invisibleView = (SwipeableCard) getChildAt(firstInvisiblePosition);
            initView(invisibleView, 0);
        }

        Animation swipeAnimation = AnimationUtils.loadAnimation(getContext(), animId);
        swipeAnimation.setAnimationListener(this);
        firstReview.startAnimation(swipeAnimation);

        SwipeableCard view;
        int starPosition = count - 3;

        for (int i = starPosition; i >= 0; i--) {
            view = (SwipeableCard) getChildAt(i);

            if (i == starPosition) {
                view.setClipRect(0);
                mAdapter.initCard(view);
                view.setOnTouchCardListener(mListener);
            }

            Animation anim = new ReviewAnimation(view, mMarginStep, mTopMargin);
            anim.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            view.startAnimation(anim);
        }
    }

    public void handleSwipe(SwipeableCard v, int animationId) {
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