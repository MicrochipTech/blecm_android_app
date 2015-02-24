package android.ble.wcm.microchip.com.microchip.ble;

import android.annotation.TargetApi;
import android.app.Service;
import android.ble.wcm.microchip.com.microchip.DataStore;
import android.ble.wcm.microchip.com.microchip.MainApp;
import android.ble.wcm.microchip.com.microchip.api.Api;
import android.ble.wcm.microchip.com.microchip.api.model.Status.Status;
import android.ble.wcm.microchip.com.microchip.model.ResponseModel;
import android.ble.wcm.microchip.com.microchip.provider.response.ResponseProvider;
import android.ble.wcm.microchip.com.microchip.utils.BleUtils;
import android.ble.wcm.microchip.com.microchip.utils.GGson;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * Created by: WillowTree
 * Date: 1/12/15
 * Time: 11:32 AM
 */
public class BleService extends Service {
    private final static String TAG = BleService.class.getSimpleName();

    public static final String BROADCAST = "BleService.broadcast";
    public static final String ACTION_ID = "action_id";

    public static final int ACTION_GATT_CONNECTED = 0;
    public static final int ACTION_GATT_DISCONNECTED = 1;
    public static final int ACTION_GATT_DISCOVER_SERVICES = 2;
    public static final int ACTION_GATT_SERVICES_DISCOVERED = 3;
    public static final int ACTION_DATA_AVAILABLE = 4;
    public static final int ACTION_UPDATE_UI = 5;
    public static final int ACTION_UPDATE_UUID_UI = 6;

    private static final int SERVICE_DISCOVERY_DELAY = 1000;

    private UUID UUID_SERVICE = GattServiceAttributes.UUID_SERVICE;
    private UUID UUID_SERVICE_DEVICE_INFORMATION = GattServiceAttributes.UUID_SERVICE_DEVICE_INFORMATION;
    private UUID UUID_CHARACTERISTIC_GENERIC = GattServiceAttributes.UUID_CHARACTERITIC_GENERIC;
    private UUID UUID_CHARACTERISTIC_BUTTONS = GattServiceAttributes.UUID_CHARACTERISTIC_BUTTONS;
    private UUID UUID_CHARACTERISTIC_POTENTIOMETER = GattServiceAttributes.UUID_CHARACTERISTIC_POTENTIOMETER;
    private UUID UUID_CHARACTERISTIC_LEDS = GattServiceAttributes.UUID_CHARACTERISTIC_LEDS;
    private UUID UUID_CHARACTERISTIC_MODEL_NUMBER = GattServiceAttributes.UUID_CHARACTERISITC_MODEL_NUMBER;
    private UUID UUID_CHARACTERISTIC_SERIAL_NUMBER = GattServiceAttributes.UUID_CHARACTERISITC_SERIAL_NUMBER;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothScanner;
    private ScanCallback scanCallback;
    private MicrochipLeScanCallback leScanCallback;

    private String bluetoothDeviceAddress;
    private String modelNumber;
    private BluetoothGatt bluetoothGatt;

    private boolean isScanning = false;
    private boolean isInitialized = false;
    private boolean isConnected = false;

    private final IBinder iBinder = new MicrochipBinder();
    private BleInterface bleInterface;

    private LocalBroadcastManager broadcast;
    private BluetoothGattCharacteristic ledsCharacteristic;

