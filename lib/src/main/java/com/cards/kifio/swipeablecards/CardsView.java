package com.cards.kifio.swipeablecards;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

/**
 * Created by kifio on 7/6/16.
 */
public class CardsView extends RelativeLayout implements Animation.AnimationListener {

    private static final String TAG = "kifio-CardsView";

    private float mTopMargin = (int) getResources().getDimension(R.dimen.default_base_margin);
    private float mMarginStep = (int) getResources().getDimension(R.dimen.default_step);
    private int mVisibleViewsCount = getResources().getInteger(R.integer.default_visible_views_count);
    private int mAnimationDuraion = getResources().getInteger(android.R.integer.config_mediumAnimTime);
    private boolean mInfinite = false;
    private Activity mContext;
    private ContentAdapter mAdapter;
    private OnTouchCardListener mDefaultOnTouchListener;

    @Override
    protected int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
    }

    public int mSwipeWidth;
    public boolean mAnimLock = false;

    public CardsView(Context context) {
        super(context);
        mContext = (Activity) context;
        setDefaultSwipeWidth();
    }

    public CardsView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        TypedArray attrs =
                context.getTheme().obtainStyledAttributes(attrSet, R.styleable.CardsView, 0, 0);
        Resources res = getResources();
        mContext = (Activity) context;
        try {
            mInfinite = attrs.getBoolean(R.styleable.CardsView_infinite, false);
            mTopMargin = attrs.getDimension(R.styleable.CardsView_topCardMargin, res.getDimension(R.dimen.default_base_margin));
            mMarginStep = attrs.getDimension(R.styleable.CardsView_marginStep, res.getDimension(R.dimen.default_step));
            mVisibleViewsCount = attrs.getInt(R.styleable.CardsView_visibleViewsCount, mVisibleViewsCount);
        } finally {
            attrs.recycle();
        }
    }

    private void setDefaultSwipeWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mSwipeWidth = (metrics.widthPixels / 100) * 5;
    }

    public void setAdapter(ContentAdapter adapter) {
        mAdapter = adapter;
        if (mVisibleViewsCount > adapter.getCount()) {
            mVisibleViewsCount = adapter.getCount();
        }
    }

    public ContentAdapter getAdapter() {
        return mAdapter;
    }

    public void reload() {

        if (mDefaultOnTouchListener == null)
            mDefaultOnTouchListener = new OnTouchCardListener(this);

        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            addView(i, count);
        }
    }

    private void addView(int index, int count) {
        SwipeableCard child = (SwipeableCard) mAdapter.getView(this);
        addView(child, 0);
        if (index < mVisibleViewsCount) {
            int translationZ = count - index - 1;
            initView(child, index, translationZ);
            if (index == 0) {
                mAdapter.initCard(child, index);
                if (child.getOnTouchCardListener() == null) {
                    child.setOnTouchCardListener(mDefaultOnTouchListener);
                }
            }
        } else {
            child.setVisibility(INVISIBLE);
        }
    }

    private void initView(SwipeableCard view, int position, int translationZ) {
        float topMargin, sideMargin;

        topMargin = mTopMargin * (position + 1);
        sideMargin = mMarginStep * (position + 1);

        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.setMargins((int) sideMargin, (int) topMargin, (int) sideMargin, 0);

        view.setLayoutParams(lp);
        view.setClipRect(position > 0 ? (int) mTopMargin : 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setTranslationZ(translationZ);
        }
        view.setTag(position);
        view.setVisibility(VISIBLE);
    }

    private void startAnimation(SwipeableCard topView, int animId) {
        mAnimLock = true;

        int count = getChildCount();
        // Try to get invisible view, and set params and translation for it.
        int position = count - (mVisibleViewsCount + 1);
        if (position >= 0) {
            SwipeableCard invisibleView = (SwipeableCard) getChildAt(position);
            initView(invisibleView, mVisibleViewsCount - 1, position);
        }

        // Start animation for top card.
        Animation swipeAnimation = AnimationUtils.loadAnimation(getContext(), animId);
        swipeAnimation.setAnimationListener(this);
        topView.startAnimation(swipeAnimation);

        // Third, we move and resize others cards.
        SwipeableCard view;
        int starPosition = count - 2;   // because (count - 1) is top card.

        for (int i = starPosition; i > count - (mVisibleViewsCount + 1); i--) {
            if (i >= 0) {
                view = (SwipeableCard) getChildAt(i);

                if (i == starPosition) {
                    view.setClipRect(0);    // draw all nested views of card.
                    mAdapter.initCard(view, ((mAdapter.getCount() - count) + 1));    // set values for nested views.
                    view.setOnTouchCardListener(mDefaultOnTouchListener);
                }

                Animation anim = new ResizeAnimation(view, mMarginStep, mTopMargin);
                anim.setDuration(mAnimationDuraion);
                view.startAnimation(anim);
            }
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