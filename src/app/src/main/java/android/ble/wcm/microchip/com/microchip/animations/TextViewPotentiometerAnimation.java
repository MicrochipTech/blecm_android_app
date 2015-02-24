package android.ble.wcm.microchip.com.microchip.animations;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

/**
 * Created by jossayjacobo on 11/6/14
 */
public class TextViewPotentiometerAnimation extends Animation {

    private TextView potentiometerText;
    private float from;
    private float to;

    public TextViewPotentiometerAnimation(TextView t, float from, float to){
        this.potentiometerText = t;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t){
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        potentiometerText.setText(String.valueOf((int) value));
    }
}