    private final BluetoothGattCallback bleCallback = new MicrochipBluetoothGattCallback();
    private Queue<BluetoothGattDescriptor> descriptorQueue = new LinkedList<BluetoothGattDescriptor>();
    private Queue<BluetoothGattCharacteristic> readCharacteristicQueue = new LinkedList<BluetoothGattCharacteristic>();

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case ACTION_GATT_DISCOVER_SERVICES:
                    if(bluetoothGatt != null && isConnected())
                        bluetoothGatt.discoverServices();
                    break;
            }
        }
    };

    private int servicesRegisteredForNotification = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopBluetoothCrashResolver();
    }

    public class MicrochipBinder extends Binder{
        public BleService getService(){
            return BleService.this;
        }
    }

    public void initialize(BleInterface bleInterface){
        this.isInitialized = true;
        this.bleInterface = bleInterface;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        leScanCallback = new MicrochipLeScanCallback();
        broadcast = LocalBroadcastManager.getInstance(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanCallback = new MicrochipScanCallback();

            if(isBluetoothEnabled()){
                bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
            }else{
                bleInterface.onBleDisabled();
                this.isInitialized = false;
            }
        }
    }

    public boolean isDeviceBleCompatible(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public boolean isConnected(){
        return isConnected;
    }

    public boolean isScanning(){ return isScanning; }

    public BluetoothDevice getDevice(){
        if(bluetoothDeviceAddress != null && bluetoothAdapter != null){
            return bluetoothAdapter.getRemoteDevice(bluetoothDeviceAddress);
        }
        return null;
    }

    /**
     * Start Scanning
     * Make sure the BLE Crash Resolver is running before you start scanning
     */
    public void startScanning(){
        startBluetoothCrashResolver();
        if(!isScanning){
            if(isBluetoothEnabled()){
                isScanning = true;
                bleInterface.onScanningStarted();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    List<ScanFilter> filters = new ArrayList<>();
                    filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID_SERVICE)).build());

                    ScanSettings scanSettings = new ScanSettings.Builder()
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                            .setScanMode(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                            .build();
                    bluetoothScanner.startScan(filters, scanSettings, scanCallback);
                }else{
                    bluetoothAdapter.startLeScan(leScanCallback);
                }
            }else{
                bleInterface.onBleDisabled();
            }
        }
    }

    /**
     * Stop Scanning
     */
    public void stopScanning(){
        if(isScanning){
            if(isBluetoothEnabled()){
                isScanning = false;
                bleInterface.onScanningStopped();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    bluetoothScanner.stopScan(scanCallback);
                }else{
                    bluetoothAdapter.stopLeScan(leScanCallback);
                }
            }else{
                bleInterface.onBleDisabled();
                isInitialized = false;
            }
        }
    }

    /**
     * BLE Scan Callback for Jelly Bean and KitKat Devices
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private class MicrochipLeScanCallback implements BluetoothAdapter.LeScanCallback {

        /**
         * Callback interface used to deliver LE scan results.
         */
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            List<UUID> uuids = BleUtils.parseUuids(scanRecord);
            for(UUID u : uuids){
                if(u.equals(UUID_SERVICE)){
                    bleInterface.onBleScan(device);
                }
            }
        }
    }

    /**
     * BLE Scan Callback for Lollipop devices and higher.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class MicrochipScanCallback extends ScanCallback {

        /**
         * Callback when a BLE advertisement has been found.
         *
         * @param callbackType Determines how this callback was triggered. Currently could only be
         *            {@link ScanSettings#CALLBACK_TYPE_ALL_MATCHES}.
         * @param result A Bluetooth LE scan result.
         */
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            if(result.getScanRecord() != null && result.getScanRecord().getServiceUuids() != null){
                for(ParcelUuid parcelUuid : result.getScanRecord().getServiceUuids()){
                    if(parcelUuid.getUuid().equals(GattServiceAttributes.UUID_SERVICE)){
                        BluetoothDevice device = result.getDevice();
                        bleInterface.onBleScan(device);
                        break;
                    }
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        /**
         * Callback when scan could not be started.
         *
         * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
         */
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            bleInterface.onBleScanFailed(String.valueOf(errorCode));
        }
    }

    /**
     * Connect
     *
     * Attempt to connect (or reconnect) to device
     *
     * @param address MAC Address of device attempting to connect to.
     * @return true or false depending on whether or not connection started
     */
    public boolean connect(final String address) {
        if (bluetoothAdapter == null || address == null) {
            return false;
        }

        // Check to see if bluetooth is still enabled on device.
        if(isBluetoothEnabled()){

            // If already connected to another device disconnect before connecting to a new one.
            if(isConnected()){
                disconnect();
            }

            // If it's a previously connected device. Try to reconnect.
            if (bluetoothDeviceAddress != null && address.equals(bluetoothDeviceAddress) && bluetoothGatt != null) {
                return bluetoothGatt.connect();
            }

            final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

            bluetoothGatt = device.connectGatt(this, false, bleCallback);
            bleInterface.onConnectingToDevice();
            bluetoothDeviceAddress = address;

            return true;
        }else{
            bleInterface.onBleDisabled();
            return false;
        }
    }

    /**
     * Disconnect
     *
     * Attempt to disconnect to connected device
     */
    public void disconnect() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            return;
        }

        if(isConnected)
            bluetoothGatt.disconnect();

        isConnected = false;
        sendBroadcast(ACTION_GATT_DISCONNECTED);
    }

    /**
     * BLE Gatt Server Callback
     */
    private class MicrochipBluetoothGattCallback extends BluetoothGattCallback{

        /**
         * Callback indicating when Gatt client has connected/disconnected to/from a remote
         * Gatt server.
         *
         * @param gatt Gatt client
         * @param status Status of the connect or disconnect operation.
         *               {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
         * @param newState Returns the new connection state. Can be one of
         *                  {@link BluetoothProfile#STATE_DISCONNECTED} or
         *                  {@link BluetoothProfile#STATE_CONNECTED}
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState){
            switch (newState){
                case BluetoothProfile.STATE_CONNECTED:

                    // Persist Mac Address && Device Name
                    DataStore.persistBleDeviceMacAddress(BleService.this, gatt.getDevice().getAddress());
                    DataStore.persistBleDeviceName(BleService.this, gatt.getDevice().getName());

                    // Send delayed message to start discovering devices
                    Message m = new Message();
                    m.what = ACTION_GATT_DISCOVER_SERVICES;
                    handler.sendMessageDelayed(m, SERVICE_DISCOVERY_DELAY);

                    // Toggle and reset flags
                    isConnected = true;
                    servicesRegisteredForNotification = 0;

                    // Send Broadcast to update UI
                    sendBroadcast(ACTION_GATT_CONNECTED);
                    break;

                case BluetoothProfile.STATE_DISCONNECTED:

                    // Send Broadcast to update UI
                    sendBroadcast(ACTION_GATT_DISCONNECTED);

                    // Toggle and reset flags
                    isConnected = false;
                    modelNumber = null;
                    servicesRegisteredForNotification = 0;
                    break;
            }
        }

        /**
         * Callback invoked when the list of remote services, characteristics and descriptors
         * for the remote device have been updated, ie new services have been discovered.
         *
         * Queue up characteristics to read
         *
         * @param gatt Gatt client invoked {@link BluetoothGatt#discoverServices}
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the remote device
         *               has been explored successfully.
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){

            // Check to see if services got discovered successfully
            if (status == BluetoothGatt.GATT_SUCCESS) {
                sendBroadcast(ACTION_GATT_SERVICES_DISCOVERED);

                // Loop through the services and queue characteristics to read
                for(BluetoothGattService service : gatt.getServices()){
                    if(service.getUuid().equals(UUID_SERVICE)){
                        readGattCharacteristic(service.getCharacteristic(UUID_CHARACTERISTIC_POTENTIOMETER));
                        readGattCharacteristic(service.getCharacteristic(UUID_CHARACTERISTIC_BUTTONS));
                        readGattCharacteristic(service.getCharacteristic(UUID_CHARACTERISTIC_LEDS));
                    }else if(service.getUuid().equals(UUID_SERVICE_DEVICE_INFORMATION)){
                        readGattCharacteristic(service.getCharacteristic(UUID_CHARACTERISTIC_MODEL_NUMBER));
                        readGattCharacteristic(service.getCharacteristic(UUID_CHARACTERISTIC_SERIAL_NUMBER));
                    }
                }
            }
        }

        /**
         * Callback reporting the result of a characteristic read operation.
         *
         * Read the next characteristic on the queue if there are any left.
         *
         * @param gatt Gatt client invoked {@link BluetoothGatt#readCharacteristic}
         * @param characteristic Characteristic that was read from the associated
         *                       remote device.
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the read operation
         *               was completed successfully.
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
            if (status == BluetoothGatt.GATT_SUCCESS) {
                sendBroadcast(ACTION_DATA_AVAILABLE);

                if(characteristic.getUuid().equals(UUID_CHARACTERISTIC_POTENTIOMETER) || characteristic.getUuid().equals(UUID_CHARACTERISTIC_BUTTONS)){
                    registerCharacteristic(characteristic);
                } else if(characteristic.getUuid().equals(UUID_CHARACTERISTIC_LEDS)){
                    ledsCharacteristic = characteristic;
                } else if(characteristic.getUuid().equals(UUID_CHARACTERISTIC_MODEL_NUMBER)){
                    modelNumber = BleUtils.bytesToDecodedHexString(characteristic.getValue());
                } else if(characteristic.getUuid().equals(UUID_CHARACTERISTIC_SERIAL_NUMBER)){

                    String serial = BleUtils.bytesToDecodedHexString(characteristic.getValue());
                    if(serial != null && serial.length() > 6){
                        serial = serial.replace(":", "");
                        serial = serial.substring(serial.length() - 6, serial.length());
                    }

                    DataStore.persistBleDeviceUuid(BleService.this, serial);
                    sendBroadcast(ACTION_UPDATE_UUID_UI);
                }

                readCharacteristicQueue.remove();
                if(readCharacteristicQueue.size() > 0){
                    gatt.readCharacteristic(readCharacteristicQueue.element());
                }else if(descriptorQueue.size() > 0){
                    bluetoothGatt.writeDescriptor(descriptorQueue.element());
                }
            }
        }

        /**
         * Callback triggered as a result of a remote characteristic notification.
         *
         * @param gatt Gatt client the characteristic is associated with
         * @param characteristic Characteristic that has been updated as a result
         *                       of a remote notification event.
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
            if(characteristic.getUuid().equals(UUID_CHARACTERISTIC_BUTTONS)){
                String buttons = BleUtils.bytesToHexString(characteristic.getValue());
                updateButtonChanges(buttons);
            }else if(characteristic.getUuid().equals(UUID_CHARACTERISTIC_POTENTIOMETER)){
                String value = BleUtils.bytesToHexString(characteristic.getValue());
                updatePotentiometerChanges(value);
            }
        }

        /**
         * Callback indicating the result of a descriptor write operation.
         *
         * Write the next queued up descriptor if any, if all descriptor have been written
         * read attempt to read the latest Server response and update the BLE device LEDs.
         *
         * @param gatt Gatt client invoked {@link BluetoothGatt#writeDescriptor}
         * @param descriptor Descriptor that was writte to the associated
         *                   remote device.
         * @param status The result of the write operation
         *               {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
         */
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            servicesRegisteredForNotification++;
            descriptorQueue.remove();
            if(descriptorQueue.size() > 0){
                bluetoothGatt.writeDescriptor(descriptorQueue.element());
            }

            if(servicesRegisteredForNotification == 2){
                // After all chars have been read and registered for notifications update LEDs
                // on the device.
                readLastServerResponseUpdateLeds();
            }
        }
    }

    /**
     * Attempt to read the latest server response and update the device's LEDs
     */
    private void readLastServerResponseUpdateLeds() {
        ResponseModel responseModel = ResponseProvider.getFirstNonPostResponse(BleService.this);
        Status status = responseModel != null
                ?  GGson.fromJson(responseModel.responseStatus, Status.class)
                : new Status();

        writeLeds(status);
    }

    /**
     * Attempt to write LEDs status to the BLE Device
     *
     * @param status Object that contains the LED Status
     */
    public void writeLeds(Status status){
        if(bluetoothAdapter != null
                && bluetoothGatt != null
                && isConnected()
                && ledsCharacteristic != null
                && !DataStore.getUserPostInProgress(BleService.this)){

            DataStore.persistBleStatusLeds(BleService.this, status);

            String led1 = status.getLed1() ? "1" : "0";
            String led2 = status.getLed2() ? "1" : "0";
            String led3 = status.getLed3() ? "1" : "0";
            String led4 = status.getLed4() ? "1" : "0";

            byte[] value = BleUtils.stringToHexByteArray(led1 + led2 + led3 + led4);
            ledsCharacteristic.setValue(value);
            bluetoothGatt.writeCharacteristic(ledsCharacteristic);
        }
    }

    /**
     * Register to receive notifications from a particular Bluetooth Gatt Characteristic
     *
     * @param characteristic Bluetooth Gatt Characteristic to register to
     */
    private void registerCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            return;
        }
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID_CHARACTERISTIC_GENERIC);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        writeGattDescriptor(descriptor);
        bluetoothGatt.setCharacteristicNotification(characteristic, true);
    }

    /**
     * Add characteristic to read queue, if there is only 1 characteristic on the queue start
     * reading the characteristic value.
     *
     * @param characteristic Bluetooth characteristic
     */
    public void readGattCharacteristic(BluetoothGattCharacteristic characteristic){
        readCharacteristicQueue.add(characteristic);
        if(readCharacteristicQueue.size() == 1){
            bluetoothGatt.readCharacteristic(characteristic);
        }
    }

    /**
     * Add Bluetooth Gatt descriptor to a queue to register to receive notifications
     *
     * @param descriptor Bluetooth Gatt Descriptor
     */
    public void writeGattDescriptor(BluetoothGattDescriptor descriptor){
        //put the descriptor into the write queue
        descriptorQueue.add(descriptor);
    }

    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    public void close() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    /**
     * Update persisted BLE Device Status and POST status update to the server
     *
     * @param buttons String value that contains the button status returned from the BLE Device
     */
    private void updateButtonChanges(String buttons) {

        // Get Latest status and update it.
        Status status = new Status();
        if(DataStore.getBleStatus(BleService.this) != null){
            status = DataStore.getBleStatus(BleService.this);
        }

        status.setButton1(buttons.charAt(0) == '1');
        status.setButton2(buttons.charAt(1) == '1');
        status.setButton3(buttons.charAt(2) == '1');
        status.setButton4(buttons.charAt(3) == '1');
        status.setDeviceType(ResponseModel.getDeviceType(modelNumber));
        status.setUuid(DataStore.getBleDeviceUuid(BleService.this));

        DataStore.persistBleStatus(BleService.this, status);
        sendBroadcast(ACTION_UPDATE_UI);

        if(!DataStore.getAwsAmi(BleService.this).isEmpty())
            Api.postStatus(BleService.this, status);
    }

    /**
     * Update persisted BLE Device Status and POST status update to the server
     *
     * @param potentiometer String value that contains the potentiometer value returned from the BLE Device
     */
    private void updatePotentiometerChanges(String potentiometer){
        // Get Latest status and update it
        Status status = new Status();
        if(DataStore.getBleStatus(BleService.this) != null){
            status = DataStore.getBleStatus(BleService.this);
        }

        status.setPotentiometer(Integer.valueOf(potentiometer));
        status.setDeviceType(ResponseModel.getDeviceType(modelNumber));
        status.setUuid(DataStore.getBleDeviceUuid(BleService.this));

        DataStore.persistBleStatus(BleService.this, status);
        sendBroadcast(ACTION_UPDATE_UI);

        if(!DataStore.getAwsAmi(BleService.this).isEmpty())
            Api.postStatus(BleService.this, status);
    }

    /**
     * Send Broadcast Action Updates to Main Activity
     *
     * @param action Action Integer Value
     */
    private void sendBroadcast(int action){
        Intent intent = new Intent(BROADCAST);
        intent.putExtra(ACTION_ID, action);
        broadcast.sendBroadcast(intent);
    }

    /**
     * Start BLE Crash Resolver
     */
    private void startBluetoothCrashResolver(){
        try{
            ((MainApp) getApplication()).getBluetoothCrashResolver().start();
        }catch (IllegalArgumentException e){
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Stop BLE Crash Resolver
     */
    private void stopBluetoothCrashResolver() {
        try{
            ((MainApp) getApplication()).getBluetoothCrashResolver().stop();
        }catch (IllegalArgumentException e){
            Log.e(TAG, e.toString());
        }
    }
}
