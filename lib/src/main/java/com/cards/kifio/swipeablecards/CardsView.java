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
import android.widget.FrameLayout;

import com.cards.kifio.swipeablecards.anim.MoveToStartPosAnimation;
import com.cards.kifio.swipeablecards.anim.ResizeAnimation;

import static com.cards.kifio.swipeablecards.AnimationTypes.LEFT_SWIPE;
import static com.cards.kifio.swipeablecards.AnimationTypes.MOVE_TO_INITIAL;
import static com.cards.kifio.swipeablecards.AnimationTypes.NO_ANIMATION;
import static com.cards.kifio.swipeablecards.AnimationTypes.RIGHT_SWIPE;

/**
 * Created by kifio on 7/6/16.
 */
public class CardsView extends FrameLayout implements Animation.AnimationListener {

    private static final String TAG = "kifio-CardsView";

    /**
     * Horizontal space between edges of 2 neighboring cards.
     */
    private int mWidthDiff;

    /**
     * Diff between top margin of 2 neighboring cards.
     */
    private int mYPositionDiff;

    /**
     * If this value will be more then count of cards in adapter, it's will be set to count of cards in adapter.
     */
    private int mVisibleCardsCount;

    private int mRemovedCards;

    private int mCardsInContainer;

    private int mTag = 0;

    /**
     * Flag for enabling infinite cards mode. In this mode, after initialization last item from adapter, first item from adapter will be initialized.
     */
    boolean mInfinite;

    /**
     * Flag for enabling movable mode. In this mode, OnTouchCardListener will handle action ACTION_MOVE and change position of top card.
     */
    boolean mMovable;

    @AnimationTypes.AnimationType int mAnimation = NO_ANIMATION;

    private int mSwipeEdge;

    private ContentAdapter mAdapter;
    private OnTouchCardListener mOnTouchListener = new OnTouchCardListener(this);
    private OnSwipeCardListener mOnCardSwipeListener;

    boolean mAnimLock = false;
    boolean mTouchLock = false;

    private Runnable mSetHeightRunnable = new Runnable() {
        @Override
        public void run() {
            getLayoutParams().height = getMeasuredHeight() + mYPositionDiff * mVisibleCardsCount;
            requestLayout();
        }
    };

    public CardsView(Context context, int visibleCardsCount) {
        super(context);
        mVisibleCardsCount = visibleCardsCount;
        post(mSetHeightRunnable);
    }

    public CardsView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        TypedArray attrs = context.getTheme().obtainStyledAttributes(attrSet, R.styleable.CardsView, 0, 0);
        Resources res = getResources();

        try {

            mWidthDiff = (int) attrs.getDimension(R.styleable.CardsView_yPositionDiff, res.getDimension(R.dimen.default_margin));

            mYPositionDiff = (int) attrs.getDimension(R.styleable.CardsView_widthDiff, res.getDimension(R.dimen.default_margin));

            mVisibleCardsCount = attrs.getInt(R.styleable.CardsView_visibleViewsCount, res.getInteger(R.integer.default_visible_views_count));

            mInfinite = attrs.getBoolean(R.styleable.CardsView_infinite, false);

            mMovable = attrs.getBoolean(R.styleable.CardsView_movable, false);

        } finally {
            attrs.recycle();
        }

        post(mSetHeightRunnable);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mSwipeEdge = 2 * (getWidth() / 5);
    }

    public void setContentAdapter(ContentAdapter adapter) {
        mAdapter = adapter;
    }

    public void setOnCardSwipeListener(OnSwipeCardListener listener) {
        mOnCardSwipeListener = listener;
    }

    public void setScrollableParent(ViewParent scrollableParent) {
        mOnTouchListener.setScrollableParent(scrollableParent);
    }

    public void reload() {

        if (!mInfinite) {
            mTag = 0;
        }

        mRemovedCards = 0;

        if (mVisibleCardsCount < 2) {
            throw new RuntimeException("At least 2 cards must be visible.");
        }

        if (mAdapter != null) {

            if (mVisibleCardsCount > mAdapter.getCount()) {
                mVisibleCardsCount = mAdapter.getCount();
            }

            mCardsInContainer = mVisibleCardsCount;

        } else {

            throw new RuntimeException("Adapter must be initialized!");

        }

        for (int i = 0; i < mVisibleCardsCount; i++) addView(i);

    }

    private void addView(final int position) {

        final View view = mAdapter.getView(this);
        addView(view, 0);

        view.setVisibility(INVISIBLE);
        view.setOnTouchListener(mOnTouchListener);
        view.setY(mWidthDiff * position);
        view.setTag(mTag);

        mAdapter.mNextPosition++;
        mTag++;

        if (mAdapter.mNextPosition == mAdapter.getCount()) {
            mAdapter.mNextPosition = 0;
        }

        view.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
                lp.width = getMeasuredWidth() - (2 * mWidthDiff * position);
                requestLayout();
                view.setVisibility(VISIBLE);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (int i = getChildCount() - 1; i > 0; i--) {
                getChildAt(i).setTranslationZ(i);
            }
        }
    }

    void onSwipe(int animId) {

        mAnimLock = true;

        mAnimation = NO_ANIMATION;

        int count = mAdapter.getCount();

        View view = findViewWithTag(mRemovedCards);
        view.setTag(null);

        Animation anim = AnimationUtils.loadAnimation(getContext(), animId);
        anim.setAnimationListener(this);
        view.startAnimation(anim);

        if (mInfinite || mRemovedCards + mVisibleCardsCount < count) {
            addView(mVisibleCardsCount - 1);
        } else if (mRemovedCards + mVisibleCardsCount > count) {
            mCardsInContainer--;
        }

        if (mOnCardSwipeListener != null) {
            mOnCardSwipeListener.onSwipeCard(mRemovedCards);
        }

        if (mCardsInContainer != 0) {
            resizeAndReplaceCards(mCardsInContainer + mRemovedCards);
        }

    }

    private void resizeAndReplaceCards(int lastPosition) {

        mRemovedCards++;

        int firstPosition = mRemovedCards;
        int secondPostion = firstPosition + 1;

        Resources res = getResources();

        for (int i = firstPosition; i < lastPosition; i++) {

            View view = findViewWithTag(i);

            if (i <= secondPostion) {
                view.setOnTouchListener(mOnTouchListener);
            }

            Animation anim = new ResizeAnimation(view, mWidthDiff, mYPositionDiff);
            anim.setDuration(res.getInteger(android.R.integer.config_mediumAnimTime));
            anim.setAnimationListener(this);
            view.startAnimation(anim);
        }
    }

    float mStartX = 0f, mStartY = 0f;

    void onMove(int dx, int dy) {

        mAnimation = NO_ANIMATION;

        View topView = findViewWithTag(mRemovedCards);

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

    public void onStopMoving() {

        mAnimation = NO_ANIMATION;

        final View view = findViewWithTag(mRemovedCards);

        final float x = view.getX();
        final float y = view.getY();

        Animation animation = new MoveToStartPosAnimation(view, x, y, mStartX, mStartY, mSwipeEdge);
        animation.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        view.startAnimation(animation);
    }

    @Override
    public void onAnimationEnd(Animation animation) {

        mAnimLock = false;
        mTouchLock = false;

        if (!ResizeAnimation.class.isInstance(animation)) {

            int pos = getChildCount() - 1;
            View view = getChildAt(pos);
            view.setOnTouchListener(null);
            removeView(view);
            if (pos == 0)
                reload();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mTouchLock = true;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public interface OnSwipeCardListener {
        void onSwipeCard(int count);
    }
}