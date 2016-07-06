package com.cards.kifio.example;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by kifio on 7/6/16.
 */
class ContentAdapter<T> extends BaseAdapter {

    private static final String TAG = "kifio-ContentAdapter";
    private final T[] mData;

    public ContentAdapter(T[] data) {
        mData = data;
    }

    @Override
    public int getCount() {
        if (mData != null) Log.d(TAG, "getCount: " + mData.length);
        return mData != null ? mData.length : 0;
    }

    @Override
    public T getItem(int i) {
        return mData[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return inflater.inflate(R.layout.v_card, null);
    }
}
