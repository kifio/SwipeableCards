package com.cards.kifio.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.cards.kifio.swipeablecards.CardsView;
import com.cards.kifio.swipeablecards.ContentAdapter;
import com.cards.kifio.swipeablecards.SwipeableCard;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        CardsView cardsView = (CardsView) findViewById(R.id.cardsView);
        cardsView.setAdapter(new Adapter());
        cardsView.setScrollableParent((ViewParent) findViewById(R.id.root));
        cardsView.reload();
    }

    static class Adapter extends ContentAdapter<String> {

        private static final int COUNT = 15;

        Adapter() {
            super(new ArrayList<String>(COUNT));
        }

        @Override
        public int getCount() {
            return COUNT;
        }

        @Override
        public SwipeableCard getView(ViewGroup viewGroup) {
            return (SampleCard) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.v_card, viewGroup, false);
        }

        @Override
        public void initCard(SwipeableCard card, int position) {
            TextView text = (TextView) card.findViewById(R.id.text);
            text.setText(String.format(Locale.getDefault(), card.getContext().getString(R.string.card_text), position));
        }
    }
}
