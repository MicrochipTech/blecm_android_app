package android.ble.wcm.microchip.com.microchip.view;

import android.ble.wcm.microchip.com.microchip.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by: WillowTree
 * Date: 1/9/15
 * Time: 11:29 AM
 */
public class SettingsBleConnectionHeader extends LinearLayout {

    ProgressBar progressBar;
    TextView progressText;
    TextView emptyView;

    public SettingsBleConnectionHeader(Context context) {
        this(context, null);
    }

    public SettingsBleConnectionHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsBleConnectionHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_header_settings_bluetooth_connections, this, true);
        progressBar = (ProgressBar) findViewById(R.id.settings_bluetooth_progress_bar);
        progressText = (TextView) findViewById(R.id.settings_bluetooth_progress_text);
        emptyView = (TextView) findViewById(R.id.settings_bluetooth_empty_view);
    }

    public void setStatusScanning(){
        progressBar.setVisibility(VISIBLE);
        progressText.setVisibility(VISIBLE);
        progressText.setText(getResources().getText(R.string.settings_scanning));
    }

    public void setStatusConnecting(){
        progressBar.setVisibility(VISIBLE);
        progressText.setVisibility(VISIBLE);
        progressText.setText(getResources().getText(R.string.settings_connecting));
    }

    public void setStatusGone(){
        progressBar.setVisibility(GONE);
        progressText.setVisibility(GONE);
        progressText.setText("");
    }

    public void showEmptyView(){
        emptyView.setVisibility(VISIBLE);
    }

    public void hideEmptyView(){
        emptyView.setVisibility(GONE);
    }

}
