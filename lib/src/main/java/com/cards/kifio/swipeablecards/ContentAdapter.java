package com.cards.kifio.swipeablecards;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by kifio on 7/6/16.
 */
public abstract class ContentAdapter<T> {

    private final ArrayList<T> mData;

    public ContentAdapter(ArrayList<T> data) {
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

    public T getItem(int i) {
        return mData.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        return view;
    }
}
