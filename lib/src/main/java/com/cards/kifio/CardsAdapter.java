package com.cards.kifio;

import android.support.v7.widget.CardView;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

public abstract class CardsAdapter<T> {

    protected final LinkedList<T> mData = new LinkedList<>();
    protected int mCurrent;

    private DataSetObserver mObserver;

    public int getCount() {
        return mData.size();
    }

    public void clear() {
        mData.clear();
    }

    public void update(List<T> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        mObserver = observer;
    }

    public void notifyDataSetChanged() {
        mObserver.onChange();
    }

    public void dispose() {
        mObserver = null;
        clear();
    }

    public abstract CardView getView(ViewGroup viewGroup, int margin);

    public interface DataSetObserver {
        void onChange();
    }

}