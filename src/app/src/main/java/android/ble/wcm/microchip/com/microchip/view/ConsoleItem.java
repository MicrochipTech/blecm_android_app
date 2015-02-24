package android.ble.wcm.microchip.com.microchip.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.ble.wcm.microchip.com.microchip.R;
import android.ble.wcm.microchip.com.microchip.model.ResponseModel;
import android.ble.wcm.microchip.com.microchip.utils.JsonPrettyPrint;
import android.ble.wcm.microchip.com.microchip.widget.MicrochipCircleStatus;

/**
 * Created by jossayjacobo on 11/5/14
 */
public class ConsoleItem extends LinearLayout implements View.OnClickListener, View.OnLongClickListener {

    private Listener listener;
    private ResponseModel response;

    MicrochipCircleStatus status;
    TextView title;
    TextView text;

    public ConsoleItem(Context context) {
        this(context, null);
    }

    public ConsoleItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConsoleItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_console_item, this, true);
        status = (MicrochipCircleStatus) findViewById(R.id.status);
        title = (TextView) findViewById(R.id.title);
        text = (TextView) findViewById(R.id.text);

        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public void setContent(ResponseModel response, boolean selected){
        this.response = response;

        title.setText(response.methodType + ": " + response.url);
        setStatusCodeAndResponse(response);

        setSelected(selected);
        setActivated(selected);
    }

    private void setStatusCodeAndResponse(ResponseModel response){
        switch (response.statusCode){
            case ResponseModel.OK:
                status.setStatusGreen();
                text.setText(JsonPrettyPrint.convert(response.responseStatus));
                break;

            case ResponseModel.BAD_REQUEST:
            case ResponseModel.INTERNAL_ERROR:
                status.setStatusRed();
                text.setText(JsonPrettyPrint.convert(response.responseError));
                break;

            default:
                status.setStatusOrange();
                text.setText(response.responseError);
        }
    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onItemClicked(response);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(listener != null)
            listener.onItemLongPressed(response);
        return true;
    }

    public interface Listener{
        public void onItemClicked(ResponseModel responseModel);
        public void onItemLongPressed(ResponseModel responseModel);
    }
}
