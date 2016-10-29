package com.cards.kifio.example;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cards.kifio.swipeablecards.CardsView;
import com.cards.kifio.swipeablecards.ContentAdapter;
import com.cards.kifio.swipeablecards.SwipeableCard;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        CardsView cardsView = (CardsView) findViewById(R.id.cardsView);

        ArrayList<String> data = new ArrayList<>();
        data.add("Moscow");
        data.add("London");
        data.add("London");
        data.add("London");
        data.add("London");

        ContentAdapter<String> adapter = new Adapter(this, data);
        cardsView.setDataSet(adapter);
        cardsView.reload();
    }

    static class Adapter extends ContentAdapter<String> {

        private final Context mContext;

        public Adapter(Context context, ArrayList<String> data) {
            super(data);
            mContext = context;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.v_card, viewGroup, false);
                TextView tv = (TextView) view.findViewById(R.id.number);
                tv.setText(String.valueOf(i));
                view.setTag(new SwipeableCardHolder());
            }
            return view;
        }

        @Override
        public void initCard(SwipeableCard child) {
            SwipeableCardHolder holder = (SwipeableCardHolder) child.getTag();
        }

    }
}
