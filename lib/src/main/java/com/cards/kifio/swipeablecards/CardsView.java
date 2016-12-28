package com.cards.kifio.swipeablecards;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.IntDef;
import android.support.annotation.MainThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

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
     * Diff between top margin of 2 neighboring cards.
     */
    private int mMarginVerticalStep;

    /**
     * Mutable count of visible cards. If this value will be more then count of cards in adapter, it's will be set to count of cards in adapter.
     */
    private int mCurrentVisibleCardsCount;

    /**
     * Immutable count of visible cards. It's used for reloading cards, when mCurrentVisibleCardsCount is 0.
     */
    private int mVisibleCardsCount;

    /**
     * Order of items in CardsView. Direct order is from first to last, Reverse is from last item to first.
     */
    public static final int DIRECT_ORDER = 0;
    public static final int REVERSE_ORDER = 1;

    private int mOrder;

    /**
     * Default margins of CardView. If you will use default android:margins, animations will be look cropped.
     */
    private int mMarginLeft;
    private int mMarginRight;
    private int mMarginTop;
    private int mMarginBottom;

    /**
     * Count of swiped cards. If infinite mode disabled, after last card swipes, this fiels reinitialized with 0 value.
     */
    private int mCurrentSwipedCardsCount;

    /**
     * Flag for enabling infinite cards mode. In this mode, after initialization last item from adapter, first item from adapter will be initialized.
     */
    private boolean mInfinite;

    /**
     * Flag for enabling movable mode. In this mode, OnTouchCardListener will handle action ACTION_MOVE and change position of top card.
     */
    boolean mMovable;

    /**
     * Increasing this value can lead to errors in determining the x position of view.
     */
    static final int DEFAULT_DEGREES_VALUE = 15;

    static final int LEFT_SWIPE = 0;
    static final int RIGHT_SWIPE = 1;
    static final int MOVE_TO_INITIAL = 2;
    static final int NO_ANIMATION = 3;

    /**
     * Types of animations for moving top card.
     */
    @IntDef({LEFT_SWIPE, RIGHT_SWIPE, MOVE_TO_INITIAL, NO_ANIMATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType {}

    @AnimationType int mAnimation = NO_ANIMATION;

    private int mSwipeEdge;

    private ContentAdapter mAdapter;

    private OnTouchCardListener mDefaultOnTouchListener = new OnTouchCardListener(this);

    public boolean mAnimLock = false;

    public CardsView(Context context) {
        super(context);
        mVisibleCardsCount = mCurrentVisibleCardsCount = getResources().getInteger(R.integer.default_visible_views_count);
    }

    public CardsView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        TypedArray attrs = context.getTheme().obtainStyledAttributes(attrSet, R.styleable.CardsView, 0, 0);
        Resources res = getResources();

        try {

            mMarginHorizontalStep = (int) attrs.getDimension(R.styleable.CardsView_marginHorizontalStep, res.getDimension(R.dimen.default_margin));

            mMarginVerticalStep = (int) attrs.getDimension(R.styleable.CardsView_marginVerticalStep, res.getDimension(R.dimen.default_margin));

            mVisibleCardsCount = mCurrentVisibleCardsCount = attrs.getInt(R.styleable.CardsView_visibleViewsCount, res.getInteger(R.integer.default_visible_views_count));

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

            mMovable = attrs.getBoolean(R.styleable.CardsView_movable, false);

            mOrder = attrs.getInt(R.styleable.CardsView_order, DIRECT_ORDER);

        } finally {
            attrs.recycle();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mSwipeEdge = 2 * (getWidth() / 5);
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

    public void reload() {
        mCurrentSwipedCardsCount = 0;
        if (mAdapter != null) {

            if (mCurrentVisibleCardsCount > mAdapter.getCount()) {
                mVisibleCardsCount = mCurrentVisibleCardsCount = mAdapter.getCount();
            } else if (mCurrentVisibleCardsCount == 0) {
                mCurrentVisibleCardsCount = mVisibleCardsCount;
            }

        } else {

            throw new RuntimeException("Adapter must be initialized!");

        }

        for (int i = 0; i < mCurrentVisibleCardsCount; i++) addView(i);
    }

    private void addView(int index) {

        SwipeableCard child = (SwipeableCard) mAdapter.getView(this);

        addView(child, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (int i = getChildCount() - 1; i > 0; i--) {
                getChildAt(i).setTranslationZ(i);
            }
        }

        initView(child, index);

        if (index == 0 || index == 1) {

            initCard(child, index);

            if (child.getOnTouchCardListener() == null) {
                child.setOnTouchCardListener(mDefaultOnTouchListener);
            }
        }
    }

    private void initView(SwipeableCard view, int position) {

        int nextPos = (position + 1);
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.setMargins(mMarginLeft + (mMarginHorizontalStep * nextPos), mMarginTop + (mMarginVerticalStep * nextPos),
                mMarginRight + (mMarginHorizontalStep * nextPos), mMarginBottom + (mMarginVerticalStep * nextPos));
        view.setLayoutParams(lp);
        view.setClipRect(position > 1 ? mMarginHorizontalStep : 0);

        view.setTag(position);
        view.setVisibility(VISIBLE);
    }

    private void initCard(SwipeableCard card, int position) {
        mAdapter.initCard(card, isReverseOrder() ? mAdapter.getCount() - position : position);
    }

    private boolean isReverseOrder() {
        return mOrder == REVERSE_ORDER;
    }

    void onSwipe(int animId) {

        mAnimLock = true;

        mAnimation = NO_ANIMATION;

        int count = mAdapter.getCount();
        boolean lastCardNeeded = mInfinite || mCurrentVisibleCardsCount + mCurrentSwipedCardsCount < count;

        if (lastCardNeeded) {
            addView(mCurrentVisibleCardsCount - 1);
        } else {
            mCurrentVisibleCardsCount--;
        }

        SwipeableCard topView = (SwipeableCard) getChildAt(mCurrentVisibleCardsCount);

        // Start animation for top card.
        Animation swipeAnimation = AnimationUtils.loadAnimation(getContext(), animId);
        swipeAnimation.setAnimationListener(this);
        topView.startAnimation(swipeAnimation);

        mCurrentSwipedCardsCount = (mInfinite && mCurrentSwipedCardsCount + 1 == count) ? 0 : mCurrentSwipedCardsCount + 1;

        resizeAndReplaceCards(lastCardNeeded);
    }

    private void resizeAndReplaceCards(boolean lastCardNeeded) {

        int firstPosition = mCurrentVisibleCardsCount - 1;
        int secondPostion = firstPosition - 1;
        int lastPosition = lastCardNeeded ? 1 : 0;

        Resources res = getResources();
        for (int i = firstPosition; i >= lastPosition; i--) {

            SwipeableCard view = (SwipeableCard) getChildAt(i);

            if (i == firstPosition) {
                view.setClipRect(0);    // draw all nested views of card.
                initCard(view, mCurrentSwipedCardsCount);    // set values for nested views.
                view.setOnTouchCardListener(mDefaultOnTouchListener);
            } else if (i == secondPostion) {
                view.setClipRect(0);    // draw all nested views of card.
                initCard(view, mCurrentSwipedCardsCount + 1);    // set values for nested views.
            }

            Animation anim = new ResizeAnimation(view, mMarginHorizontalStep, mMarginVerticalStep);
            anim.setDuration(res.getInteger(android.R.integer.config_mediumAnimTime));
            view.startAnimation(anim);
        }
    }

    void onMove(int dx, int dy) {

        mAnimation = NO_ANIMATION;

        SwipeableCard topView = (SwipeableCard) getChildAt(mCurrentVisibleCardsCount - 1);

        float newX = topView.getX() + dx;
        float newY = topView.getY() + dy;

        topView.setRotation((int) ((DEFAULT_DEGREES_VALUE * (newX - dx)) / mSwipeEdge));
        topView.setX(newX);
        topView.setY(newY);

        if (Math.abs(newX) > mSwipeEdge) {

            if (newX > 0) {
                mAnimation = RIGHT_SWIPE;
            } else {
                mAnimation = LEFT_SWIPE;
            }

        } else {
            mAnimation = MOVE_TO_INITIAL;
        }

    }

    public void moveTopCardToStartPos() {

        mAnimation = NO_ANIMATION;

        final SwipeableCard view = (SwipeableCard) getChildAt(mCurrentVisibleCardsCount - 1);

        final int startXPos = (int) view.getX();
        final int startYPos = (int) view.getY();

        Animation animation = new MoveToStartPosAnimation(view, startXPos, startYPos,
                mMarginHorizontalStep, mMarginVerticalStep, mSwipeEdge);

        animation.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        view.startAnimation(animation);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mAnimLock = false;
        int pos = getChildCount() - 1;
        View view = getChildAt(pos);
        view.setOnTouchListener(null);
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