package com.cards.kifio.swipeablecards;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kifio on 7/6/16.
 */
public abstract class ContentAdapter<T> {

    private final List<T> mData = new ArrayList<>();
    protected int mNextPosition = 0;

    public ContentAdapter(List<T> data) {
        mData.addAll(data);
    }

    public int getCount() {
        return mData.size();
    }

    public void clear() {
        mData.clear();
    }

    public void update(List<T> data) {
        mData.addAll(data);
    }

    protected abstract View getView(ViewGroup viewGroup);

    protected T getNextItem() {
        return mData.get(mNextPosition < mData.size() ? mNextPosition : 0);
    }
}
