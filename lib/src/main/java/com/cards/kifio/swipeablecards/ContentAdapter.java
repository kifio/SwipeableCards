package com.cards.kifio.swipeablecards;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kifio on 7/6/16.
 */
public abstract class ContentAdapter<T> {

    protected final List<T> mData;

    public ContentAdapter(List<T> data) {
        mData = new ArrayList<>();
        mData.addAll(data);
    }

    public void add(T item){
        mData.add(item);
    }

    public void remove(T item) {
        mData.remove(item);
    }

    public void add(int pos) {
        mData.remove(pos);
    }

    public int getCount() {
        return mData.size();
    }

    public abstract View getView(ViewGroup viewGroup);

    public abstract void initCard(SwipeableCard card, int i);
}
