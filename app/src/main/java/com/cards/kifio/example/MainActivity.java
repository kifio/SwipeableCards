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

        String[] data = getResources().getStringArray(R.array.lorem_ipsum);

        CardsView cardsView0 = (CardsView) findViewById(R.id.cardsView0);
        CardsView cardsView1 = (CardsView) findViewById(R.id.cardsView1);
        CardsView cardsView2 = (CardsView) findViewById(R.id.cardsView2);
        CardsView cardsView3 = (CardsView) findViewById(R.id.cardsView3);

        ContentAdapter<String> adapter0 = new Adapter(this, Arrays.asList(data));
        ContentAdapter<String> adapter1 = new Adapter(this, Arrays.asList(data));
        ContentAdapter<String> adapter2 = new Adapter(this, Arrays.asList(data));
        ContentAdapter<String> adapter3 = new Adapter(this, Arrays.asList(data));

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

        Adapter(Context context, List<String> data) {
            super(data);
            mContext = context;
        }

        @Override
        public SwipeableCard getView(ViewGroup viewGroup) {
            return (TextCard) LayoutInflater.from(mContext).inflate(R.layout.v_card, viewGroup, false);
        }

        @Override
        public void initCard(SwipeableCard card, int position) {
            TextView text = (TextView) card.findViewById(R.id.text);
            TextView cardNumber = (TextView) card.findViewById(R.id.cardNumber);
            text.setText(mData.get(position));
            cardNumber.setText(String.valueOf(position));

        }

    }
}
