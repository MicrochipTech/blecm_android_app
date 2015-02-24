package android.ble.wcm.microchip.com.microchip.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by jossayjacobo on 11/7/14
 */
public class ExpandAnimation extends Animation {

    private View view;
    private int width;
    private int height;

    public ExpandAnimation(View view, int width, int height){
        this.view = view;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t){
        super.applyTransformation(interpolatedTime, t);
        view.getLayoutParams().width = (int) (width * interpolatedTime);
        view.getLayoutParams().height = (int) (height * interpolatedTime);
        view.requestLayout();
    }
}
