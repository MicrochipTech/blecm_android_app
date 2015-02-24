package android.ble.wcm.microchip.com.microchip.view;

import android.ble.wcm.microchip.com.microchip.DataStore;
import android.ble.wcm.microchip.com.microchip.R;
import android.ble.wcm.microchip.com.microchip.utils.InputValidator;
import android.ble.wcm.microchip.com.microchip.widget.MicrochipSwitch;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iangclifton.android.floatlabel.FloatLabel;

/**
 * Created by: WillowTree
 * Date: 1/7/15
 * Time: 5:02 PM
 */
public class SettingsHeaderView extends LinearLayout implements View.OnKeyListener, TextWatcher, CompoundButton.OnCheckedChangeListener {

    Context context;
    FloatLabel serverAddress;
    MicrochipSwitch sslErrorSwitch;

    public SettingsHeaderView(Context context) {
        this(context, null);
    }

    public SettingsHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_header_settings, this, true);

        serverAddress = (FloatLabel) findViewById(R.id.settings_server_address);
        sslErrorSwitch = (MicrochipSwitch) findViewById(R.id.ignore_error_switch);
        sslErrorSwitch.setChecked(DataStore.getIgnoreSllErrors(context));
        sslErrorSwitch.setOnCheckedChangeListener(this);

        // Server Address text change listener and validation
        serverAddress.getEditText().addTextChangedListener(this);
        serverAddress.getEditText().setOnKeyListener(this);
        serverAddress.getEditText().setText(DataStore.getAwsAmi(context));

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_ENTER:
                if (!InputValidator.IsUrl(((TextView) v).getText().toString())) {
                    serverAddress.getEditText().setError(context.getResources().getString(R.string.settings_server_address_no_valid));
                    return true;
                } else {
                    serverAddress.getEditText().setError(null);
                    return false;
                }
            default:
                return false;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String url = String.valueOf(s);
        DataStore.persistAwsAmi(context, url);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        DataStore.persistIgnoreSllErrors(context, isChecked);
    }
}
