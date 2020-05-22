package com.nialls.app;

import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.RelativeLayout;

public class Animations {

    // Animation for clearing info box off screen on app start
    public static void initialOffScreen(View view) {
        view.setTranslationX(-1000);
    }

    // Animation for clearing info box off screen when user taps icon
    public static void offScreen(View view) {
        ObjectAnimator offScreenAnimation = ObjectAnimator.ofFloat(view, "translationX", -1000);
        offScreenAnimation.setDuration(250);
        offScreenAnimation.start();
    }

    // Animation for putting info box on screen when user taps icon
    public static void onScreen(View view) {
        ObjectAnimator onScreenAnimation = ObjectAnimator.ofFloat(view, "translationX", 10);
        onScreenAnimation.setDuration(250);
        onScreenAnimation.start();
    }

    // For expanding info box when tapped
    public static void expandBox(View view){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.height = (view.getHeight() * 2);
        params.width = (int) (view.getWidth() * 1.25);
        view.setLayoutParams(params);
    }

    // For shrinking info box when tapped
    public static void collapseBox(View view){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.height = (int) (view.getHeight() * 0.5);
        params.width = (int) (view.getWidth() * 0.8);
        view.setLayoutParams(params);
    }


}
