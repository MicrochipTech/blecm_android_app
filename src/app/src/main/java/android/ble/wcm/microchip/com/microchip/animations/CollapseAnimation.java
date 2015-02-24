package android.ble.wcm.microchip.com.microchip.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by jossayjacobo on 11/7/14
 */
public class CollapseAnimation extends Animation {

    private View view;
    private int width;
    private int height;

    public CollapseAnimation(View view, int width, int height){
        this.view = view;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t){
        super.applyTransformation(interpolatedTime, t);
        float inverseInterpolatedTime = 1.0f - interpolatedTime;

        view.getLayoutParams().width = (int) (width * inverseInterpolatedTime);
        view.getLayoutParams().height = (int) (height * inverseInterpolatedTime);
        view.requestLayout();
    }
}
