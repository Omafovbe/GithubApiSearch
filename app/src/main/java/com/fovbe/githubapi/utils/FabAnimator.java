package com.fovbe.githubapi.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.fovbe.githubapi.R;

/**
 * Created by OWNER1 on 7/19/2017.
 */

public class FabAnimator {
    /**
     *
     * @param context
     * @param view
     */
    public static void  fabClose(Context context, View view){
        runAnimation(context, view, R.anim.fab_menu_close);
    }
    public static void  fabOpen(Context context, View view){
        runAnimation(context, view, R.anim.fab_menu_open);
    }
    public static void  fabRotateBack(Context context, View view){
        runAnimation(context, view, R.anim.rotate_backward);
    }
    public static void slide_down(Context context, View view){
        runAnimation(context, view, R.anim.slide_down);
    }

    public static void loadingRotate(Context context, View view){
        runAnimation(context, view, R.anim.rotatate_infinite);
    }

    /**
     * Rotate the fab forward to open the menu
     * @param context
     * @param view
     */
    public static void  fabRotateForward(Context context, View view){
        runAnimation(context, view, R.anim.rotate_forward);
    }

    /**
     * Runs the animationson the view
     * @param context
     * @param view
     * @param animationId
     */
    private static void runAnimation(Context context, View view, int animationId){
        view.startAnimation(AnimationUtils.loadAnimation(context, animationId));
    }
}
