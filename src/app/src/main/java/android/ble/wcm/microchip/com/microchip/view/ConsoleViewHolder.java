package android.ble.wcm.microchip.com.microchip.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by jossayjacobo on 11/6/14
 */
public class ConsoleViewHolder extends RecyclerView.ViewHolder {

    public ConsoleItem consoleItem;

    public ConsoleViewHolder(View itemView, ConsoleItem.Listener listener) {
        super(itemView);
        consoleItem = (ConsoleItem) itemView;
        consoleItem.setListener(listener);
    }
}
