**Swipeable Cards**

Библиотека с реализацией стэка перемещаемых объектов CardView в RelativeLayout.
Для использования необходимо добавить в иерархию View объектов CardsLayout и передать ему реализацию класса CardsAdapter. 

В методе  ```getView(ViewGroup viewGroup, int margin)``` необходимо указать позиционирование карточек, 
например 

```
    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
    lp.setMargins(margin, 0, margin, 0);
    lp.addRule(RelativeLayout.ALIGN_TOP, R.id.view);
    view.setLayoutParams(lp);
```

Интерфесы 



