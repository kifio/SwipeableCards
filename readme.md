**Swipeable Cards**

Библиотека с реализацией очереди перемещаемых объектов CardView в RelativeLayout.
Для использования необходимо добавить в иерархию View объектов CardsLayout и передать ему реализацию класса CardsAdapter. 

В методе  ```getView(ViewGroup viewGroup, int margin)``` необходимо реализовать позиционирование карточек, 
как если бы в иерархию добавлялась самая первая для пользователя карточка стека: 

```
    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
    lp.setMargins(margin, 0, margin, 0);
    lp.addRule(RelativeLayout.ALIGN_TOP, R.id.view);
    view.setLayoutParams(lp);
```

Добавление вертикального смещения и увеличенных отступов для всех карточек будет сделано объектом класса CardsLayout. В этом же классе реализованы анимации смахивания и возвращения арточки в начало очереди, а также добавление карточек в конец очереди.

Метод
```
    setScrollableParent(ViewParent scrollableParent);
```
должен применяться в случае, если CardsLayout вложен в ScrollView или NestedScrollView, для перехвата жестов.


Метод 

```
    setCardsPositionObserver(CardsPositionObserver observer);
```
должен применяться в случае, если карточки должны позиционироваться относительно какого-то объекта View (практически всегда) для того, чтобы передать в CardsLayout ссылку на объект, который возврщает информацию о позиции этого объекта View.


Метод

```
    setOnSwipeCardListener(OnSwipeCardListener listener);
```
должен применяться в случае, если необходимо отслеживать смахивание карточек. Количество смахнутых карточек можно отслеживать вручную.

Актуальная версия библиотеки не размещена в Bintray, наиболее удобным способом внедрения её в свой проект будет форк.
    



