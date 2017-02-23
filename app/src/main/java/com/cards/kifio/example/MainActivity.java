package com.cards.kifio.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        LinearLayout content = (LinearLayout) findViewById(R.id.content);

        for (int i = 0; i <= 10; i++) {

            CardsView cardsView = new CardsView(this, i);
            content.addView(cardsView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) cardsView.getLayoutParams();
            lp.gravity = Gravity.CENTER;
            content.requestLayout();

            if (i % 2 == 0 && i % 3 != 0) {
                cardsView.setInfinite(true);
            } else if (i % 3 == 0){
                cardsView.setMovable(true);
            } else {
                cardsView.setInfinite(true);
                cardsView.setMovable(true);
            }

            cardsView.setYPositionDiff(i * 20);
            cardsView.setHorizontalSpaceMargin(i);

            cardsView.setAdapter(new CardsAdapter(getResources().getStringArray(R.array.cards_titles)));
            cardsView.setOnCardSwipeListener(this);
            cardsView.setScrollableParent(findViewById(R.id.scroll_view));
            cardsView.reload();
        }

    }

    @Override
    public void onSwipeCard(int direction, int count) {
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
