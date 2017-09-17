package com.cards.kifio.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cards.kifio.CardsLayout;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements
        CardsLayout.OnSwipeCardListener,
        CardsLayout.CardsPositionObserver {

    private CardsLayout mCardsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        CardsAdapter adapter = new CardsAdapter();
        mCardsLayout = findViewById(R.id.cardsLayout);
        mCardsLayout.setAdapter(adapter);
        mCardsLayout.setCardsPositionObserver(this);
        mCardsLayout.setScrollableParent(mCardsLayout.getParent());
        mCardsLayout.setOnSwipeCardListener(this);
        adapter.update(Arrays.asList("First", "Second", "Third"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCardsLayout.dispose();
    }

    @Override
    public void onSwipeCard(int direction) {

    }

    @Override
    public int getCardsTop() {
        return findViewById(R.id.view).getTop();
    }

    private static class CardsAdapter extends com.cards.kifio.CardsAdapter<String> {

        @Override
        public CardView getView(ViewGroup viewGroup, int margin) {
            if (mCurrent == getCount()) mCurrent = 0;
            final String titleText = mData.get(mCurrent);
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            CardView view = (CardView) inflater.inflate(R.layout.v_card, viewGroup, false);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(titleText);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
            lp.setMargins(margin, 0, margin, 0);
            lp.addRule(RelativeLayout.ALIGN_TOP, R.id.view);
            view.setLayoutParams(lp);
            title.setOnClickListener(v -> {
                String text = "Card: " + titleText;
                Toast.makeText(v.getContext(), text, Toast.LENGTH_SHORT).show();
            });
            mCurrent++;
            return view;
        }
    }
}
