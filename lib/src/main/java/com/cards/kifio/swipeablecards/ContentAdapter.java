package com.cards.kifio.swipeablecards;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by kifio on 7/6/16.
 */
public abstract class ContentAdapter<T> extends BaseAdapter {

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

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return view;
    }

    public abstract void initHolder(View view, int pos);

    public abstract void destroyView(View view);

    public interface ViewHolder<T> {

        void init(T element);

        void destroy();
    }
}
