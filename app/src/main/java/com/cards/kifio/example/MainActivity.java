package com.cards.kifio.example;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cards.kifio.swipeablecards.CardsView;
import com.cards.kifio.swipeablecards.ContentAdapter;

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
        data.add("Helsinki");
        data.add("Paris");
        data.add("Berlin");
        data.add("Tokyo");
        data.add("Washington");
        data.add("Moscow");
        data.add("London");
        data.add("Helsinki");
        data.add("Paris");
        data.add("Berlin");
        data.add("Tokyo");
        data.add("Washington");
        data.add("Moscow");
        data.add("London");
        data.add("Helsinki");
        data.add("Paris");
        data.add("Berlin");
        data.add("Tokyo");
        data.add("Washington");

        ContentAdapter<String> adapter = new Adapter(this, data);
        try {
            cardsView.setAdapter(adapter);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
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
                Log.d("Test", "inflate new card");
                view = LayoutInflater.from(mContext).inflate(R.layout.v_card, viewGroup, false);
                view.setTag(new Holder(view));
            }
            return super.getView(i, view, viewGroup);
        }

        @Override
        public void initHolder(View view, int pos) {
            Holder viewHolder = (Holder) view.getTag();
            viewHolder.init(getItem(pos));
        }

        @Override
        public void destroyView(View view) {
            Holder viewHolder = (Holder) view.getTag();
            view.setTag(null);
            viewHolder.destroy();
        }
    }

    static class Holder implements ContentAdapter.ViewHolder<String> {

        ImageView image;
        TextView title;
        TextView subtitle;

        public Holder(View view) {
            image = (ImageView) view.findViewById(R.id.image);
            title = (TextView) view.findViewById(R.id.title);
            subtitle = (TextView) view.findViewById(R.id.subtitle);
        }

        @Override
        public void init(String element) {
            title.setText(element);
            image.setImageResource(R.drawable.otradnoe);
        }

        @Override
        public void destroy() {
            image = null;
            title = null;
            subtitle = null;
        }
    }
}
