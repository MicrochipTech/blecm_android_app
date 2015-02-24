package android.ble.wcm.microchip.com.microchip.adapters;

import android.ble.wcm.microchip.com.microchip.view.BluetoothListItem;
import android.ble.wcm.microchip.com.microchip.view.SettingsBleConnectionHeader;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: WillowTree
 * Date: 1/7/15
 * Time: 5:30 PM
 */
public class BleListAdapter extends BaseAdapter {

    public static final int NO_SELECTION = -1;

    Context context;
    public List<BluetoothDevice> items;
    public int selected = NO_SELECTION;

    SettingsBleConnectionHeader settingsBleConnectionHeader;

    public BleListAdapter(Context context){
        this.context = context;
        items = new ArrayList<BluetoothDevice>();
    }

    public void addDevice(BluetoothDevice blu){
        if(!items.contains(blu)){
            items.add(blu);
        }else{
            int index = items.indexOf(blu);
            items.remove(index);
            items.add(index, blu);
        }
        notifyDataSetChanged();
    }

    public void addSelectedDevice(BluetoothDevice blu){
        if(!items.contains(blu)){
            items.add(blu);
            selected = items.size() - 1;
        }else{
            selected = items.indexOf(blu);
        }
        notifyDataSetChanged();
    }

    public void setSettingsBleConnectionHeader(SettingsBleConnectionHeader view){
        this.settingsBleConnectionHeader = view;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice device = items.get(position);

        BluetoothListItem listItem = convertView == null
                ? new BluetoothListItem(context)
                : (BluetoothListItem) convertView;

        listItem.setText(device);
        listItem.setSelected(position == selected);
        listItem.setTag(position);

        return listItem;
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
        if(settingsBleConnectionHeader != null) {
            if(items == null || items.size() < 1){
                settingsBleConnectionHeader.showEmptyView();
            }else{
                settingsBleConnectionHeader.hideEmptyView();
            }
        }
    }

    public void setSelectedIndex(int position) {
        selected = position;
        notifyDataSetChanged();
    }

    public void clear(){
        selected = NO_SELECTION;
        clearDevicesNotConnected();
    }

    public void clearDevicesNotConnected() {
        BluetoothDevice device = null;
        if(selected != -1){
            device = items.get(selected);
        }

        items.clear();

        if(device != null){
            items.add(device);
            selected = 0;
        }

        notifyDataSetChanged();
    }
}
