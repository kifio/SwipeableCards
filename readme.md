**Swipeable Cards**

* Simple library with cards, which you can swipe to the left or to the right.
* Easy interaction with Scrollview.
* Animations for resizing cards.

![](https://i.imgur.com/KEv6lSb.gif)

**QuickStart**

Add dependency in your build.gradle

```groovy
dependencies {
    compile 'com.github.kifio:swipeablecards:0.3'
}
```

For start using swipeable cards, add CardsView to your layout:

```xml
<LinearLayout
          android:orientation="vertical"
          android:layout_width="match_parent"
          android:layout_height="wrap_content">
  
          <com.cards.kifio.swipeablecards.CardsView
              android:id="@+id/cardsView"
              android:layout_width="match_parent"
              android:layout_height="match_parent" />
              
 </LinearLayout>
```

Do not set visible views count less than 3. In case with 1 or 2 visible cards, all cards will be initialized with wrong items.
It's known bug, and it will be fixed in next pre-release.

You must extend SwipeableCard, for creating cards with your own content.

```java
public class SampleCard extends SwipeableCard implements View.OnClickListener {

    public SampleCard(Context context) {
        super(context);
    }

    public SampleCard(Context context, AttributeSet attrs) {
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
```

I recommended set instance of OnTouchCardListener to large views in your card for handling swipe events and click events. 
In this listener, when click event occurs, performClick() method of this views calls. 

Also, you must extends ContentAdapter, for initialization cards with data.

```java
static class Adapter extends ContentAdapter<String> {

        private static final int COUNT = 5;

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
```

If in your view hierarchy ScrollView or NestedScrollView exist, 
i recommended call setScrollableParent(ViewParent viewParent) method and pass ref to this view as argument.  
It's allow you to avoid scroll events when user swipe cards.

```java
        CardsView cardsView = (CardsView) findViewById(R.id.cardsView);
        cardsView.setAdapter(new Adapter());
        cardsView.setScrollableParent((ViewParent) findViewById(R.id.root));
        cardsView.reload();
```

That's it.

**Attributes of CardsView**

* visibleViewsCount - count of visible cards. It's initialized in constructor.
* marginHorizontalStep - horizontal space between 2 neighboring cards. Default value - 8dp.
* marginVerticalStep - difference between y position of 2 neighboring cards. Default value - 8dp.

* marginLeft - default left margin of card.
* marginTop - default top margin of card.
* marginRight - default right margin of card.
* marginBottom - default bottom margin of card.

* margin - set all 4 previous args with one value.

* infinite - infinite getting cards from adapter.
* reverse - reverse order of initializing items from adapter.
* movable - draggable cards.

All attributes, excepting ```visibleViewsCount```, has setters and getters.





