package android.ble.wcm.microchip.com.microchip.view;

import android.ble.wcm.microchip.com.microchip.R;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by: WillowTree
 * Date: 1/9/15
 * Time: 12:07 PM
 */
public class BluetoothListItem extends LinearLayout {

    TextView text;
    RadioButton radioButton;

    public BluetoothListItem(Context context) {
        this(context, null);
    }

    public BluetoothListItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BluetoothListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_list_item_bluetooth, this, true);
        text = (TextView) findViewById(R.id.list_item_bluetooth_text);
        radioButton = (RadioButton) findViewById(R.id.list_item_bluetooth_radio);
    }

    public void setText(BluetoothDevice device){
        this.text.setText(device.getName());
    }

    public void setSelected(boolean selected){
        radioButton.setChecked(selected);
    }

}
