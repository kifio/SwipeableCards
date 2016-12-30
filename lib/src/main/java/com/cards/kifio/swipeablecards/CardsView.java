package com.cards.kifio.swipeablecards;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.cards.kifio.swipeablecards.anim.MoveToStartPosAnimation;
import com.cards.kifio.swipeablecards.anim.ResizeAnimation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * Created by kifio on 7/6/16.
 */
public class CardsView extends FrameLayout implements Animation.AnimationListener {

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
    private int mVisibleCardsCount;

    /**
     * If true, order of items in CardsView will be reverse.
     */
    private boolean mReverse;

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
    private int mSwipedCardsCount;

    /**
     * Flag for enabling infinite cards mode. In this mode, after initialization last item from adapter, first item from adapter will be initialized.
     */
    private boolean mInfinite;

    /**
     * Flag for enabling movable mode. In this mode, OnTouchCardListener will handle action ACTION_MOVE and change position of top card.
     */
    boolean mMovable;

    /**
     * Types of animations for moving top card.
     */
    static final int LEFT_SWIPE = 0;
    static final int RIGHT_SWIPE = 1;
    static final int MOVE_TO_INITIAL = 2;
    static final int NO_ANIMATION = 3;

    @IntDef({LEFT_SWIPE, RIGHT_SWIPE, MOVE_TO_INITIAL, NO_ANIMATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType {}

    @AnimationType int mAnimation = NO_ANIMATION;

    private int mSwipeEdge;

    private ContentAdapter mAdapter;

    private OnTouchCardListener mDefaultOnTouchListener = new OnTouchCardListener(this);

    private OnCardsCountChangeListener mOnCardsCountChangedListener;

    public boolean mAnimLock = false;

    public CardsView(Context context, int visibleCardsCount) {
        super(context);
        mVisibleCardsCount = visibleCardsCount;
    }

    public CardsView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        TypedArray attrs = context.getTheme().obtainStyledAttributes(attrSet, R.styleable.CardsView, 0, 0);
        Resources res = getResources();

        try {

            mMarginHorizontalStep = (int) attrs.getDimension(R.styleable.CardsView_marginHorizontalStep, res.getDimension(R.dimen.default_margin));

            mMarginVerticalStep = (int) attrs.getDimension(R.styleable.CardsView_marginVerticalStep, res.getDimension(R.dimen.default_margin));

            mVisibleCardsCount = attrs.getInt(R.styleable.CardsView_visibleViewsCount, res.getInteger(R.integer.default_visible_views_count));

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

            mReverse = attrs.getBoolean(R.styleable.CardsView_reverse, false);

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

    public int getMarginHorizontalStep() {
        return mMarginHorizontalStep;
    }

    public void setMarginHorizontalStep(int step) {
        mMarginHorizontalStep = step;
    }

    public int getMarginVerticalStep() {
        return mMarginVerticalStep;
    }

    public void setMarginVerticalStep(int step) {
        mMarginVerticalStep = step;
    }

    public boolean isReverse() {
        return mReverse;
    }

    public void setReverse(boolean reverse) {
        mReverse = mReverse;
    }

    public boolean isInfinite() {
        return mInfinite;
    }

    public void setInfinite(boolean infinite) {
        mInfinite = mInfinite;
    }

    public boolean isMovable() {
        return mMovable;
    }

    public void setmMovable(boolean movable) {
        mMovable = movable;
    }

    /**
     * For listening changing of mVisibleCardsCount.
     */
    public void setOnCardsCountChangedListener(OnCardsCountChangeListener listener) {
        mOnCardsCountChangedListener = listener;
    }

    /**
     * If CardsView is child of ScrollView or NestedScrollView, to avoid scroll events when user swipe cards, set reference on ScrollView.
     */
    public void setScrollableParent(ViewParent scrollableParent) {
        mDefaultOnTouchListener.setScrollableParent(scrollableParent);
    }

    public void reload() {

        mSwipedCardsCount = 0;

        if (mAdapter != null) {

            if (mVisibleCardsCount > mAdapter.getCount()) {
                mVisibleCardsCount = mAdapter.getCount();
            }

        } else {

            throw new RuntimeException("Adapter must be initialized!");

        }

        for (int i = 0; i < mVisibleCardsCount; i++) addView(i);
    }

    private void addView(int position) {

        SwipeableCard view = (SwipeableCard) mAdapter.getView(this);

        addView(view, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (int i = getChildCount() - 1; i > 0; i--) {
                getChildAt(i).setTranslationZ(i);
            }
        }

        initView(view, position);
    }

    private void initView(final SwipeableCard view, int position) {

        int nextPos = (position + 1);
        view.setY(0);

        LayoutParams lp = (LayoutParams) view.getLayoutParams();

        lp.setMargins(mMarginLeft + (mMarginHorizontalStep * nextPos), mMarginTop + (mMarginVerticalStep * nextPos),
                mMarginRight + (mMarginHorizontalStep * nextPos), mMarginBottom + (mMarginVerticalStep * nextPos));

        if (position == 0) {

            initCard(view, position);
            view.setOnTouchCardListener(mDefaultOnTouchListener);
            view.setClipRect(0);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mStartX == 0f && mStartY == 0f) {
                        mStartX = view.getX();
                        mStartY = view.getY();
                        Log.d(TAG, String.format(Locale.getDefault(), "x: %s; y: %s", view.getX(), view.getY()));
                    }
                }
            }, 200);
        } else if (position == 1) {

            initCard(view, position);
            view.setClipRect(0);

        } else {
            view.setClipRect(mMarginHorizontalStep);
        }

