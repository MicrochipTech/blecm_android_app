package android.ble.wcm.microchip.com.microchip;

import android.app.Application;
import android.ble.wcm.microchip.com.microchip.ble.BluetoothCrashResolver;

/**
 * Created by: WillowTree
 * Date: 11/20/14
 * Time: 4:26 PM.
 */
public class MainApp extends Application {

    private BluetoothCrashResolver bluetoothCrashResolver = null;

    @Override
    public void onCreate(){
        super.onCreate();

        bluetoothCrashResolver = new BluetoothCrashResolver(this);
        bluetoothCrashResolver.start();
    }

    public BluetoothCrashResolver getBluetoothCrashResolver(){
        return bluetoothCrashResolver;
    }

}
