package android.ble.wcm.microchip.com.microchip.widget;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import android.ble.wcm.microchip.com.microchip.R;

/**
 * Created by jossayjacobo on 11/5/14
 */
public class MicrochipSwitch extends LinearLayout {

    SwitchCompat switchCompat;

    public MicrochipSwitch(Context context) {
        this(context, null);
    }

    public MicrochipSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MicrochipSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_switch, this, true);
        switchCompat = (SwitchCompat) findViewById(R.id.switchCompat);
    }

    public boolean isChecked(){
        return switchCompat.isChecked();
    }

    public void setChecked(boolean checked){
        switchCompat.setChecked(checked);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        switchCompat.setOnCheckedChangeListener(onCheckedChangeListener);
    }
}
