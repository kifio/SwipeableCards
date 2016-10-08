package com.cards.kifio.swipeablecards;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import java.util.Locale;

/**
 * Created by kifio on 7/6/16.
 */
public class CardsView extends RelativeLayout implements View.OnTouchListener, Animation.AnimationListener {

    private static final String TAG = "kifio-CardsView";

    private static final float CLICK_OFFSET_LIMIT = 5;
    private static final float SWIPE_OFFSET_LIMIT = 10;
    private static final int DEFAULT_VISIBLE_VIEWS_COUNT = 3;

    private static final int ORDER_NATURAL = 0;
    private static final int ORDER_REVERSE = 1;

    private float mInitialTouchX, mInitialTouchY;
    private boolean mAnimLock, mClick;

    private ContentAdapter mAdapter;

    private float mBaseMargin;
    private float mMarginStep;
    private int mVisibleViewsCount;
    private int mOrder;
    private int mCurrentVisiblePosition;

    public CardsView(Context context) {
        super(context);
        Resources res = getResources();
        mBaseMargin = res.getDimension(R.dimen.default_base_margin);
        mMarginStep = res.getDimension(R.dimen.default_step);
        mVisibleViewsCount = DEFAULT_VISIBLE_VIEWS_COUNT;
        mOrder = ORDER_NATURAL;
    }

    public CardsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributesValues = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardsView, 0, 0);
        Resources res = getResources();
        try {
            mBaseMargin = attributesValues.getDimension(R.styleable.CardsView_topCardMargin, res.getDimension(R.dimen.default_base_margin));
            mMarginStep = attributesValues.getDimension(R.styleable.CardsView_topCardMargin, (int) res.getDimension(R.dimen.default_base_margin));
            mVisibleViewsCount = attributesValues.getInt(R.styleable.CardsView_count_visible, DEFAULT_VISIBLE_VIEWS_COUNT);
            mOrder = attributesValues.getInt(R.styleable.CardsView_order, ORDER_NATURAL);
        } finally {
            attributesValues.recycle();
        }
    }

    public void setAdapter(ContentAdapter adapter) {
        if (mVisibleViewsCount > adapter.getCount()) {
            mVisibleViewsCount = adapter.getCount();
        }
        mAdapter = adapter;
    }

    public void reload(boolean inited) {

        mAnimLock = false;  // Одна анимация в один момент времени.
        mClick = false;     // Отслеживаем нажатие.

        SwipeableCard child;

        int position;
        int count = mAdapter.getCount();
        mCurrentVisiblePosition = /*mOrder == ORDER_NATURAL ? (mVisibleViewsCount - 1) :*/ 0;

        for (int i = 0; i < count; i++) {
            if (!inited) {
                child = (SwipeableCard) mAdapter.getView(i, null, this);
                addView(child, i);
            } else {
                child = (SwipeableCard) getChildAt(i);
            }
            position = count - (i + 1);

            if (position < mVisibleViewsCount) {
                initView(child, position, i);
                if (position == 0) {
                    mAdapter.initHolder(child, mCurrentVisiblePosition);
                    child.setOnTouchListener(this);
                }
            } else
                child.setVisibility(INVISIBLE);
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

                if (Math.abs(dx + dy) <= CLICK_OFFSET_LIMIT) {
                    mClick = true;
                } else {
                    v.getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
                    if (dx < -SWIPE_OFFSET_LIMIT && !mAnimLock)
                        startAnimation((SwipeableCard) v, R.anim.slide_out_left);
                    else if (dx > SWIPE_OFFSET_LIMIT && !mAnimLock)
                        startAnimation((SwipeableCard) v, R.anim.slide_out_right);
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

    private void initView(SwipeableCard view, int relativePosition, int translation) {
        int topMargin, sideMargin;
        topMargin = (int) (mMarginStep + (mMarginStep * relativePosition));
        sideMargin = (int) mBaseMargin + topMargin;
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, view.getMeasuredHeight());
        lp.setMargins(sideMargin, topMargin, sideMargin, 0);
        view.setLayoutParams(lp);
        view.setClipRect((int) mMarginStep);
        setTranslationZ(view, translation);
        view.setVisibility(VISIBLE);
    }

    private void setTranslationZ(View view, int translation){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) view.setTranslationZ(translation);
    }

    private void startAnimation(SwipeableCard firstReview, int animId) {
        mAnimLock = true;
        int count = getChildCount() - mCurrentVisiblePosition;
        int firstInvisiblePosition = count - (mVisibleViewsCount + 1);
        mCurrentVisiblePosition++;

        if (firstInvisiblePosition >= 0) {
            SwipeableCard invisibleView = (SwipeableCard) getChildAt(firstInvisiblePosition);
            initView(invisibleView, mVisibleViewsCount - 1, firstInvisiblePosition);
        }

        Animation swipeAnimation = AnimationUtils.loadAnimation(getContext(), animId);
        swipeAnimation.setAnimationListener(this);
        firstReview.startAnimation(swipeAnimation);

//      Apply animation to srcView, for move it to destView position.
        View srcView, destView;
        int srcPos, destPos;

        for (int i = 1; i < mVisibleViewsCount; i++) {
            srcPos = (i + 1);
            destPos = i;

            srcView = getChildAt(count - srcPos);
            destView = getChildAt(count - destPos);

            if (srcView != null) {
                if (srcPos == 2) {
                    mAdapter.initHolder(srcView, mCurrentVisiblePosition);
                    srcView.setOnTouchListener(this);
                }

                Animation anim = new ReviewAnimation(srcView, (int) (mBaseMargin + mMarginStep * (i - 1)), (int) destView.getY());
                anim.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
                srcView.startAnimation(anim);
            }
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mAnimLock = false;
        View view = getChildAt(getChildCount() - 1);
        view.setOnTouchListener(null);
        view.setVisibility(GONE);
//        mAdapter.destroyView(view);
//        removeView(view);
        Log.d(TAG, String.format(Locale.getDefault(), "onAnimationEnd: %d : %d ", getChildCount(), mCurrentVisiblePosition));
        if (getChildCount() == mCurrentVisiblePosition) reload(true);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}