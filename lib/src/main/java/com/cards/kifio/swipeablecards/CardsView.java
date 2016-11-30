package com.cards.kifio.swipeablecards;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

/**
 * Created by kifio on 7/6/16.
 */
public class CardsView extends RelativeLayout implements Animation.AnimationListener {

    private static final String TAG = "kifio-CardsView";

    private int mMarginTop;
    private int mMarginLeft;
    private int mMarginRight;
    private int mMarginBottom;

    private int mMarginStep;
    private int mVisibleViewsCount;

    private Activity mContext;
    private ContentAdapter mAdapter;
    private OnTouchCardListener mDefaultOnTouchListener = new OnTouchCardListener(this);

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
        TypedArray attrs = context.getTheme().obtainStyledAttributes(attrSet, R.styleable.CardsView, 0, 0);
        Resources res = getResources();
        mContext = (Activity) context;

        try {

            int margin = (int) attrs.getDimension(R.styleable.CardsView_margin, 0);

            if (margin == 0) {
                mMarginTop = (int) attrs.getDimension(R.styleable.CardsView_marginTop, res.getDimension(R.dimen.default_margin));
                mMarginLeft = (int) attrs.getDimension(R.styleable.CardsView_marginLeft, res.getDimension(R.dimen.default_margin));
                mMarginRight = (int) attrs.getDimension(R.styleable.CardsView_marginRight, res.getDimension(R.dimen.default_margin));
                mMarginBottom = (int) attrs.getDimension(R.styleable.CardsView_marginBottom, res.getDimension(R.dimen.default_margin));
            } else {
                mMarginTop = mMarginLeft = mMarginRight = mMarginBottom = margin;
            }

            mMarginStep = (int) attrs.getDimension(R.styleable.CardsView_marginStep, res.getDimension(R.dimen.default_margin));

            mVisibleViewsCount = attrs.getInt(R.styleable.CardsView_visibleViewsCount, res.getInteger(R.integer.default_visible_views_count));
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


    public void setScrollableParent(ViewParent scrollableParent) {
        mDefaultOnTouchListener.setScrollableParent(scrollableParent);
    }

    public ContentAdapter getAdapter() {
        return mAdapter;
    }

    public void reload() {

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

        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.setMargins(mMarginLeft * (position + 1), mMarginTop * (position + 1), mMarginRight * (position + 1), mMarginBottom * (position + 1));

        view.setLayoutParams(lp);
        view.setClipRect(position > 0 ? mMarginTop : 0);

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

        Resources res = getResources();
        for (int i = starPosition; i > count - (mVisibleViewsCount + 1); i--) {
            if (i >= 0) {
                view = (SwipeableCard) getChildAt(i);

                if (i == starPosition) {
                    view.setClipRect(0);    // draw all nested views of card.
                    mAdapter.initCard(view, ((mAdapter.getCount() - count) + 1));    // set values for nested views.
                    view.setOnTouchCardListener(mDefaultOnTouchListener);
                }

                Animation anim = new ResizeAnimation(view, mMarginStep, mMarginTop);
                anim.setDuration(res.getInteger(android.R.integer.config_mediumAnimTime));
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