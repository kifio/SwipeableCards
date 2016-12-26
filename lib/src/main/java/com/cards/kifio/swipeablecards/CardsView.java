package com.cards.kifio.swipeablecards;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
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

    /**
     * Horizontal space between edges of 2 neighboring cards.
     */
    private int mMarginHorizontalStep;

    /**
     * Diff between y position of 2 neighboring cards.
     */
    private int mMarginVerticalStep;

    /**
     * Immutable count of visible cards. If this value will be more then count of cards in adapter, it's will be set to count of cards in adapter.
     */
    private int mVisibleCardsCount;


    /**
     * Default margins of CardView. If you will use default android:margins, animations will be look cropped.
     */
    private int mMarginLeft;
    private int mMarginRight;
    private int mMarginTop;
    private int mMarginBottom;

    /**
     * Count of swiped cards.
     */
    private int mSwipedCardsCount;

    /**
     * Flag for enabling infinite cards mode. In this mode, after initialization last item from adapter, first item from adapter will be initialized.
     */
    private boolean mInfinite;

    private ContentAdapter mAdapter;
    private OnTouchCardListener mDefaultOnTouchListener = new OnTouchCardListener(this);

    public boolean mAnimLock = false;

    public CardsView(Context context) {
        super(context);
        mStackCardsCount = mVisibleCardsCount = getResources().getInteger(R.integer.default_visible_views_count);
    }

    public CardsView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        TypedArray attrs = context.getTheme().obtainStyledAttributes(attrSet, R.styleable.CardsView, 0, 0);
        Resources res = getResources();

        try {

            mMarginHorizontalStep = (int) attrs.getDimension(R.styleable.CardsView_marginHorizontalStep, res.getDimension(R.dimen.default_margin));

            mMarginVerticalStep = (int) attrs.getDimension(R.styleable.CardsView_marginVerticalStep, res.getDimension(R.dimen.default_margin));

            mStackCardsCount = mVisibleCardsCount = attrs.getInt(R.styleable.CardsView_visibleViewsCount, res.getInteger(R.integer.default_visible_views_count));

            int margin = (int) attrs.getDimension(R.styleable.CardsView_margin, 0);

            if (margin == 0) {

                mMarginLeft = (int) attrs.getDimension(R.styleable.CardsView_marginVerticalStep, 0);

                mMarginRight = (int) attrs.getDimension(R.styleable.CardsView_marginVerticalStep, 0);

                mMarginTop = (int) attrs.getDimension(R.styleable.CardsView_marginVerticalStep, 0);

                mMarginBottom = (int) attrs.getDimension(R.styleable.CardsView_marginVerticalStep, 0);

            } else {
                mMarginLeft = mMarginTop = mMarginRight = mMarginBottom = margin;
            }

            mInfinite = attrs.getBoolean(R.styleable.CardsView_infinite, false);

        } finally {
            attrs.recycle();
        }
    }

    public void setAdapter(ContentAdapter adapter) {
        mAdapter = adapter;
    }

    public ContentAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * If CardsView is child of ScrollView or NestedScrollView, to avoid scroll events when user swipe cards, set reference on ScrollView.
     *
     * @param scrollableParent ScrollView or NestedScrollView instance in view hierarchy.
     */
    public void setScrollableParent(ViewParent scrollableParent) {
        mDefaultOnTouchListener.setScrollableParent(scrollableParent);
    }

    private int mStackCardsCount;

    public void reload() {
        mSwipedCardsCount = 0;
        if (mAdapter != null) {

            if (mVisibleCardsCount > mAdapter.getCount()) {
                mStackCardsCount = mVisibleCardsCount = mAdapter.getCount();
            } else if (mVisibleCardsCount == 0) {
                mVisibleCardsCount = mStackCardsCount;
            }

        } else {

            throw new RuntimeException("Adapter must be initialized!");

        }

        int count = mAdapter.getCount();
        for (int i = 0; i < mVisibleCardsCount; i++) {
            addView(i, count);
        }
    }

    private void addView(int index, int count) {
        SwipeableCard child = (SwipeableCard) mAdapter.getView(this);
        addView(child, 0);

        int translationZ = count - index - 1;
        initView(child, index, translationZ);
        if (index == 0) {
            mAdapter.initCard(child, index);
            if (child.getOnTouchCardListener() == null) {
                child.setOnTouchCardListener(mDefaultOnTouchListener);
            }
        }

    }

    private void initView(SwipeableCard view, int position, int translationZ) {

        int nextPos = (position + 1);

        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.setMargins(mMarginLeft + (mMarginHorizontalStep * nextPos), mMarginTop + (mMarginVerticalStep * nextPos),
                mMarginRight + (mMarginHorizontalStep * nextPos), mMarginBottom + (mMarginVerticalStep * nextPos));
        view.setLayoutParams(lp);


        view.setClipRect(position > 0 ? mMarginHorizontalStep : 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setTranslationZ(translationZ);
        }

        view.setTag(position);
        view.setVisibility(VISIBLE);
    }

    /**
     * Logic of animations. Swipe top card, resize rest cards, set visibilities of content and invisible cards.
     */
    void onSwipe(int animId) {
        mAnimLock = true;

        int count = mAdapter.getCount();

        if (mInfinite || mVisibleCardsCount + mSwipedCardsCount < count) {
            addView(mVisibleCardsCount, count - mSwipedCardsCount);
        } else {
            mVisibleCardsCount--;
        }

        SwipeableCard topView = (SwipeableCard) getChildAt(mVisibleCardsCount);

        // Start animation for top card.
        Animation swipeAnimation = AnimationUtils.loadAnimation(getContext(), animId);
        swipeAnimation.setAnimationListener(this);
        topView.startAnimation(swipeAnimation);

        mSwipedCardsCount = (mInfinite && mSwipedCardsCount + 1 == count) ? 0 : mSwipedCardsCount + 1;

        // Third, we move and resize others cards.
        int starPosition = mVisibleCardsCount - 1;   // because (count - 1) is top card.

        Resources res = getResources();
        for (int i = starPosition; i >= 0; i--) {
            SwipeableCard view = (SwipeableCard) getChildAt(i);

            if (i == starPosition) {
                view.setClipRect(0);    // draw all nested views of card.
                mAdapter.initCard(view, mSwipedCardsCount);    // set values for nested views.
                view.setOnTouchCardListener(mDefaultOnTouchListener);
            }

            Animation anim = new ResizeAnimation(view, mMarginHorizontalStep, mMarginVerticalStep);
            anim.setDuration(res.getInteger(android.R.integer.config_mediumAnimTime));
            view.startAnimation(anim);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mAnimLock = false;
        int pos = getChildCount() - 1;
        View view = getChildAt(pos);
        view.setOnTouchListener(null);
        removeView(view);
        Log.d(TAG, "onAnimationEnd: " + pos);
        if (pos == 0) reload();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}