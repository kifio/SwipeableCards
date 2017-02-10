package com.cards.kifio.swipeablecards;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kifio on 7/6/16.
 */
public abstract class ContentAdapter<T> {

    protected final List<T> mData;
    protected int mNextPosition = 0;
    protected boolean mReverse;

    public ContentAdapter(List<T> data) {
        mData = new ArrayList<>();
        mData.addAll(data);
    }

    public int getCount() {
        return mData.size();
    }

    protected abstract View getView(ViewGroup viewGroup);

    void reset(boolean reverse) {
        mReverse = reverse;
        mNextPosition = 0;
    }

    protected T getItem() {
        if (mReverse) {
            return mData.get(mData.size() - 1 - mNextPosition);
        } else {
            return mData.get(mNextPosition);
        }
    }
}