        view.setTag(position);
        view.setVisibility(VISIBLE);
    }

    private void initCard(SwipeableCard card, int position) {
        mAdapter.initCard(card, mReverse ? (mAdapter.getCount() - position) - 1 : position);
    }

    void onSwipe(int animId) {

        mAnimLock = true;

        mAnimation = NO_ANIMATION;

        int count = mAdapter.getCount();

        boolean lastCardNeeded = mInfinite || mVisibleCardsCount + mSwipedCardsCount < count;

        if (lastCardNeeded) {
            addView(mVisibleCardsCount - 1);
        } else {
            mVisibleCardsCount--;
        }

        SwipeableCard view = (SwipeableCard) getChildAt(mVisibleCardsCount);

        Animation anim = AnimationUtils.loadAnimation(getContext(), animId);
        anim.setAnimationListener(this);
        view.startAnimation(anim);

        if (mInfinite && mSwipedCardsCount == count - 2) {
            mSwipedCardsCount = 0;
        } else {
            mSwipedCardsCount++;
        }

        if (mOnCardsCountChangedListener != null) {
            mOnCardsCountChangedListener.onCardsCountChanged(mSwipedCardsCount);
        }

        resizeAndReplaceCards(lastCardNeeded ? 1 : 0);
    }

    private void resizeAndReplaceCards(int lastPosition) {

        int firstPosition = mVisibleCardsCount - 1;
        int secondPostion = firstPosition - 1;

        Resources res = getResources();
        for (int i = firstPosition; i >= lastPosition; i--) {

            SwipeableCard view = (SwipeableCard) getChildAt(i);

            if (i == firstPosition) {
                view.setOnTouchCardListener(mDefaultOnTouchListener);
            } else if (i == secondPostion) {
                view.setClipRect(0);
                initCard(view, mSwipedCardsCount + 1);
                view.setOnTouchCardListener(mDefaultOnTouchListener);
            }
            Animation anim = new ResizeAnimation(view, mMarginHorizontalStep, mMarginVerticalStep);
            anim.setDuration(res.getInteger(android.R.integer.config_mediumAnimTime));
            view.startAnimation(anim);
        }
    }

    float mStartX = 0f, mStartY = 0f;

    void onMove(int dx, int dy) {

        mAnimation = NO_ANIMATION;

        SwipeableCard topView = (SwipeableCard) getChildAt(mVisibleCardsCount - 1);

        float x = topView.getX() + dx;
        float y = topView.getY() + dy;

        float defaultDeegrees = 15; //Increasing this value can lead to errors in determining the x position of view.

        topView.setRotation((int) ((defaultDeegrees * (x - dx)) / mSwipeEdge));

        topView.setX(x);
        topView.setY(y);

        if (Math.abs(x) > mSwipeEdge) {

            if (x > 0) {
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

        final SwipeableCard view = (SwipeableCard) getChildAt(mVisibleCardsCount - 1);

        final float x = view.getX();
        final float y = view.getY();

        Animation animation = new MoveToStartPosAnimation(view, x, y,
                mStartX, mStartY, mSwipeEdge);

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