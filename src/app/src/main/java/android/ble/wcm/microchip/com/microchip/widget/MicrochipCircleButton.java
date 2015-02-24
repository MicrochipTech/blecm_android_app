package android.ble.wcm.microchip.com.microchip.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import android.ble.wcm.microchip.com.microchip.R;

/**
 * Created by jossayjacobo on 11/5/14
 */
public class MicrochipCircleButton extends LinearLayout {
    public MicrochipCircleButton(Context context) {
        this(context, null);
    }

    public MicrochipCircleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MicrochipCircleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_circle_button, this, true);
    }

}
