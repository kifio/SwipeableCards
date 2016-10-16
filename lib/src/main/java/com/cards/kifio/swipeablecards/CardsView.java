package com.cards.kifio.swipeablecards;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private float mInitialTouchX, mInitialTouchY;
    private boolean mAnimLock, mClick;

    private ContentAdapter mAdapter;
    private Animation mSwipeAnimationLeft, mSwipeAnimationRight;

    private float mBaseMargin;
    private float mMarginStep;
    private int mVisibleViewsCount = DEFAULT_VISIBLE_VIEWS_COUNT;
    private int mOrder = ORDER_NATURAL;
    private boolean mInfinite = true;


    public CardsView(Context context) {
        super(context);
        initFields(context, null);
        initAnimation();
    }

    public CardsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFields(context, attrs);
        initAnimation();
    }

    private void initFields(Context context, AttributeSet attrs) {
        Resources res = getResources();
        if (attrs == null) {
            mBaseMargin = res.getDimension(R.dimen.default_base_margin);
            mMarginStep = res.getDimension(R.dimen.default_step);
        } else {
            TypedArray attributesValues = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardsView, 0, 0);
            try {
                mBaseMargin = attributesValues.getDimension(R.styleable.CardsView_topCardMargin, res.getDimension(R.dimen.default_base_margin));
                mMarginStep = attributesValues.getDimension(R.styleable.CardsView_topCardMargin, (int) res.getDimension(R.dimen.default_base_margin));
                mVisibleViewsCount = attributesValues.getInt(R.styleable.CardsView_count_visible, DEFAULT_VISIBLE_VIEWS_COUNT);
                mOrder = attributesValues.getInt(R.styleable.CardsView_order, ORDER_NATURAL);
                mInfinite = attributesValues.getBoolean(R.styleable.CardsView_infinite, true);
            } finally {
                attributesValues.recycle();
            }
        }
    }

    private void initAnimation() {
        mSwipeAnimationLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
        mSwipeAnimationRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
        mSwipeAnimationLeft.setAnimationListener(this);
        mSwipeAnimationRight.setAnimationListener(this);
    }

    public void setAdapter(ContentAdapter adapter) {
        if (mVisibleViewsCount > adapter.getCount()) {
            mVisibleViewsCount = adapter.getCount();
        }
        mAdapter = adapter;
    }

    public void reload() {

        mAnimLock = false;  // Одна анимация в один момент времени.
        mClick = false;     // Отслеживаем нажатие.

        int position;
        SwipeableCard view;

        for (int i = 0; i < mVisibleViewsCount; i++) {
            view = addItem(i);
            position = (mVisibleViewsCount - 1) - i;
            initView(view, position, i);
//            if (position == 0) {
                view.setOnTouchListener(this);
//            }
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

                float dx = event.getX() - mInitialTouchX;
                float dy = event.getY() - mInitialTouchY;

                if (Math.abs(dx + dy) <= CLICK_OFFSET_LIMIT) {
                    mClick = true;
                } else {
                    v.getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
                    if (!mAnimLock) {
                        startAnimation(v, dx);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mClick) {
                    v.performClick();
                }
                return false;

            default:
                return false;
        }
        return true;
    }

    private SwipeableCard addItem(int position) {
        SwipeableCard view = (SwipeableCard) mAdapter.getView(position, null, this);
        addView(view, position);
        return view;
    }

    private void initView(SwipeableCard view, int relativePosition, int transition) {
        int topMargin, sideMargin;
        topMargin = (int) (mMarginStep + (mMarginStep * relativePosition));
        sideMargin = (int) mBaseMargin + topMargin;
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, view.getMeasuredHeight());
        lp.setMargins(sideMargin, topMargin, sideMargin, 0);
        view.setLayoutParams(lp);
        setTranslationZ(view, transition);
        view.setVisibility(VISIBLE);
        view.setTag(transition);
    }

    private void setTranslationZ(View view, int translation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            view.setTranslationZ(translation);
    }

    private void startAnimation(View view, float dx) {
        if (dx < -SWIPE_OFFSET_LIMIT && !mAnimLock) {
            mAnimLock = true;
            view.startAnimation(mSwipeAnimationLeft);
        } else if (dx > SWIPE_OFFSET_LIMIT && !mAnimLock) {
            mAnimLock = true;
            view.startAnimation(mSwipeAnimationRight);
        }

        setTranslationZ(view, mVisibleViewsCount + 1);
    }

    @Override
    public void onAnimationEnd(Animation animation) {

        View view = findViewWithTag(mVisibleViewsCount - 1);
        View[] views = new View[mVisibleViewsCount - 1];

        for (int i = 0; i < mVisibleViewsCount - 1; i++) {
            View srcView = findViewWithTag(i);
            View destView = findViewWithTag(i + 1);
            views[i] = srcView;

            setTranslationZ(srcView, i + 1);
                Animation anim = new ReviewAnimation(srcView, (int) mBaseMargin, destView == null ? 0 : (int) destView.getY());
                anim.setDuration(200);
                srcView.startAnimation(anim);
        }

        resetTags(views);

        if (mInfinite) {
            setLastItemVisible(view);
        } else {
            view.setVisibility(GONE);
        }

    }

    private void resetTags(View[] views) {
        int i = 1;
        for (View v : views) {
            v.setTag(i);
            i++;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void setLastItemVisible(View view) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.leftMargin = (int) (mBaseMargin * (mVisibleViewsCount + 1));
        lp.rightMargin = (int) (mBaseMargin * (mVisibleViewsCount + 1));
        view.setY(getTop() + (mBaseMargin + mBaseMargin + mBaseMargin));
        setTranslationZ(view, 0);
        view.setTag(0);
//        view.setVisibility(VISIBLE);
    }
}