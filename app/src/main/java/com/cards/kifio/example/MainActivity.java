package com.cards.kifio.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        for (int i = 2; i < 5; i++) {

            CardsView cardsView = new CardsView(this, i);
            content.addView(cardsView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) cardsView.getLayoutParams();
            lp.gravity = Gravity.CENTER;
            content.requestLayout();

            if (i == 2) {
                cardsView.setInfinite(true);
            } else if (i == 3) {
                cardsView.setMovable(true);
            } else {
                cardsView.setInfinite(true);
                cardsView.setMovable(true);
            }

            cardsView.setYPositionDiff(i * 8);
            cardsView.setHorizontalSpaceMargin(i);

            cardsView.setAdapter(new CardsAdapter(getResources().getStringArray(R.array.numbers)));
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

        CardsAdapter(String[] numbers) {
            super(Arrays.asList(numbers));
        }

        @Override
        public View getView(ViewGroup viewGroup) {

            View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.v_card, viewGroup, false);

            final String titleText = getNextItem();

            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(titleText);

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Card: " + titleText, Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
    }
}
