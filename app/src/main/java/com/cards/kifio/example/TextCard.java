package com.cards.kifio.example;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cards.kifio.swipeablecards.OnTouchCardListener;
import com.cards.kifio.swipeablecards.SwipeableCard;

/**
 * Created by kifio on 11/4/16.
 */

public class TextCard extends SwipeableCard implements View.OnClickListener {

    public TextCard(Context context) {
        super(context);
    }

    public TextCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnTouchCardListener(OnTouchCardListener listener) {
        super.setOnTouchCardListener(listener);

        Button button = (Button) findViewById(R.id.button);

        // For handling swipe actions.
        button.setOnTouchListener(listener);

        // For handling clicks.
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            Toast.makeText(getContext(), R.string.button_click_action, Toast.LENGTH_SHORT).show();
        }
    }
}
