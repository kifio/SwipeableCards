package com.cards.kifio.swipeablecards;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

/**
 * Created by kifio on 7/6/16.
 */
public class CardsView extends RelativeLayout implements View.OnTouchListener, Animation.AnimationListener {

    private static final String TAG = "kifio-CardsView";

    private static final float CLICK_OFFSET_LIMIT = 5;
    private static final float SWIPE_OFFSET_LIMIT = 10;
    private static final int DEFAULT_VISIBLE_VIEWS_COUNT = 3;

    private float mInitialTouchX, mInitialTouchY;
    private boolean mAnimLock, mClick;

    private ContentAdapter mAdapter;

    private int mBaseMargin;
    private int mMarginStep;
    private int mVisibleViewsCount;
    private int mNext;

    public CardsView(Context context) {
        super(context);
        Resources res = getResources();
        init((int) res.getDimension(R.dimen.default_base_margin), (int) res.getDimension(R.dimen.default_step), DEFAULT_VISIBLE_VIEWS_COUNT);
    }

    public CardsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributesValues = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardsView, 0, 0);
        Resources res = getResources();
        try {
            init((int) attributesValues.getDimension(R.styleable.CardsView_topCardMargin, (int) res.getDimension(R.dimen.default_base_margin)),
                    (int) attributesValues.getDimension(R.styleable.CardsView_marginStep, (int) res.getDimension(R.dimen.default_step)),
                    attributesValues.getInt(R.styleable.CardsView_count_visible, DEFAULT_VISIBLE_VIEWS_COUNT));
        } finally {
            attributesValues.recycle();
        }
    }

    private void init(int baseMargin, int marginStep, int visibleViewsCount) {
        mBaseMargin = baseMargin;
        mMarginStep = marginStep;
        mVisibleViewsCount = visibleViewsCount;
    }

    public void setAdapter(ContentAdapter adapter) throws Throwable {
        if (mVisibleViewsCount > adapter.getCount())
            throw new Throwable("Not enough elements in adapter. Must be at least: " + mVisibleViewsCount);
        mAdapter = adapter;
    }

    public void reload() {
        mAnimLock = false; mClick = false;
        mNext = 0;
        View child;
        removeAllViews();
        int position, count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            child = mAdapter.getView(i, null, this);
            addView(child, i);
            position = count - (i + 1);
            if (position < mVisibleViewsCount) {
                initView(child, position, i);
                if (position == 0) {
                    mAdapter.initHolder(child, mNext);
                    child.setOnTouchListener(this);
                }
            } else
                child.setVisibility(INVISIBLE);
        }
        mNext++;
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
                        startAnimation(v, R.anim.slide_out_left);
                    else if (dx > SWIPE_OFFSET_LIMIT && !mAnimLock)
                        startAnimation(v, R.anim.slide_out_right);
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

    private void initView(View view, int relativePosition, int translation) {
        int topMargin, sideMargin;
        topMargin = mMarginStep * relativePosition;
        sideMargin = mBaseMargin + topMargin;
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, view.getMeasuredHeight());
        lp.setMargins(sideMargin, topMargin, sideMargin, 0);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        view.setLayoutParams(lp);
        setTranslationZ(view, translation);
        view.setVisibility(VISIBLE);
    }

    private void setTranslationZ(View view, int translation){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) view.setTranslationZ(translation);
    }

    private void startAnimation(View firstReview, int animId) {
        mAnimLock = true;
        int count = getChildCount();
        int firstInvisiblePosition = count - (mVisibleViewsCount + 1);

        if (firstInvisiblePosition >= 0) {
            View invisibleView = getChildAt(firstInvisiblePosition);
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
                    mAdapter.initHolder(srcView, mNext);
                    srcView.setOnTouchListener(this);
                }

                Animation anim = new ReviewAnimation(srcView, mBaseMargin + mMarginStep * (i - 1), (int) destView.getY());
                anim.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
                srcView.startAnimation(anim);
            }
        }
        mNext++;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mAnimLock = false;
        View view = getChildAt(getChildCount() - 1);
        view.setOnTouchListener(null);
        mAdapter.destroyView(view);
        removeView(view);
        if (getChildCount() == 0) reload();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}