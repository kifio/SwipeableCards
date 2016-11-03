package com.cards.kifio.example;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cards.kifio.swipeablecards.OnTouchCardListener;
import com.cards.kifio.swipeablecards.SwipeableCard;

/**
 * Created by kifio on 11/4/16.
 */

public class PhotoCard extends SwipeableCard implements View.OnClickListener {

    public PhotoCard(Context context) {
        super(context);
    }

    public PhotoCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnTouchCardListener(OnTouchCardListener listener) {
        super.setOnTouchCardListener(listener);

        TextView textView = (TextView) findViewById(R.id.text);
        ImageView imageView = (ImageView) findViewById(R.id.image);

        textView.setOnTouchListener(listener);
        imageView.setOnTouchListener(listener);

        textView.setOnClickListener(this);
        imageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.text) {
            Toast.makeText(getContext(), "Click on text", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.image) {
            Toast.makeText(getContext(), "Click on image", Toast.LENGTH_SHORT).show();
        }
    }
}
