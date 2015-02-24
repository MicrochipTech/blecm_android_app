package android.ble.wcm.microchip.com.microchip.view;

import android.ble.wcm.microchip.com.microchip.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by: WillowTree
 * Date: 1/7/15
 * Time: 5:17 PM
 */
public class SettingsFooterAttributionView extends LinearLayout implements View.OnClickListener {

    Context context;
    ImageView wtaAttribution;

    public SettingsFooterAttributionView(Context context) {
        this(context, null);
    }

    public SettingsFooterAttributionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsFooterAttributionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_footer_settings_attribution, this, true);
        wtaAttribution = (ImageView) findViewById(R.id.settings_wta_attribution);
        wtaAttribution.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.wta_attribution_url)));
        context.startActivity(i);
    }
}
