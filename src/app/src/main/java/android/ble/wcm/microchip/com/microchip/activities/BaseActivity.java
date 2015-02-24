package android.ble.wcm.microchip.com.microchip.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.ble.wcm.microchip.com.microchip.R;
import android.ble.wcm.microchip.com.microchip.interfaces.SettingsListener;
import android.ble.wcm.microchip.com.microchip.animations.CollapseAnimation;
import android.ble.wcm.microchip.com.microchip.animations.ExpandAnimation;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by: WillowTree
 * Date: 11/18/14
 * Time: 3:39 PM.
 */
public abstract class BaseActivity extends ActionBarActivity implements SettingsListener {

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    private static final int SETTINGS_ANIMATION_DURATION = 250;

    public void revealSquareAnimation(View view, int width, int height) {
        ExpandAnimation expandAnimation = new ExpandAnimation(view, width, height);
        expandAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        expandAnimation.setDuration(SETTINGS_ANIMATION_DURATION);
        expandAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onSettingsOpened();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.getLayoutParams().height = 0;
        view.getLayoutParams().height = 0;
        view.requestLayout();
        view.setVisibility(View.VISIBLE);
        view.startAnimation(expandAnimation);
    }

    public void concealSquareAnimation(final View view, int width, int height) {
        CollapseAnimation collapseAnimation = new CollapseAnimation(view, width, height);
        collapseAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        collapseAnimation.setDuration(SETTINGS_ANIMATION_DURATION);
        collapseAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                onSettingsClosed();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(collapseAnimation);
        hideSoftKeyboard(view);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void revealCircleAnimation(View view, int width, int height) {
        // Get Settings Menu item
        View settings = findViewById(R.id.menu_settings);
        // get the center for the starting position
        int[] position = new int[2];
        settings.getLocationOnScreen(position);

        int cx = position[0] + (settings.getLeft() + settings.getRight()) / 2;
        int cy = position[1] + (settings.getTop() + settings.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(width, height);

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();

        onSettingsOpened();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void concealCircleAnimation(final View view, int width, int height){
        // Get Settings Menu item
        View settings = findViewById(R.id.menu_settings);
        // get the center for the starting position
        int[] position = new int[2];
        settings.getLocationOnScreen(position);

        int cx = position[0] + (settings.getLeft() + settings.getRight()) / 2;
        int cy = position[1] + (settings.getTop() + settings.getBottom()) / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, height, 0);

        // make the view gone when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }
        });
        anim.start();
        hideSoftKeyboard(view);

        onSettingsClosed();
    }

    public void hideSoftKeyboard(View view){
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
