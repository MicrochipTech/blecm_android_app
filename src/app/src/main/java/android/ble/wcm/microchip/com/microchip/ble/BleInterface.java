package android.ble.wcm.microchip.com.microchip.ble;

import android.bluetooth.BluetoothDevice;

/**
 * Created by: WillowTree
 * Date: 1/12/15
 * Time: 1:55 PM
 */
public interface BleInterface {

    public void onBleDisabled();

    public void onBleScan(BluetoothDevice device);

    public void onBleScanFailed(String message);

    public void onScanningStarted();

    public void onScanningStopped();

    public void onConnectingToDevice();

}
