**Swipeable Cards**

* Simple library with cards, which you can swipe to the left or to the right.
* Easy interaction with Scrollview.
* Animations for resizing cards.

![](https://i.imgur.com/KEv6lSb.gif)

**QuickStart**

Add dependency in your build.gradle

```groovy
dependencies {
    compile 'com.github.kifio:swipeablecards:1.0@aar'
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

You must extends ContentAdapter, for initialization cards with data.

```java
class CardsAdapter extends ContentAdapter<String> {

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
```

If in your view hierarchy ScrollView or NestedScrollView exist, 
i recommended call setScrollableParent(View scrollView) method and pass ref to this view as argument.
It's allow you to avoid scroll events when user try to swipe cards.

```java
    CardsView cardsView = (CardsView) findViewById(R.id.cardsView);
    cardsView.setAdapter(new Adapter());
    cardsView.setScrollableParent(findViewById(R.id.root));
    cardsView.reload();
```

That's it.

**Attributes of CardsView**

* visibleViewsCount - count of visible cards. It's initialized in constructor.
* horizontalSpaceMargin - horizontal space between 2 neighboring cards. Default value - 8dp.
* yPositionDiff - difference between y position of 2 neighboring cards. Default value - 8dp.
* infinite - infinite getting cards from adapter.
* movable - draggable cards.

All attributes, excepting ```visibleViewsCount```, has setters and getters.





