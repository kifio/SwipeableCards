package com.cards.kifio.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cards.kifio.swipeablecards.CardsView;
import com.cards.kifio.swipeablecards.ContentAdapter;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements CardsView.OnSwipeCardListener {

    private static final String TAG = "kifio-MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        CardsView cardsView = (CardsView) findViewById(R.id.cards_view);
        cardsView.setContentAdapter(new CardsAdapter(getResources().getStringArray(R.array.cards_titles)));
        cardsView.setOnCardSwipeListener(this);
        cardsView.reload();
    }

    @Override
    public void onSwipeCard(int count) {
        Log.d(TAG, "onSwipeCard: " + count);
    }

    static class CardsAdapter extends ContentAdapter<String> {

        CardsAdapter(String[] titles) {
            super(Arrays.asList(titles));
        }

        @Override
        public View getView(ViewGroup viewGroup) {
            View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.v_card, viewGroup, false);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getItem() + " : " + mNextPosition);
            return view;
        }
    }
}
