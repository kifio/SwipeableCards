package com.cards.kifio.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cards.kifio.swipeablecards.CardsView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        CardsView cardsView = (CardsView) findViewById(R.id.cardsView);
        ContentAdapter<String> adapter = new ContentAdapter<>(new String[] {"Moscow", "London", "Helsinki", "Paris", "Berlin", "London"});
        cardsView.setAdapter(adapter);
        cardsView.init();
    }
}
