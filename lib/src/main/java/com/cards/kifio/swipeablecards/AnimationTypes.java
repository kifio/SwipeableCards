package com.cards.kifio.swipeablecards;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by kifio on 2/7/17.
 */

public class AnimationTypes {

    /**
     * Swipe card to the left.
     */
    public static final int LEFT_SWIPE = 0;

    /**
     * Swipe card to the right.
     */
    public static final int RIGHT_SWIPE = 1;

    /**
     * Move card to initial position in movable mode.
     */
    static final int MOVE_TO_INITIAL = 2;

    /**
     *
     */
    static final int NO_ANIMATION = 3;

    @IntDef({LEFT_SWIPE, RIGHT_SWIPE, MOVE_TO_INITIAL, NO_ANIMATION})

    @Retention(RetentionPolicy.SOURCE)
    @interface AnimationType {}
}