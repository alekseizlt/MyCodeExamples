package ru.alexprogs.dressroom;

import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
* Класс для управления анимацией элементов
*/
public class AnimationCustom {

    //==============================================================================================
    // Метод для создания анимации перемещения элемента
    public static TranslateAnimation getTranslateAnimation(float fromX, float toX, float fromY, float toY, long duration) {
        TranslateAnimation translateAnimation = new TranslateAnimation(fromX, toX, fromY, toY);
        translateAnimation.setDuration(duration);
        translateAnimation.setFillAfter(true);

        return translateAnimation;
    }

    //==============================================================================================
    // Метод для создания анимации масштабирования элемента
    public static ScaleAnimation getScaleAnimation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY, long duration) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, pivotX, pivotY);
        scaleAnimation.setDuration(duration);
        scaleAnimation.setFillAfter(true);

        return scaleAnimation;
    }

    //==============================================================================================
    // Метод для создания alpha-анимации
    public static AlphaAnimation getAlphaAnimation(float fromAlpha, float toAlpha, long duration) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);

        return alphaAnimation;
    }
}
