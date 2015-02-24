package android.ble.wcm.microchip.com.microchip.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.ble.wcm.microchip.com.microchip.R;

/**
 * Created by jossayjacobo on 11/5/14
 */
public class MicrochipCircleStatus extends LinearLayout {

    ImageView circle;

    public MicrochipCircleStatus(Context context) {
        this(context, null);
    }

    public MicrochipCircleStatus(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MicrochipCircleStatus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_circle_status, this, true);
        circle = (ImageView) findViewById(R.id.circle);
    }

    public void setStatusOff(){
        circle.setBackgroundResource(R.drawable.switch_handle_off);
    }

    public void setStatusGreen(){
        circle.setBackgroundResource(R.drawable.circle_green);
    }

    public void setStatusRed(){
        circle.setBackgroundResource(R.drawable.circle_red);
    }

    public void setStatusOrange(){
        circle.setBackgroundResource(R.drawable.circle_orange);
    }
}
