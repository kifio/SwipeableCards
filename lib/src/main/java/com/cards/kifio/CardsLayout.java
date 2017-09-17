package com.cards.kifio;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import com.cards.kifio.swipeablecards.R;

public class CardsLayout extends RelativeLayout implements
        Animation.AnimationListener, CardsAdapter.DataSetObserver {

    private static final float CARD_ROTATION_ANGLE = 15;

    public static final int LEFT_SWIPE = 0;
    public static final int RIGHT_SWIPE = 1;
    public static final int MOVE_TO_INITIAL = 2;
    public static final int NO_ANIMATION = 3;

    private float mVerticalPositionOffset;
    private float mHorizontalMarginOffset;
    private float mHorizontalMargin;

    private boolean mTouchLock = false;
    private int mAnimation = NO_ANIMATION;
    private int mTag;
    private int mRemovedCardsCounter;
    private float mCardsTop;
    private OnSwipeCardListener mOnSwipeCardListener;
    private CardsPositionObserver mCardsPositionObserver;
    private ViewParent mScrollableParent;
    private CardsAdapter mAdapter;
    private DisplayMetrics mDisplayMetrics;


    public CardsLayout(Context context, AttributeSet attrsSet) {
        super(context, attrsSet);

        TypedArray attrs = context.getTheme().obtainStyledAttributes(attrsSet, R.styleable.CardsView, 0, 0);
        Resources res = getResources();
        mDisplayMetrics = getResources().getDisplayMetrics();

        try {

            int marginID = R.styleable.CardsView_horizontal_margin;
            int defaultMarginID = (int) res.getDimension(R.dimen.dp16);
            int marginDifferenceID = R.styleable.CardsView_horizontal_margin;
            int defaultMarginOffsetID = (int) res.getDimension(R.dimen.dp8);
            int verticalOffsetID = R.styleable.CardsView_vertical_offset;
            int defaultVerticalOffsetID = (int) res.getDimension(R.dimen.dp8);

            mHorizontalMargin = attrs.getDimension(marginID, defaultMarginID);
            mHorizontalMarginOffset = attrs.getDimension(marginDifferenceID, defaultMarginOffsetID);
            mVerticalPositionOffset = attrs.getDimension(verticalOffsetID, defaultVerticalOffsetID);

        } finally {
            attrs.recycle();
        }
    }

    public void setScrollableParent(ViewParent scrollableParent) {
        mScrollableParent = scrollableParent;
    }

    public void setOnSwipeCardListener(OnSwipeCardListener listener) {
        mOnSwipeCardListener = listener;
    }

    public void setAdapter(CardsAdapter adapter) {
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(this);
    }

    public void setCardsPositionObserver(CardsPositionObserver observer) {
        mCardsPositionObserver = observer;
    }

    private void setOnTouchListener(ViewGroup viewGroup) {
        OnTouchCardListener listener = new OnTouchCardListener(this, dpToPx(4));
        listener.setScrollableParent(mScrollableParent);
        viewGroup.setOnTouchListener(listener);
        setOnNestedViewTouchListener(viewGroup, listener);
    }

    private void setOnNestedViewTouchListener(View view, OnTouchCardListener listener) {
        view.setOnTouchListener(listener);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setOnNestedViewTouchListener(viewGroup.getChildAt(i), listener);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mCardsTop = mCardsPositionObserver.getCardsTop();
    }

    private void moveCard(float dx, float dy) {
        mAnimation = NO_ANIMATION;
        ViewGroup topView = findViewWithTag(mRemovedCardsCounter);
        float x = topView.getX() + dx;
        float y = topView.getY() + dy;
        topView.setRotation((int) ((CARD_ROTATION_ANGLE * (x - dx)) / 360));
        topView.setX(x);
        topView.setY(y);
        mAnimation = Math.abs(x) > 160
                ? x > 0 ? RIGHT_SWIPE : LEFT_SWIPE
                : MOVE_TO_INITIAL;
    }

    private void swipeCard(int direction, int animId) {
        mTouchLock = true;
        mAnimation = NO_ANIMATION;
        View view = findViewWithTag(mRemovedCardsCounter);
        setOnNestedViewTouchListener(view, null);
        Animation anim = AnimationUtils.loadAnimation(getContext(), animId);
        anim.setAnimationListener(this);
        view.startAnimation(anim);
        mOnSwipeCardListener.onSwipeCard(direction);
        resizeAndReplaceCards();
    }

    private void initCard(int position, int elevation) {
        int margin = (int) (mHorizontalMargin + mHorizontalMarginOffset * position);
        CardView card = mAdapter.getView(this, margin);
        card.setCardElevation(elevation);
        card.setY(mVerticalPositionOffset * position);
        addView(card);
        card.setTag(mTag);
        setOnTouchListener(card);
        mTag++;
    }

    private void resizeAndReplaceCards() {
        for (int i = mRemovedCardsCounter + 1; i < mTag; i++) {
            CardView view = findViewWithTag(i);
            Animation anim = new ResizeAnimation(view, mHorizontalMarginOffset,
                    mVerticalPositionOffset);
            anim.setAnimationListener(this);
            view.startAnimation(anim);
        }
        initCard(mAdapter.getCount() - 1, (dpToPx(1)));
    }

    private void returnCardToPosition() {
        mAnimation = NO_ANIMATION;
        final View view = findViewWithTag(mRemovedCardsCounter);
        Animation animation = new MoveToStartPosAnimation(view, view.getX(), view.getY(),
                mHorizontalMargin, mCardsTop);
        view.startAnimation(animation);
    }


    @Override
    public void onChange() {
        int count = mAdapter.getCount();
        for (int i = mRemovedCardsCounter; i < mTag; i++) {
            findViewWithTag(i).setVisibility(View.INVISIBLE);
            removeView(findViewWithTag(i));
        }
        mRemovedCardsCounter = 0;
        mTag = 0;
        for (int i = 0; i < count; i++) {
            initCard(i, dpToPx(1 + count - i));
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (!ResizeAnimation.class.isInstance(animation)) {
            View view = findViewWithTag(mRemovedCardsCounter);
            view.setVisibility(View.INVISIBLE);
            view.setOnTouchListener(null);
            view.setTag(null);
            postDelayed(() -> removeView(view), 100);
            mRemovedCardsCounter++;
            CardView card;
            for (int i = mRemovedCardsCounter; i < mTag; i++) {
                card = findViewWithTag(i);
                card.setCardElevation(dpToPx(1 + mTag - i));
            }
        }
        mTouchLock = false;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * mDisplayMetrics.density);
    }

    public void dispose() {
        mOnSwipeCardListener = null;
        mCardsPositionObserver = null;
        mScrollableParent = null;
        if (mAdapter != null) {
            mAdapter.dispose();
        }
        mAdapter = null;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public interface OnSwipeCardListener {
        void onSwipeCard(int direction);
    }

    public interface CardsPositionObserver {
        int getCardsTop();
    }

    private static class MoveToStartPosAnimation extends Animation {

        private View mView;
        private int mX, mY;
        private int mDx, mDy, mXLimit = 100;

        MoveToStartPosAnimation(View view, float x, float y, float startX, float startY) {
            setDuration(200);
            mView = view;
            mX = (int) x;
            mY = (int) y;
            mDx = (int) startX;
            mDy = (int) (y - startY);
        }

        @Override
        protected void applyTransformation(float time, Transformation t) {
            int x = (int) (mX * (1 - time)) + mDx;
            int y = (int) (mY - (mDy * (time)));
            mView.setRotation(((CARD_ROTATION_ANGLE * (x - mDx)) / mXLimit));
            mView.setX(x);
            mView.setY(y);
        }
    }

    private static class ResizeAnimation extends Animation {

        private float mHorizontalMargin;
        private float mVerticalPositionOffset;
        private float mInitialPosition;
        private float mStartWidth;
        private View mView;

        private RelativeLayout.LayoutParams mParams;

        ResizeAnimation(View view, float margin, float verticalPositionOffset) {
            setDuration(50);

            mView = view;
            mParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

            mInitialPosition = view.getY();
            mStartWidth = mParams.leftMargin;

            mHorizontalMargin = margin;
            mVerticalPositionOffset = verticalPositionOffset;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            mParams.leftMargin = (int) (mStartWidth - mHorizontalMargin * interpolatedTime);
            mParams.rightMargin = (int) (mStartWidth - mHorizontalMargin * interpolatedTime);
            mView.setY(mInitialPosition - mVerticalPositionOffset * interpolatedTime);
            mView.requestLayout();
        }
    }

    private static class OnTouchCardListener implements View.OnTouchListener {

        private CardsLayout mCardsManager;
        private ViewParent mScrollableParent;
        private float mInitialTouchX, mInitialTouchY;
        private boolean mClick;
        private boolean mScrollLock;
        private boolean mScrollIntercept;
        private int mScrollLimit;

        OnTouchCardListener(CardsLayout cm, int scrollLimit) {
            mCardsManager = cm;
            mScrollLimit = scrollLimit;
            mScrollableParent = cm.mScrollableParent;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!mCardsManager.mTouchLock) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        mClick = true;
                        mInitialTouchX = event.getX();
                        mInitialTouchY = event.getY();
                        mScrollIntercept = true;
                        if (mScrollableParent != null) {
                            mScrollableParent.requestDisallowInterceptTouchEvent(true);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - mInitialTouchX;
                        float dy = event.getY() - mInitialTouchY;
                        if (Math.abs(dx) > 4 && Math.abs(dy) > 4) mClick = false;
                        if (!mClick && !mScrollLock) {
                            if (Math.abs(dy) < Math.abs(dx)) {
                                mScrollLock = true;
                            } else if (Math.abs(dy) > mScrollLimit && Math.abs(dy) > Math.abs(dx)) {
                                mScrollIntercept = false;
                                if (mScrollableParent != null) {
                                    mScrollableParent.requestDisallowInterceptTouchEvent(false);
                                }
                            }
                        }

                        if (!mClick && mScrollLock && mScrollIntercept) {
                            mCardsManager.moveCard(dx, dy);
                        }
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mScrollIntercept = false;
                        if (mScrollableParent !=null) {
                            mScrollableParent.requestDisallowInterceptTouchEvent(true);
                        }
                        mScrollLock = false;
                        if (mClick) {
                            v.performClick();
                        } else {
                            animateCard();
                        }
                        return true;
                    default:
                        return true;
                }
            }
            return true;
        }

        private void animateCard() {
            if (mCardsManager.mAnimation == LEFT_SWIPE) {
                mCardsManager.swipeCard(LEFT_SWIPE, R.anim.slide_out_left);
            } else if (mCardsManager.mAnimation == RIGHT_SWIPE) {
                mCardsManager.swipeCard(RIGHT_SWIPE, R.anim.slide_out_right);
            } else if (mCardsManager.mAnimation == MOVE_TO_INITIAL) {
                mCardsManager.returnCardToPosition();
            }
        }

        void setScrollableParent(ViewParent scrollableParent) {
            mScrollableParent = scrollableParent;
        }
    }
}