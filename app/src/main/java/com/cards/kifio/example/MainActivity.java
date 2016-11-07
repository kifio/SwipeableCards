package com.cards.kifio.example;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cards.kifio.swipeablecards.CardsView;
import com.cards.kifio.swipeablecards.ContentAdapter;
import com.cards.kifio.swipeablecards.OnTouchCardListener;
import com.cards.kifio.swipeablecards.SwipeableCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        CardsView cardsView0 = (CardsView) findViewById(R.id.cardsView0);
        CardsView cardsView1 = (CardsView) findViewById(R.id.cardsView1);
        CardsView cardsView2 = (CardsView) findViewById(R.id.cardsView2);
        CardsView cardsView3 = (CardsView) findViewById(R.id.cardsView3);
        OnTouchCardListener listener0 = new OnTouchCardListener(cardsView0, (ScrollView) findViewById(R.id.root));
        OnTouchCardListener listener1 = new OnTouchCardListener(cardsView1, (ScrollView) findViewById(R.id.root));
        OnTouchCardListener listener2 = new OnTouchCardListener(cardsView2, (ScrollView) findViewById(R.id.root));
        OnTouchCardListener listener3 = new OnTouchCardListener(cardsView3, (ScrollView) findViewById(R.id.root));

        String[] data = getResources().getStringArray(R.array.lorem_ipsum);
        ContentAdapter<String> adapter0 = new Adapter(this, Arrays.asList(data), listener0);
        ContentAdapter<String> adapter1 = new Adapter(this, Arrays.asList(data), listener1);
        ContentAdapter<String> adapter2 = new Adapter(this, Arrays.asList(data), listener2);
        ContentAdapter<String> adapter3 = new Adapter(this, Arrays.asList(data), listener3);

        cardsView0.setDataSet(adapter0);
        cardsView1.setDataSet(adapter1);
        cardsView2.setDataSet(adapter2);
        cardsView3.setDataSet(adapter3);

        cardsView0.reload();
        cardsView1.reload();
        cardsView2.reload();
        cardsView3.reload();
    }

    static class Adapter extends ContentAdapter<String> {

        private final Context mContext;
        private OnTouchCardListener mListener;

        Adapter(Context context, List<String> data, OnTouchCardListener listener) {
            super(data);
            mContext = context;
            mListener = listener;
        }

        @Override
        public SwipeableCard getView(int i, ViewGroup viewGroup) {

            TextCard card = (TextCard) LayoutInflater.from(mContext).inflate(R.layout.v_card, viewGroup, false);

            TextView text = (TextView) card.findViewById(R.id.text);
            TextView cardNumber = (TextView) card.findViewById(R.id.cardNumber);

            text.setText(mData.get(i));
            cardNumber.setText(String.valueOf(i));

            return card;
        }

        @Override
        public void initCard(SwipeableCard child) {
            child.setOnTouchCardListener(mListener);
        }

    }
}
