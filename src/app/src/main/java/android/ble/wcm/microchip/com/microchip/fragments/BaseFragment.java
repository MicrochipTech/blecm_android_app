package android.ble.wcm.microchip.com.microchip.fragments;

import android.support.v4.app.Fragment;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.ble.wcm.microchip.com.microchip.animations.ProgressBarAnimation;
import android.ble.wcm.microchip.com.microchip.animations.TextViewPotentiometerAnimation;

/**
 * Created by: WillowTree
 * Date: 11/20/14
 * Time: 11:16 AM.
 */
public class BaseFragment extends Fragment {

    /**
     * Animate potentiometer progressbar and numerical values
     *
     * @param potentiometer     - ProgressBar to animate
     * @param potentiometerText - TextView to animate
     * @param fromValue         - Value from 0 to 1023
     * @param toValue           - Value from 0 to 1023
     */
    public void setPotentiometerProgress(ProgressBar potentiometer, TextView potentiometerText, float fromValue, float toValue, int animationDuration){
        animatePotentiometerToValue(
                potentiometer,
                potentiometerText,
                fromValue,
                toValue,
                animationDuration,
                null);
    }

    /**
     * Animate ProgressBar and TextView from float value to another float value
     *
     * @param p         ProgressBar to animate
     * @param t         TextView to animate
     * @param from      Value from 0 to 1023
     * @param to        Value from 0 to 1023
     * @param duration  Animation duration in milliseconds
     * @param l         Animation Listener
     */
    private void animatePotentiometerToValue(ProgressBar p, TextView t, float from, float to, int duration, Animation.AnimationListener l){

        // ProgressBar Animation
        float fromPercentage = from / 1023 * 100;
        float toPercentage = to / 1023 * 100;
        ProgressBarAnimation progressBarAnimation = new ProgressBarAnimation(p, fromPercentage, toPercentage);
        progressBarAnimation.setDuration(duration);
        progressBarAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        if(l != null)
            progressBarAnimation.setAnimationListener(l);

        // TextView Animation
        TextViewPotentiometerAnimation textViewPotentiometerAnimation = new TextViewPotentiometerAnimation(t, from, to);
        textViewPotentiometerAnimation.setDuration(duration);
        textViewPotentiometerAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        // Start the animations
        p.startAnimation(progressBarAnimation);
        t.startAnimation(textViewPotentiometerAnimation);
    }

}
